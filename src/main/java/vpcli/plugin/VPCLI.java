package vpcli.plugin;

import com.vp.plugin.*;
import vpcli.plugin.drivingadapter.CLIController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.tinylog.configuration.Configuration;

public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Dagger component 惰性持有者：仅在首次真正使用时才初始化，
    // 保证各 bean 的静态 logger 在 configLogWriterFile 完成配置后才创建。
    private static final class ComponentHolder {
        private static final VPCliComponent INSTANCE = DaggerVPCliComponent.create();
    }

    private static VPCliComponent component() {
        return ComponentHolder.INSTANCE;
    }


    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
        configLogWriterFile(pluginInfo);
        makeScriptsExecutable(pluginInfo);
    }

    // 在插件加载阶段把日志落点指到“插件安装目录下 log/”，其余参数（级别、格式、
    // 滚动策略、备份数、文件名模式）全部来自 tinylog.properties，代码不硬编码。
    // 必须在首次创建/获取 logger 之前调用（Tinylog 配置一经触碰即冻结，之后 set 会抛异常）。
    void configLogWriterFile(VPPluginInfo pluginInfo) {
        Path logDir = resolvePluginLogDir(pluginInfo);
        String pattern = readWriterFilePattern();
        String file = logDir.resolve(pattern).toAbsolutePath().toString();
        Configuration.set("writer.file", file);
    }

    // 解析日志输出目录：优先插件安装目录下 log/；否则系统临时目录；兜底当前工作目录。
    Path resolvePluginLogDir(VPPluginInfo pluginInfo) {
        if (pluginInfo != null) {
            File pluginDirFile = pluginInfo.getPluginDir();
            if (pluginDirFile != null) {
                Path logDir = pluginDirFile.toPath().resolve("log");
                try {
                    Files.createDirectories(logDir);
                    return logDir;
                } catch (IOException e) {
                    // 目录已存在（此时 createDirectories 视为成功）则仍使用插件下的 log；否则回退临时目录
                    if (Files.isDirectory(logDir)) {
                        return logDir;
                    }
                }
            }
        }
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    // 从 tinylog.properties 读取文件名模式（pattern 需含 placeholders）
    String readWriterFilePattern() {
        Properties props = new Properties();
        try (InputStream in = VPCLI.class.getResourceAsStream("/tinylog.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("无法加载日志配置文件 tinylog.properties", e);
        }
        return props.getProperty("writer.file.pattern", "vpcli.{date:yyyy-MM-dd}.{count}.log");
    }

    // 在安装/加载插件时，给插件 script/ 目录下的 vp-* 脚本赋可执行权限，
    // 省去用户手动 chmod +x。仅在真正需要时调用（避免无意义的写操作）：
    //  - Windows 完全不触发（setExecutable 是 no-op，且与项目约定无关）；
    //  - 非 Windows 已可执行（canExecute() 为 true）也已赋过权限，跳过。
    void makeScriptsExecutable(VPPluginInfo pluginInfo) {
        if (isWindows()) {
            return;
        }
        if (pluginInfo == null) {
            return;
        }
        File pluginDir = pluginInfo.getPluginDir();
        File scriptDir = pluginDir == null ? null : new File(pluginDir, "script");
        if (scriptDir == null || !scriptDir.isDirectory()) {
            return;
        }
        File[] scripts = scriptDir.listFiles((dir, name) -> name.startsWith("vp-"));
        if (scripts == null) {
            return;
        }
        for (File script : scripts) {
            if (!script.canExecute()) {
                script.setExecutable(true);
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    @Override
    public void unloaded() {
        // Dagger 生成的组件无显式关闭：@Singleton 实例由组件引用持有，
        // 到进程结束自然可达，无需在此清理。
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {
        CLIController cliController = component().cliController();
        try {
            cliController.invoke(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
