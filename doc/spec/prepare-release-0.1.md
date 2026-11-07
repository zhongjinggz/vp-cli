# 简化架构

## 目标
准备正式发布，使用户可以从 github 下载并使用。 主要步骤如下：

- 补充单元测试
- 用 Java 代替目前的 Python，统一打包到最终的 zip 中
- Shell 脚本统一打包到 zip 中
- 编写 README.md (中英文)
- 在 git 中为本 release 打 tag
- 将打包后的 zip 放入 github release

## 步骤0: 补充单元测试
- 状态：已完成
 
## 步骤1: 用 Java 代替目前的 Python，统一打包到最终的 zip 中
具体方案和步骤如下：

- 改变目录结构
  - src/main/java/vpcli/plugin/ : the vp plugin 
- 创建多 maven/idea 模块系统
    - module: src/main/java/vpcli/plugin/ : the vp plugin
    - module: src/main/java/vpcli/shell/  : java shell to call the plugin with command line
    - script: src/main/script/            : .sh or .bat to call the java shell
- 将 Python 转化为 java
  - 同步修改 script
- 脚本统一打包到 zip 中



## 步骤3: 编写 README.md (中文)

## 步骤4: 编写 README.md (英文)
将中文翻译为英文，在中、英文两个版本中加超链接（文档顶端），默认为英文版。

## 步骤5: 在 git 中为本 release 打 tag

## 步骤6: 将打包后的 zip 放入 github release
