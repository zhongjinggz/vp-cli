package plugins.vpcli;

import dagger.Component;
import jakarta.inject.Singleton;
import plugins.vpcli.drivingadapter.CLIController;

/**
 * Dagger 2 装配根。编译期由 dagger-compiler 生成 DaggerVPCliComponent。
 * 全部 bean 均为具体类、以构造器 @jakarta.inject.Inject 声明依赖，因此 Dagger
 * 通过 JIT 绑定即可自动装配整棵依赖树，无需任何 module 或显式注册。
 * （Migrated from hand-written BeanFactory; replaces Micronaut which crashed on
 * VP install paths containing spaces due to runtime classpath scanning.)
 */
@Singleton
@Component
public interface VPCliComponent {

    /** CLI 入口控制器。 */
    CLIController cliController();
}
