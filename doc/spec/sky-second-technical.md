# 版本“天二”技术演进

- 改用 Tinylog
- 引入 JSpecify
- 可以静态化的方法抽取成静态工具
- 引入 picocli

## 改用 Tinylog

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

### 关键设计决策：配置时机与“配置冻结”

官方文档（Javadoc `Configuration.set/replace`）明确：

> “Configuration properties must be set before calling any logging methods. 一旦日志初始化，配置即不可变，之后的修改会抛 `UnsupportedOperationException`。”

当前代码的做法是：在 `VPCLI` 的**静态块**里预置日志属性，然后在 `loaded()`（插件加载回调）拿到插件目录后再 `reloadLogbackConfiguration()` 重载一次，把 FILE appender 指向插件目录 `log/`。logback 允许运行时重载，因此可行；**Tinylog 不可重载**，所以：

1. Tinylog 的全部配置必须在**首次创建 logger 之前**完成。
2. `VPCLI` 的静态块恰好依次先于其自身 `LOG` 静态字段（21–24 行先于 26–27 行声明），且插件加载时 `VPCLI` 是入口类，其它 12 个类的静态 logger 都在其后初始化。因此把配置放在 **`VPCLI` 静态块**中是正确且充分的。
3. `loaded()` 里“拿插件目录→重载”的逻辑不再需要，因为无法重载；改为在静态块内通过系统属性直接解析日志目录（见步骤 3）。

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
2. **新增** `src/main/resources/tinylog.properties`，作为 dev 默认与启动早期兜底（在 `VPCLI` 静态块接管前的 console 输出）：

   ```properties
   writer        = console
   writer.level  = trace
   writer.format = {date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}
   writer.stream = out
   ```

   - `writer.level = trace`：对齐原 root=`TRACE`（Tinylog 默认其实是 `trace`，但显式写出避免歧义）。
   - `writer.stream = out`：**行为对齐**。官方 console writer 默认把 `warn`/`error` 写到 `System.err`、其余写 `System.out`；而原 logback `ConsoleAppender`（无 target）一律写 `System.out`。为不改变行为，显式固定为 `out`。
   - `{class-name}` = 原 `%logger{0}`（简单类名）；`{level:min-size=5}` = 原 `%-5level`（右补空格到 5 位）；`{date:HH:mm:ss.SSS}` = 原 `%d{HH:mm:ss.SSS}`；`{message}` = 原 `%msg`；结尾隐式换行，无需 `%n`。
3. **修改** `assembly/plugin-zip.xml`：`classes fileSet` 的 `<includes>` 把 `<include>logback.xml</include>` 改为 `<include>tinylog.properties</include>`，确保打进插件 zip。

#### 步骤 3：改造 `VPCLI.java` 的程序化配置（核心）

当前 `VPCLI` 相关部分（16–72 行）依赖 logback：

- 静态块（21–24 行）：据 profile 设置 `vpcli.log.appender`。
- `configureLogging(VPPluginInfo)`（41–61 行）：设 appender、算插件目录 `log/` 并设 `vpcli.log.dir`，prod 时 `reloadLogbackConfiguration()`。
- `reloadLogbackConfiguration()`（64–72 行）：logback 特有。

改动：

1. **删除** logback 相关导入：
   - `ch.qos.logback.classic.LoggerContext`
   - `ch.qos.logback.classic.util.ContextInitializer`
   - `ch.qos.logback.core.joran.spi.JoranException`
2. **新增**导入：
   - `org.tinylog.configuration.Configuration`、`org.tinylog.Level`
3. **重写静态块**，在首次 logger 之前完成全部配置：

   ```java
   static {
       String profile = System.getProperty("vpcli.log.profile", "dev");
       boolean prod = "prod".equalsIgnoreCase(profile);

       Map<String, String> cfg = new LinkedHashMap<>();
       String format = "{date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}";

       if (prod) {
           File logDir = resolvePluginLogDir();   // 见解析规则
           cfg.put("writer", "rolling file");
           cfg.put("writer.level", "trace");
           cfg.put("writer.format", format);
           cfg.put("writer.file", new File(logDir, "vpcli.{date:yyyy-MM-dd}.{count}.log").getAbsolutePath());
           cfg.put("writer.latest", new File(logDir, "vpcli.log").getAbsolutePath()); // 始终指向最新文件
           cfg.put("writer.policies", "size: 5mb, daily");
           cfg.put("writer.backups", "30");
       } else {
           cfg.put("writer", "console");
           cfg.put("writer.level", "trace");
           cfg.put("writer.format", format);
           cfg.put("writer.stream", "out");
       }
       Configuration.replace(cfg);   // 必须早于任何 getLogger()；静态块天然满足
   }
   ```

   - `Configuration` 的属性名**不带** `tinylog.` 前缀（与 properties 文件一致），这点与 logback 的 `system property` 用法不同，见官方 Javadoc。
   - **`resolvePluginLogDir()`**：prod 下写入“插件安装目录下 `log/`”。因静态块早于 `loaded()`、拿不到 `pluginInfo`，用两种方式解析（按序、取首个成功者）：
     1. 系统属性 `vpcli.log.dir`（若调用方/启动脚本已通过 `-Dvpcli.log.dir=...` 注入，例如 CLI 启动路径）；
     2. 由插件自身字节码位置推导：`new File(VPCLI.class.getProtectionDomain().getCodeSource().getLocation().toURI())` 得到 `…/com.github.zhongjinggz.vpcli/classes/`，取其 `getParentFile()` 即插件目录（VP 解压插件后 classes/ 与插件根目录同层）。指向该目录并 `mkdirs()`。
     3. 兜底：当前工作目录下 `log/`。
   - 两种 profile 共用统一 `format` 常量，保证行格式一致。
