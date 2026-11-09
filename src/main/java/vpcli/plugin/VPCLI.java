package vpcli.plugin;

import com.vp.plugin.*;
import vpcli.plugin.drivingadapter.CLIController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.tinylog.configuration.Configuration;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Dagger component 惰性持有者：仅在首次真正使用（在 loaded() 完成 configureLogging
    // 之后）才初始化，从而保证依赖树各 bean 的静态 logger 创建晚于 Tinylog 配置。
    private static final class ComponentHolder {
        private static final VPCliComponent INSTANCE = DaggerVPCliComponent.create();
    }
    private static VPCliComponent component() {
        return ComponentHolder.INSTANCE;
    }


    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
        configureLogging(pluginInfo);
        makeScriptsExecutable(pluginInfo);
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
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    // 在插件加载阶段按 profile 选择日志输出目标，程序化配置 Tinylog。
    // 未指定 -Dvpcli.log.profile 时默认 dev（输出到控制台）。
    // 所有可改参数（级别、格式、writer 类型、滚动策略、文件名模式）均来自
    // tinylog.properties，按 dev./prod. 前缀区分，便于通过配置文件调整。
    void configureLogging(VPPluginInfo pluginInfo) {
        String profile = System.getProperty("vpcli.log.profile", "dev");
        boolean prod = "prod".equalsIgnoreCase(profile);
        File logDir = prod ? resolvePluginLogDir(pluginInfo) : null;
        Configuration.replace(buildLoggingConfig(loadLoggingProperties(), prod, logDir));
    }

    // 加载 classpath 根下的 tinylog.properties（与 Tinylog 自动发现的资源同名，
    // 作为唯一配置源）。
    Properties loadLoggingProperties() {
        Properties props = new Properties();
        try (InputStream in = VPCLI.class.getResourceAsStream("/tinylog.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("无法加载日志配置文件 tinylog.properties", e);
        }
        return props;
    }

    // 从配置文件构造传给 Tinylog 的 writer.* 映射（纯函数，便于单测锁定映射内容；
    // prod 时将动态解析出的插件日志目录拼入 writer.file）。
    Map<String, String> buildLoggingConfig(Properties props, boolean prod, File logDir) {
        String prefix = prod ? "prod." : "dev.";
        Map<String, String> cfg = new LinkedHashMap<>();
        cfg.put("writer", props.getProperty(prefix + "writer"));
        cfg.put("writer.level", props.getProperty("writer.level"));
        cfg.put("writer.format", props.getProperty("writer.format"));
        if (prod) {
            String pattern = props.getProperty(prefix + "writer.file.pattern");
            cfg.put("writer.file", new File(logDir, pattern).getAbsolutePath());
            cfg.put("writer.policies", props.getProperty(prefix + "writer.policies"));
            cfg.put("writer.backups", props.getProperty(prefix + "writer.backups"));
        }
        return cfg;
    }

    // 解析 prod 日志输出目录（仅用于滚动文件 writer 的文件路径）。
    File resolvePluginLogDir(VPPluginInfo pluginInfo) {
        if (pluginInfo != null) {
            File pluginDir = pluginInfo.getPluginDir();
            if (pluginDir != null) {
                File logDir = new File(pluginDir, "log");
                if (logDir.mkdirs() || logDir.isDirectory()) {
                    return logDir;
                }
            }
        }
        String dirProp = System.getProperty("vpcli.log.dir");
        if (dirProp != null && !dirProp.isEmpty()) {
            return new File(dirProp);
        }
        return new File(System.getProperty("user.dir"), "log");
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
