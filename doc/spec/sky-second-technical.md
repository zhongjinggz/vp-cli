# 版本“天二”技术演进

- 改用 Tinylog
- 引入 picocli
- 引入 JSpecify
- 可以静态化的方法抽取成静态工具

## 改用 Tinylog
- 状态：已完成

> 依据官方文档（tinylog 2.8.1，当前最新稳定版）：
> [Getting Started](https://www.tinylog.org/getting-started/)
> [Logging](https://www.tinylog.org/logging/)
> [Configuration](https://www.tinylog.org/configuration/)
> [Javadoc](https://www.tinylog.org/javadoc/org/tinylog/configuration/Configuration.html)

### 目标与约束

把日志后端从 logback 换成 Tinylog 2，**尽量不改变目前程序行为**，并保持改动面最小：

- 保留“双 profile”行为：未指定 `-Dvpcli.log.profile` 时默认 `dev` → 输出到**控制台**；`prod` → 输出到**插件安装目录下 `log/`** 的滚动文件。
- 保留日志级别：当前 root 为 `TRACE`（控制台与文件一致）。
- 保留输出行格式**语义**：时间戳 + 级别 + 简单类名 + 消息。
- 保留 SLF4J 调用面：现源码有 13 处用 `org.slf4j.LoggerFactory`/`org.slf4j.Logger`（`DocWriter`、`MyIO`、`CLIController`、`ViElementType`、各个 `Puml*Exporter` 等）。为不改动这些调用点、大幅降低回归面，**保留 `slf4j-api`，通过官方绑定 `slf4j-tinylog` 把 SLF4J 桥接到 Tinylog**。源码日志调用一行不改。

### 关键设计决策：配置时机与“配置冻结”、避免静态块

官方文档（Javadoc `Configuration.set/replace`）明确：

> “Configuration properties must be set before calling any logging methods. 一旦日志初始化，配置即不可变，之后的修改会抛 `UnsupportedOperationException`。”

因此 Tinylog 的全部配置必须在**首次触及 tinylog（创建/取得 logger、写日志、或 `Configuration.get()`）之前**完成，且一旦配置后不可再重载（logback 允许运行时重载，Tinylog 不允许）。

为**避免静态块**（便于理解与测试），采用“显式初始化 + 惰性持有者”：

1. 在 `VPCLI` 中**删除静态块**，并将所有会触发日志初始化的静态字段——`LOG` 与 Dagger 组件 `component`——改为**惰性**（惰性持有者 Holder / 双重检查 getter），使它们**不再随类加载而初始化**。
2. 关键点：`component = DaggerVPCliComponent.create()` 若仍是静态字段，会在类加载时实例化整棵依赖树、加载所有依赖类并初始化它们的静态 logger，从而在 `loaded()` 之前就冻结了 Tinylog。因此**`component` 必须一并惰性化**，否则 `configureLogging` 会抛 `UnsupportedOperationException`。
3. 惰性化后，类加载时不接触 tinylog；**首次触及 tinylog 的时机**被推迟到显式调用 `configureLogging(pluginInfo)` 的那一刻。
4. `loaded(pluginInfo)` 作为 VP 生命周期回调最先执行，其**第一句**就调用 `configureLogging(pluginInfo)`——此时已拿到插件目录（`pluginInfo.getPluginDir()`），且尚未创建任何 logger，可安全地一次性完成全部配置。
5. 配置完成后，后续 `log()`/`component` 惰性初始化才首次创建 logger，路由到已配置的 Tinylog。
6. `configureLogging` 是普通方法，可接收 `pluginInfo`/目录参数、便于单元测试（可传 mock 或注入目录）。

#### 稳健性分析：删除静态块对 Dagger 与 Tinylog 的影响

采用惰性化后，删除静态块的改动是**稳妥**的，理由如下：

- **Dagger**：真正会在类加载时触发日志初始化的，是 `component = DaggerVPCliComponent.create()` 这个**静态字段**（类初始化时即实例化整棵依赖树、加载各 `@Singleton` 依赖类，从而初始化它们的静态 logger，如 `DocWriter`、`CLIController` 的 `private static final Logger = LoggerFactory.getLogger(...)`）。改惰性 Holder 后，该字段的求值推迟到**首次访问 `component()`** 时。`DaggerVPCliComponent.create()` 自身无静态副作用，因此依赖树及其静态 logger 必然晚于 `loaded()` 才加载。仅删静态块而**不**惰性化 `component` 将不足以保证顺序——这正是方案把 `LOG` 与 `component` **同时**惰性化、并在关键决策第 2 点显式声明的原因。
- **Tinylog**：`Configuration.replace(cfg)` 只需在首次创建/取得 logger 之前调用，`loaded()` 作为 VP 生命周期第一回调、其首句即调用，时机满足。
  - **前提/风险**：tinylog 配置按 classloader 全局冻结。若该 plugin classloader 下**先于 `loaded()`** 已有任何 tinylog logger 被创建（例如 VP 容器含 Tinylog 2、或其它类抢先触发 slf4j 桥），`Configuration.replace` 会因配置已冻结抛 `UnsupportedOperationException`。本项目 VP 一般不使用 Tinylog 2，且组件已惰性化，前置风险低；实施时应确认该前提成立（见步骤 5 验证）。
- **测试性**：`configureLogging` 改为普通方法、可注入目录/mock `pluginInfo`，静态块原有的“隐式时机、难注入、难测”问题随之消除。改造会**破坏既有 `VPCLITest`**（它断言 `loaded(null)` 后 `vpcli.log.appender=CONSOLE`、`vpcli.log.dir` 为 null，见 src/test/java/vpcli/plugin/VPCLITest.java 10–34 行），因此须先在前期步骤同步更新/新增测试（见下节“单元测试先行”）。

### 单元测试先行（前期步骤）

本仓库**已有** JUnit 5 + Mockito 测试基础设施（`junit-jupiter`/`mockito-core`，test scope；约 20 个测试类）。迁移 Tinylog 是“行为对齐”改动，应**用单测先行锁定现有行为、防止回归**，而不是改完再补测。将 `src/main/java/vpcli/plugin/VPCLI.java` 的改造拆成可测的小步：

1. **先跑通现有测试基线**：`mvn test`（或 `mvn -q -Dtest=VPCLITest test`）应绿，作为改造前基线。
2. **更新既有 `VPCLITest`**（因 `configureLogging` 从“写系统属性 + 重载”改为“`Configuration.replace` 建 cfg 映射”，旧断言不再成立）：
   - 删除对 `vpcli.log.appender` 的断言（Tinylog 不再有该属性）。
   - 保留/改写 `loaded(null)` 相关测试；`VPCLI.configureLogging` 改为包可见普通方法后，可直接调用并断言其内部行为。
3. **新增 `VPCLIConfigurationTest`（核心回归测试）**，把 `configureLogging` 视作可注入的纯逻辑，验证其产出的配置映射与预期一致（不依赖真实 tinylog-impl 冻结，故稳定）：
   - `configureLogging(devPluginInfo)` → writer=console、level=trace、format 含 `{date:HH:mm:ss.SSS}` 与 `{class-name}`，且**不含** `writer.latest`、不设 `writer.stream`（确认采用 Tinylog 默认流划分）。
   - `configureLogging(prodPluginInfo)`（mock `pluginInfo.getPluginDir()` 返回临时目录）→ writer=rolling file、`writer.file` 以临时目录 + `vpcli.{date:yyyy-MM-dd}.{count}.log` 结尾、**不设** `writer.latest`（避免生成链接到实际滚动文件的 `vpcli.log`，保证只产物一个日志文件）、`writer.policies=size: 5mb, daily`、`writer.backups=30`、level=trace。
   - `resolvePluginLogDir` 三个分支各一用例：`pluginInfo` 可用的取插件目录；无 pluginInfo 但有 `-Dvpcli.log.dir` 的系统属性时取该值；两者皆无时兜底当前目录 `log/`。
   - **顺序保护用例**：断言惰性化后，在 `configureLogging` 执行**之前**，`VPCLI` 类的“首次触发日志”句柄尚未被触及 —— 具体做法：先调用被改造的 `configureLogging`，在测试内 assert 无 `UnsupportedOperationException`，以验证“配置早于首次 logger”（即类加载未抢跑、两个 Holder 均惰性生效）。
4. **执行** `mvn -q -Dtest=VPCLIConfigurationTest,VPCLITest test`，两步全绿后再进入下述改造。
5. 上述改造完成后，`mvn test` 全量回归（既有 20 个测试 + 新增）保持全绿。

> 说明：单测锁定的是 `configureLogging` 产出的**配置映射**与**顺序约束**；真实文件写出/滚动行为仍由步骤 5 的 GUI/CLI 手动验证兜底。目标是把迁移的**行为等价性**尽量下沉到可在 CI 里反复跑的单元测试。

### 实施步骤

#### 步骤 1：调整 pom.xml 依赖（logback → tinylog）

当前 `pom.xml`：有属性 `logback.version`，有依赖 `org.slf4j:slf4j-api` 与 `ch.qos.logback:logback-classic`。

改动：

1. 删除属性 `<logback.version>1.6.3</logback.version>`，新增 `<tinylog.version>2.8.1</tinylog.version>`。
2. **删除**依赖 `ch.qos.logback:logback-classic`。
3. **保留**依赖 `org.slf4j:slf4j-api`（源码仍用 SLF4J API，必备）。
4. **新增**依赖（三者同版本 `2.8.1`）：
   - `org.tinylog:tinylog-api`（门面）
   - `org.tinylog:tinylog-impl`（实现，必须一并打包）
   - `org.tinylog:slf4j-tinylog`（SLF4J 绑定；作用是把 `LoggerFactory.getLogger()` 的调用桥接到 Tinylog。**缺少它 slf4j-api 将无绑定并回退 NOP、打印 “Failed to load class…并对真正的日志生产环境不可接受**）
5. 三者为运行时依赖，会经 `assembly` 打进插件 `lib/`，无需额外处理 scope。

#### 步骤 2：替换日志配置文件

1. **删除** `src/main/resources/logback.xml`。
2. **新增** `src/main/resources/tinylog.properties`，作为**唯一日志配置源**，同时承载 dev 与 prod 两组配置（通过 `dev.*`/`prod.*` 前缀区分，公共项无前缀）；也作为启动早期兜底：

   ```properties
   # 公共项（dev/prod 同时生效）：级别与行格式
   writer.level  = trace
   writer.format = {date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}

   # dev：控制台输出
   dev.writer = console

   # prod：滚动文件输出（pattern 需含 placeholders；writer.file 由代码拼接日志目录）
   prod.writer = rolling file
   prod.writer.file.pattern = vpcli.{date:yyyy-MM-dd}.{count}.log
   prod.writer.policies = size: 5mb, daily
   prod.writer.backups = 30
   ```

   - 所有可改参数写在配置文件里，改 `tinylog.properties` 即可调整，**不在代码中硬编码**；`VPCLI#configureLogging` 只负责按 profile 读取对应分组并 apply。
   - `writer.level = trace`：对齐原 root=`TRACE`（Tinylog 默认其实是 `trace`，但显式写出避免歧义）。
   - **console 流采用 Tinylog 默认行为**：官方 console writer 默认把 `warn`/`error` 写到 `System.err`、其余写到 `System.out`。不再固定 `writer.stream`，接受这一默认流划分（原 logback `ConsoleAppender` 无 target 时全量写 `System.out`，此为**有意的行为差异**，Tinylog 默认行为更合理）。
   - `{class-name}` = 原 `%logger{0}`（简单类名）；`{level:min-size=5}` = 原 `%-5level`（右补空格到 5 位）；`{date:HH:mm:ss.SSS}` = 原 `%d{HH:mm:ss.SSS}`；`{message}` = 原 `%msg`；结尾隐式换行，无需 `%n`。
3. **修改** `assembly/plugin-zip.xml`：`classes fileSet` 的 `<includes>` 把 `<include>logback.xml</include>` 改为 `<include>tinylog.properties</include>`，确保打进插件 zip。

#### 步骤 3：改造 `VPCLI.java` 的程序化配置（核心）

当前 `VPCLI` 相关部分（16–72 行）依赖 logback 且重用静态块：

- 静态块（21–24 行）：据 profile 设置 `vpcli.log.appender`。
- 静态字段（26–29 行）：`LOG` 与 `component`（Dagger 组件，类加载即实例化依赖树）。
- `configureLogging(VPPluginInfo)`（41–61 行）：设 appender、算插件目录 `log/` 并设 `vpcli.log.dir`，prod 时 `reloadLogbackConfiguration()`。
- `reloadLogbackConfiguration()`（64–72 行）：logback 特有。

改动：

1. **删除** logback 相关导入：
   - `ch.qos.logback.classic.LoggerContext`
   - `ch.qos.logback.classic.util.ContextInitializer`
   - `ch.qos.logback.core.joran.spi.JoranException`
2. **新增**导入：
   - `org.tinylog.configuration.Configuration`、`org.tinylog.Level`
3. **删除静态块**（21–24 行）。Tinylog 由显式调用配置，无需静态预置。
4. **惰性化静态字段**，避免类加载时初始化：

   ```java
   // LOG 与 component 均改为惰性持有者：类加载时不做任何初始化，
   // 确保 loaded() 前不会触碰 tinylog（日志配置因此可在 loaded() 内安全完成）。

   private static final class LogHolder {
       private static final org.slf4j.Logger INSTANCE =
           org.slf4j.LoggerFactory.getLogger(VPCLI.class);
   }
   private static org.slf4j.Logger log() {
       return LogHolder.INSTANCE;
   }

   private static final class ComponentHolder {
       private static final VPCliComponent INSTANCE = DaggerVPCliComponent.create();
   }
   private static VPCliComponent component() {
       return ComponentHolder.INSTANCE;
   }
   ```

   - `LogHolder`（惰性持有者）确保首次创建 logger 的时机推迟到真正写日志时，而那时 Tinylog 已完成配置。
   - `ComponentHolder` 确保 `DaggerVPCliComponent.create()` 及其依赖树（各类的静态 logger）不在类加载时初始化，而仅在 `invoke()`/实际使用时才触发。
5. **改写 `loaded()`**：作为 VP 生命周期第一回调，其首句调用 `configureLogging(pluginInfo)`：

   ```java
   private static final String FORMAT =
       "{date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}";

   @Override
   public void loaded(VPPluginInfo pluginInfo) {
       configureLogging(pluginInfo);
   }

   void configureLogging(VPPluginInfo pluginInfo) {   // 普通方法，便于测试（可 mock pluginInfo / 注入目录）
       String profile = System.getProperty("vpcli.log.profile", "dev");
       boolean prod = "prod".equalsIgnoreCase(profile);
       File logDir = prod ? resolvePluginLogDir(pluginInfo) : null;
       Configuration.replace(buildLoggingConfig(loadLoggingProperties(), prod, logDir));
   }

   // 加载 classpath 根下的 tinylog.properties（与 Tinylog 自动发现的资源同名，作为唯一配置源）
   Properties loadLoggingProperties() {
       Properties props = new Properties();
       try (InputStream in = VPCLI.class.getResourceAsStream("/tinylog.properties")) {
           if (in != null) props.load(in);
       } catch (IOException e) {
           throw new IllegalStateException("无法加载日志配置文件 tinylog.properties", e);
       }
       return props;
   }

   // 从配置文件构造传给 Tinylog 的 writer.* 映射（纯函数，便于单测锁定；
   // prod 时将动态解析出的插件日志目录拼入 writer.file）
   Map<String, String> buildLoggingConfig(Properties props, boolean prod, File logDir) {
       String prefix = prod ? "prod." : "dev.";
       Map<String, String> cfg = new LinkedHashMap<>();
       cfg.put("writer", props.getProperty(prefix + "writer"));
       cfg.put("writer.level", props.getProperty("writer.level"));   // 公共项
       cfg.put("writer.format", props.getProperty("writer.format")); // 公共项
       if (prod) {
           cfg.put("writer.file", new File(logDir,
               props.getProperty(prefix + "writer.file.pattern")).getAbsolutePath());
           cfg.put("writer.policies", props.getProperty(prefix + "writer.policies"));
           cfg.put("writer.backups", props.getProperty(prefix + "writer.backups"));
       }
       return cfg;
   }
   ```

   - 所有可改参数（writer 类型、级别、格式、滚动策略、备份数、文件名模式）均**来自配置文件**，代码不硬编码；dev/prod 通过 `dev.*`/`prod.*` 前缀在同一份 `tinylog.properties` 中分组，公共项（级别/格式）无前缀。
   - `Configuration` 的属性名**不带** `tinylog.` 前缀（与 properties 文件一致），这点与 logback 的 `system property` 用法不同，见官方 Javadoc。
   - **`resolvePluginLogDir(pluginInfo)`**：prod 下写入“插件安装目录下 `log/`”。现在能拿到 `pluginInfo`，直接按序解析（取首个成功者）：
     1. `pluginInfo.getPluginDir()` → `new File(pluginDir, "log")`，`mkdirs()`（与原 47–53 行逻辑一致）；
     2. 系统属性 `vpcli.log.dir`（若调用方/启动脚本注入）；
     3. 兜底：当前工作目录下 `log/`。
6. **删除** `reloadLogbackConfiguration()`（Tinylog 无需重载）。
7. **改写 `invoke(args)`**：`component()` 改为惰性 getter 调用：

   ```java
   @Override
   public void invoke(String[] args) {
       CLIController cliController = component().cliController();
       try {
           cliController.invoke(args);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
   ```

8. 原 `LOG` 静态字段的使用处改为 `log().xxx(...)`（仅 `VPCLI` 一处，`reloadLogbackConfiguration` 已删除后即无其它使用）。
9. `unloaded()` 保持现状。

> 参数校验/错误处理等与本次无关的逻辑不动。

#### 步骤 4：格式串与滚动策略映射（行为对齐）

原 logback pattern：`%d{HH:mm:ss.SSS} %-5level %logger{0} - %msg%n`

| logback | Tinylog（官方占位符） | 说明 |
| --- | --- | --- |
| `%d{HH:mm:ss.SSS}` | `{date:HH:mm:ss.SSS}` | 时间戳，格式不变 |
| `%-5level` | `{level:min-size=5}` | 右补空格到 5 位，等价 |
| `%logger{0}` | `{class-name}` | 简单类名（不带包名），等价 |
| `%msg` | `{message}` | 消息 |
| `%n` | （隐式） | Tinylog 每条自动换行 |

滚动策略（当前 logback → Tinylog 映射与偏差）：

| logback | Tinylog | 说明 |
| --- | --- | --- |
| 尺寸 5MB 滚动 + 每日滚动 | `policies = size: 5mb, daily` | 对齐 |
| 活动文件 `vpcli.log` | （不生成 `writer.latest`） | 用户期望只产物一个日志文件；滚动文件 writer 仅保留带占位符的实际文件 `vpcli.{date}.{count}.log`。若需固定名链接可自行开启 `writer.latest`，但会产生指向最新文件的链接 |
| 备份命名 `vpcli.%d.%i.log` | `vpcli.{date:yyyy-MM-dd}.{count}.log` | 命名不同，属可接受的偏差（不影响“读取到日志文件”行为） |
| `maxHistory=30` | `writer.backups = 30` | 对齐“保留最近 30 份” |
| `totalSizeCap=50MB` | （官方滚动文件 writer 无 totalSizeCap） | **已知偏差**：Tinylog 无总大小上限配置，改用 `backups=30` 兜底；在实施时确认锁定版本是否新增该能力，若无则以 30 份为准 |

其它行为对齐：

- **启动日志**：logback init 会打印内部状态（原用 `NopStatusListener` 静音）；Tinylog 不打印此类启动日志，故无需等价配置，启动输出更干净（不影响业务行为）。
- **console stream**：采用 Tinylog 默认的流划分（warn/error → `System.err`，其余 → `System.out`），不固定 `writer.stream`。与原 logback 全量 `System.out` 相比属**有意的行为差异**。

#### 步骤 5：真实环境验证（GUI/CLI 手动；单元测试已在上文“单元测试先行”完成）

1. `mvn clean package` 成功；检查 `target/plugins.vpcli.zip`：`lib/` 含 `tinylog-api`、`tinylog-impl`、`slf4j-tinylog`，且**不再**含 `logback-classic`；`classes/` 含 `tinylog.properties`、不含 `logback.xml`。
2. **dev（GUI 默认）**：不设 `-Dvpcli.log.profile`，安装插件后触发日志（如 `CLIController.performExport` 的 `info`），确认输出到控制台、行格式与原 logback 一致。
3. **prod（CLI）**：通过脚本（`VpCliCommon` 已传 `-Dvpcli.log.profile=prod`）执行一次导出，确认 `log/vpcli.log`（latest 链接）在插件目录被创建并写入；`info`/`warn`/`trace` 行格式与原文件一致，且 TRACE 级确实被记录（验证 `writer.level=trace` 生效）。
4. 回归抽查日志点较集中的文件：`MyIO.writeFile` 的 `debug`、`ViElementType.of` 与 `PumlElementExporterRepository.findBy` 的 `warn`、`PumlUnimplementedExporter.export` 的 `warn`，确认均正常输出。

### 涉及文件清单

- `pom.xml`（依赖/属性）
- `src/main/resources/logback.xml`（删除）
- `src/main/resources/tinylog.properties`（新增）
- `assembly/plugin-zip.xml`（`include` 由 logback.xml 改为 tinylog.properties）
- `src/main/java/vpcli/plugin/VPCLI.java`（**删除静态块**；`LOG` 与 Dagger `component` 静态字段改为惰性持有者；`loaded()` 调用 `configureLogging(pluginInfo)`；删除 `reloadLogbackConfiguration()`；`invoke()` 改用惰性 `component()`）
- `src/test/java/vpcli/plugin/VPCLITest.java`（更新：移除对 `vpcli.log.appender` 的断言）
- `src/test/java/vpcli/plugin/VPCLIConfigurationTest.java`（新增：锁定 `configureLogging` 产出的配置映射、`resolvePluginLogDir` 三分支与配置顺序约束）

> 其余 12 处用 SLF4J 的源码文件**不改动**。

## 引入 picocli
### 需求
- 引入 Picocli 完成 `/Users/zhongjing/Projects/mine/vp-cli/src/main/java/vpcli/shell/VPCLIShell.java`
- `VPCLIShell.java` 将覆盖 `VpExport.java` `VpListDiagrams.java` `VpTree.java` 的所有功能
- `VPCLIShell.java` 中的代码全部重新写，可以参考或拷贝 `/Users/zhongjing/Projects/mine/vp-cli/src/main/java/vpcli/shell` 中原来的代码实现，但不要引用这个包中原来的代码，这个包中原来的代码最终会被删除
- 原来的脚本会相应修改成下面的样子：
  - vp-export:  java -cp "$(dirname "${BASH_SOURCE[0]}")/../classes" vpcli.shell.VPCLIShell export "$@"
  - vp-tree:  java -cp "$(dirname "${BASH_SOURCE[0]}")/../classes" vpcli.shell.VPCLIShell tree "$@"
  - vp-list-diagrams:  java -cp "$(dirname "${BASH_SOURCE[0]}")/../classes" vpcli.shell.VPCLIShell list-diagrams "$@"
  - 注意，上面三条的改动意味着，同一个程序 `VPCLIShell`,依靠第一个参数（export, tree, list-diagrams） 来区分具体实现什么功能
- 为了满足上一条需求，估计要使用 Picocli 的子命令功能，这个需要你确认一下。
- **确认：采用 picocli 子命令（`@Command(subcommands=...)`）+ `CommandLine.execute`**。三个子命令 `export`/`tree`/`list-diagrams` 在同一个 `VPCLIShell` 下注册，同一个进程内靠**第一个位置参数**（子命令名）分派，恰好满足脚本只需改 `vpcli.shell.VPCLIShell export|tree|list-diagrams` 的需求。picocli 自动处理子命令解析、缺失必填参数的报错与 usage 帮助，可整体替换手写的 `CLIParams`/`VpCliCommon` 参数拼装。

### 目标与约束

- **行为不变**：三个命令最终仍是“解析参数 → 拼接参数 → 调用 VP 插件 (`com.vp.cmd.Plugin`) 并将插件退出码透传为进程退出码”。只替换**参数解析与命令分派**这一层，不替换深层的 `ExportService`/`TreeService`/`ListDiagramsService`。
- **不引用旧包代码**：`VPCLIShell.java` 全部重写，不得依赖（import/复用）`vpcli.shell` 下原有的 `VpExport`、`VpTree`、`VpListDiagrams`、`CLIParams`、`VpCliCommon`——这些文件最终会被删除。其核心逻辑（读 `vpcli.properties`、切目录、启动进程）需**重新实现**进新骨架。
- **只动 shell 层**：`VPCLIShell` 只负责 CLI 解析 + 拼参 + 启动 VP 插件。`vpcli/shell` 之外（如 `vpcli/plugin/drivingadapter/CLIParams.java`、`CLIController.java`）**不改**——那里仍是插件侧真正执行 `export/tree/list-diagrams` 的 `-action` 分发点。
- **Java 11 兼容**：选中一个支持 Java 8+ 的 picocli 4.x 版本（本仓库编译目标 `maven.compiler.release=11`），不引入需要更高 JDK 的 API。

### 关键设计决策

1. **一个入口类 + 三个子命令类，`@TopLevel` 命令无参数**。`VPCLIShell` 用 `@Command(subcommands = {ExportCommand.class, TreeCommand.class, ListDiagramsCommand.class})` 声明为纯容器；真正的参数都挂在子命令上。脚本改指针后，`VPCLIShell export ...` / `VPCLIShell tree ...` 直接命中对应子命令。
2. **子命令通过参数拼成兼容旧 `-action/-path/-target` 的参数**（保持插件侧 `CLIParams`/`CLIController` 零改动）。即：
   - `export -project <prj> -target <n> -path <out>` → 插件参数 `-action export -target <n> -path <out>`（`-target` 未给则用默认 `all`）
   - `tree -project <prj>` → 插件参数 `-action tree`
   - `list-diagrams -project <prj>` → 插件参数 `-action list-diagrams`
   - **参数分两层**（与旧 `VpCliCommon.runVpPlugin` 一致）：
     - **`-project` 是 VP 进程参数**：附加在 `com.vp.cmd.Plugin` 之后、`-pluginargs` 之前（旧实现 80–82 行 `add("-project"); add(project); add("-pluginargs")`），用于本进程/VP 定位工程文件（`new File(params.getProject()).getCanonicalPath()`）。
     - **`-action/-path/-target` 是插件参数**：放在 `-pluginargs` 之后（旧实现 83–88 行），由 VP 插件侧 `CLIParams`/`CLIController` 读取并分派。
3. **进程与退出码语义与旧实现一致**：沿用旧 `runVpPlugin` 流程——“拼接 java 启动命令 → `new ProcessBuilder(command).directory(appBin).inheritIO().start()` → `waitFor()` → 以插件退出码作为本进程退出码”。picocli 只负责解析并调用子命令，把 `call()` 的返回值映射为 `main` 的退出码。
4. **`vpcli.properties` 读取逻辑放进 `VpCliLauncher`**（原在 `VpCliCommon`）：从 classpath 根读 `vp.app`，失败回退默认 `/Applications/Visual Paradigm.app/Contents/Resources/app`。`-Dvpcli.log.profile=prod`、`--add-exports`、`-Xms/-Xmx`、`-Djava.awt.headless=true` 等 JVM 参数也在这里拼入启动命令。
5. **经确认的清单一处偏差**：旧 `CLIParams` 中 `-target` 虽在 `validateParams` 里被 `export` 检查，但 shell 层 `VpExport` 用 `setAction` 后直接 `runVpPlugin`，**未设 `-target` 时不会报错**（`CLIParams` 里 `-target` 根本没放进 `keyValueParams`，见 42–43 行）。因此本方案**不在 shell 层强制 `-target`**，沿用 `<n>` 缺省 `all` 的宽松语义；仅用 picocli `required=true` 强制真正必填的 `-project`（三个子命令都要）与 `export` 的 `-path`。缺失时 picocli 自动报错并打印 usage，退出码用其参数错误常量（`2`）。
6. **测试**：把“子命令字段 → 分层的进程/插件参数列表”的拼接抽成可测的纯方法（输入子命令字段值，返回 `List<String>`），用 JUnit 逐命令断言生成的参数序列及 `-project`/`-action` 所在层级，替代目前 `CLIParams` 的解析测试面。

### 实施步骤

#### 步骤 1：pom.xml 引入 picocli

在 `<properties>` 增加 `picocli.version`，在 `<dependencies>` 增加（compile scope，随 assembly 打进插件 `lib/`；如需精简也可 runtime）：

```xml
<property>
    <picocli.version>4.7.6</picocli.version>
</property>

<dependency>
    <groupId>info.picocli</groupId>
    <artifactId>picocli</artifactId>
    <version>${picocli.version}</version>
</dependency>
```

> 说明：4.7.6 是当前 4.x 稳定版，支持 Java 8+，满足 release=11。若实施时 Maven 中央库可解析到更高 4.x 小版本（如 4.7.7+），可更新为最新稳定版，但须保持 4.7.x 主版本号不变，避免引入 API 破坏性变更。

#### 步骤 2：重写 `VPCLIShell.java`（新骨架，自包含，不引用旧 shell 类）

结构（一个文件内可含多个类，便于独立成文件也可拆）：

```java
package vpcli.shell;

@Command(
    name = "vpcli",
    synopsisSubcommandLabel = "COMMAND",
    subcommands = {
        VPCLIShell.ExportCommand.class,
        VPCLIShell.TreeCommand.class,
        VPCLIShell.ListDiagramsCommand.class
    },
    mixinStandardHelpOptions = true,
    version = "vpcli 1.1.1"
)
public class VPCLIShell implements Runnable {

    @Override
    public void run() {
        // 无子命令时打印 usage；用 CommandLine(this).usage(out)
    }

    static int execute(String[] args) {
        return new CommandLine(new VPCLIShell()).execute(args);
    }

    public static void main(String[] args) {
        int exitCode = execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    // ---- 三个子命令 ----
    @Command(name = "export", mixinStandardHelpOptions = true,
             description = "导出模型到 PlantUML。")
    static class ExportCommand implements Callable<Integer> {
        @Option(names = "-project", required = true) String project;
        @Option(names = "-target", defaultValue = "all") String target;
        @Option(names = "-path", required = true) String path;

        @Override
        public Integer call() {
            return VpCliLauncher.launch(
                PluginArgs.of(project, List.of("export", target, path)));
        }
    }

    @Command(name = "tree", mixinStandardHelpOptions = true,
             description = "以树形输出模型结构。")
    static class TreeCommand implements Callable<Integer> {
        @Option(names = "-project", required = true) String project;

        @Override
        public Integer call() {
            return VpCliLauncher.launch(PluginArgs.of(project, List.of("tree")));
        }
    }

    @Command(name = "list-diagrams", mixinStandardHelpOptions = true,
             description = "列出工程中可用的图表。")
    static class ListDiagramsCommand implements Callable<Integer> {
        @Option(names = "-project", required = true) String project;

        @Override
        public Integer call() {
            return VpCliLauncher.launch(PluginArgs.of(project, List.of("list-diagrams")));
        }
    }

    /** 承载参数分层的纯值对象：project 是进程参数，actionArgs 是 -pluginargs 后的插件参数。 */
    static final class PluginArgs {
        final String project;
        final List<String> pluginArgs;   // 形如 List.of("export","DiagramA","out")
        private PluginArgs(String project, List<String> pluginArgs) {
            this.project = project;
            this.pluginArgs = pluginArgs;
        }
        static PluginArgs of(String project, List<String> pluginArgs) {
            return new PluginArgs(project, pluginArgs);
        }
    }
}
```

- 参数**分两层交付**给 `VpCliLauncher`（对应旧 `runVpPlugin` 的固定拼装顺序）：
  - **进程参数** `project`：启动命令中固定在 `com.vp.cmd.Plugin -pluginid ... -project <project> -pluginargs` 段（对齐旧实现 78–82 行）。
  - **插件参数** `pluginArgs`：在 `-pluginargs` 之后展开为 `-action export -target <t> -path <out>`（`export`）、`-action tree`、`-action list-diagrams`（对齐旧实现 83–88 行的固定顺序 `-action`→`-path`→`-target`）。
  - `PluginArgs` 仅示意传参形态；`VpCliLauncher` 内部负责把两层参数展开、插入固定前缀（`-action` 等）并拼成完整启动命令。
- **`VpCliLauncher`**（新建，替代旧 `VpCliCommon`）：负责“读 `vpcli.properties` 取 `vp.app` → 拼 java 启动命令（`java --add-exports java.desktop/com.apple.eawt=ALL-UNNAMED -Xms256m -Xmx768m -Djava.awt.headless=true -Dvpcli.log.profile=prod -cp ... com.vp.cmd.Plugin -pluginid com.github.zhongjinggz.vpcli -project <project> -pluginargs <action对应参数>`）→ `new ProcessBuilder(command).directory(appBin).inheritIO().start()` → `waitFor()` → 返回插件退出码”。启动器不引用旧 `VpCliCommon`/`CLIParams`。
- `main` 的退出码交给 `CommandLine.execute` 的返回值；`Callable.call()` 返回插件退出码，`System.exit(exitCode)` 透传，与旧脚本行为一致。

#### 步骤 3：修改三个脚本（.sh 与 .bat）

- `src/main/script/vp-export`、`vp-tree`、`vp-list-diagrams`：
  - `vp-export` → `java -cp "..." vpcli.shell.VPCLIShell export "$@"`
  - `vp-tree` → `... VPCLIShell tree "$@"`
  - `vp-list-diagrams` → `... VPCLIShell list-diagrams "$@"`
- 三处 `.bat` 同理改为 `vpcli.shell.VPCLIShell export %*` 等。

#### 步骤 4：删除旧的 shell 源文件

删除 `VpExport.java`、`VpTree.java`、`VpListDiagrams.java`、`CLIParams.java`、`VpCliCommon.java`（`vpcli/shell` 内仅保留新 `VPCLIShell` 与启动器文件）。删除前跑一遍单测确认无其它引用。

#### 步骤 5：测试

- `mvn clean package` 成功，且 `target/plugins.vpcli.zip` 的 `script/` 中三个脚本内容正确指向 `VPCLIShell`。
- 新增 JUnit 测试锁定 `commandArgs`（或等价拼参方法）对三个子命令生成的插件参数序列，及启动命令中 `-Dvpcli.log.profile=prod`、`com.vp.cmd.Plugin`、`-pluginid` 等关键片段。
- 手动验证：`./vp-export -project x.vpp -path out`、`./vp-tree -project x.vpp`、`./vp-list-diagrams -project x.vpp` 三者行为与改造前一致。
- 现有 `VPCLIExecutableTest`（vp-* 脚本可执行位）应保持全绿。

### 涉及文件清单

- `pom.xml`（新增 picocli 依赖/属性）
- `src/main/java/vpcli/shell/VPCLIShell.java`（重写为 picocli 顶层 + 三子命令入口）
- `src/main/java/vpcli/shell/VpCliLauncher.java`（新增：插件启动逻辑，替代旧 `VpCliCommon`）
- `src/main/script/vp-export`、`vp-tree`、`vp-list-diagrams`（入口类改 `VPCLIShell` + 子命令名）
- `src/main/script/*.bat`（三份，同步）
- `src/main/java/vpcli/shell/{VpExport,VpTree,VpListDiagrams,CLIParams,VpCliCommon}.java`（删除）
- `src/test/java/vpcli/shell/...Test.java`（新增：子命令拼参与启动命令断言）

> `vpcli/plugin/drivingadapter/{CLIParams,CLIController}.java` **不改**——插件侧 `-action` 分发保持现状，新 shell 只是把子命令映射回同样的 `-action` 参数。