4. **删除** `configureLogging(VPPluginInfo)` 和 `reloadLogbackConfiguration()`。
5. **`loaded()` 改为空实现**（删除对 `configureLogging` 的调用），`unloaded()` 保持现状。
6. `LOG` 静态字段（26–27 行，`org.slf4j.LoggerFactory.getLogger(VPCLI.class)`）**保持不变**，经 `slf4j-tinylog` 绑定自然路由到 Tinylog。

> 若不想在静态块里做路径推导，可退化为：prod 的文件路径固定取 `System.getProperty("vpcli.log.dir", <default>)`，并把日志目录的注入责任交给各 CLI 启动入口（`VpCliCommon.runVpPlugin` 已会把 `-Dvpcli.log.profile=prod` 传给子进程，可在同一处补 `-Dvpcli.log.dir=...`）。二者取其一，方案以“静态块 + getProtectionDomain 推导”为主选，手动验证时确认其在 VP 安装目录下的实际取值。

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
| 活动文件 `vpcli.log` | `writer.latest = …/vpcli.log` | 用最新文件链接保持“vpcli.log 指向当前活动日志”的观测习惯 |
| 备份命名 `vpcli.%d.%i.log` | `vpcli.{date:yyyy-MM-dd}.{count}.log` | 命名不同，属可接受的偏差（不影响“读取到日志文件”行为） |
| `maxHistory=30` | `writer.backups = 30` | 对齐“保留最近 30 份” |
| `totalSizeCap=50MB` | （官方滚动文件 writer 无 totalSizeCap） | **已知偏差**：Tinylog 无总大小上限配置，改用 `backups=30` 兜底；在实施时确认锁定版本是否新增该能力，若无则以 30 份为准 |

其它行为对齐：

- **启动日志**：logback init 会打印内部状态（原用 `NopStatusListener` 静音）；Tinylog 不打印此类启动日志，故无需等价配置，启动输出更干净（不影响业务行为）。
- **console stream**：固定 `out` 以对齐 logback 全量 `System.out`（见步骤 2）。

#### 步骤 5：验证（手动，因本仓库无自动化测试）

1. `mvn clean package` 成功；检查 `target/plugins.vpcli.zip`：`lib/` 含 `tinylog-api`、`tinylog-impl`、`slf4j-tinylog`，且**不再**含 `logback-classic`；`classes/` 含 `tinylog.properties`、不含 `logback.xml`。
2. **dev（GUI 默认）**：不设 `-Dvpcli.log.profile`，安装插件后触发日志（如 `CLIController.performExport` 的 `info`），确认输出到控制台、行格式与原 logback 一致。
3. **prod（CLI）**：通过脚本（`VpCliCommon` 已传 `-Dvpcli.log.profile=prod`）执行一次导出，确认 `log/vpcli.log`（latest 链接）在插件目录被创建并写入；`info`/`warn`/`trace` 行格式与原文件一致，且 TRACE 级确实被记录（验证 `writer.level=trace` 生效）。
4. 回归抽查日志点较集中的文件：`MyIO.writeFile` 的 `debug`、`ViElementType.of` 与 `PumlElementExporterRepository.findBy` 的 `warn`、`PumlUnimplementedExporter.export` 的 `warn`，确认均正常输出。

### 涉及文件清单

- `pom.xml`（依赖/属性）
- `src/main/resources/logback.xml`（删除）
- `src/main/resources/tinylog.properties`（新增）
- `assembly/plugin-zip.xml`（`include` 由 logback.xml 改为 tinylog.properties）
- `src/main/java/vpcli/plugin/VPCLI.java`（静态块程序化配置；删除 `configureLogging`/`reloadLogbackConfiguration`；`loaded()` 置空）

> 其余 12 处用 SLF4J 的源码文件**不改动**。
