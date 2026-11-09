# 用例图 Use Case Diagram

## 1 用例和包 Use Case & Package
- 状态：已完成

### 需求

```plantuml
@startuml
allowmixing

namespace "package1" as package1 {
    usecase "usecase1" as package1__usecase1
}

usecase "usecase2" as usecase2

@enduml
```

## 方案
略

## 2 执行者 Actor
- 状态：待设计

### 需求

```plantuml
@startuml
allowmixing

namespace "package1" as package1 {
    actor "actor1" as package1__actor1
}

actor "actor2" as actor2

@enduml
```

### 方案
TODO

## 3 执行者和用例关联
- 状态：待设计

### 需求

```plantuml
@startuml
allowmixing


actor "actor1" as actor1
usecase "usecase1" as usecase1
usecase "usecase2" as usecase2

actor1 --> usecase1
actor1 --> usecase2

@enduml
```

### 方案
TODO

## 4 泛化 Generalization
- 状态：待编写需求

### 需求

```plantuml
@startuml
allowmixing

' 执行者之间的泛化
actor "普通用户" as normalUser
actor "管理员" as admin
normalUser <|-- admin

' 用例之间的泛化
usecase "登录" as login
usecase "密码登录" as passwordLogin
login <|-- passwordLogin

@enduml
```

说明：泛化可作用于执行者之间（子执行者继承父执行者参与的用例）或用例之间（子用例继承父用例的行为），用空心三角箭头表示，子指向父。

## 5 包含 Include
- 状态：待编写需求

### 需求

```plantuml
@startuml
allowmixing

usecase "下订单" as placeOrder
usecase "验证库存" as checkStock
usecase "扣减库存" as deductStock

placeOrder ..> checkStock : <<include>>
placeOrder ..> deductStock : <<include>>

@enduml
```

说明：include 表示基础用例在执行时必定会包含被包含用例的行为，箭头从基础用例指向被包含用例，标注 `<<include>>` 构造型（虚线依赖）。

## 6 扩展 Extend
- 状态：待编写需求

### 需求

```plantuml
@startuml
allowmixing

usecase "登录" as login
usecase "找回密码" as forgotPassword
usecase "生成验证码" as generateCaptcha

forgotPassword ..> login : <<extend>>
generateCaptcha ..> login : <<extend>>

@enduml
```

说明：extend 表示扩展用例在满足一定条件时才会加入到基础用例中，箭头从扩展用例指向基础用例，标注 `<<extend>>` 构造型（虚线依赖）。

## 7 依赖 Dependence
- 状态：待编写需求

### 需求

```plantuml
@startuml
allowmixing

usecase "生成报告" as genReport
usecase "导出PDF" as exportPdf
usecase "导出Excel" as exportExcel

genReport ..> exportPdf
genReport ..> exportExcel

@enduml
```

说明：普通的依赖关系用带箭头的虚线表示，表示一个用例以某种方式依赖于另一个用例，但不带 `<<include>>`/`<<extend>>` 等构造型。

## 8 主题 Subject
- 状态：待编写需求

### 需求
- UML中用术语 Subject 表示系统或子系统边界  

```plantuml
@startuml
allowmixing

namespace "订单子系统" as orderSub <<Rectangle>> {
    usecase "下单" as placeOrder
    usecase "取消订单" as cancelOrder
}

namespace "支付子系统" as paySub <<Rectangle>> {
    usecase "在线支付" as onlinePay
}

@enduml
```

说明：子系统用 `package` + `<<subsystem>>` 构造型表示，用于对用例图元素进行分组。

## 9 注释 Note
- 状态：待编写需求

### 需求

```plantuml
@startuml
allowmixing

usecase "登录" as login
note right of login
    用户输入用户名和密码进行身份验证
end note

actor "管理员" as admin
note bottom of admin : 拥有后台所有权限

@enduml
```

说明：注释（Note）用于补充说明单个元素，或通过 `note ... end note` 跨元素说明。

## 10 扩展点 Extension Point
- 状态：待编写需求

### 需求
- 方法一：文字说明
- 由于 PlantUML 对于 usecase ，as 既可以用于多行输出，也可以用于别名，两种用法冲突，因此下面用注释表示，一边保留全局唯一别名。
```plantuml
@startuml
allowmixing

usecase "下单" as placeOrder

note right of placeOrder
  Extension Points：
  --
  发货成功通知: 验证通过后
  库存不足提醒: 库存不足时
end note

@enduml
```

方法二：利用扩展依赖，同时注明扩展条件
```plantuml
@startuml
allowmixing

usecase "下单" as placeOrder

usecase "发货成功通知" as notify
usecase "库存不足提醒" as stockWarning

notify ..> placeOrder : <<extend>>
note on link
验证通过后
end note

stockWarning ..> placeOrder : <<extend>>
note on link
库存不足时
end note

@enduml
```

说明：扩展点位于基础用例内部，标识出哪些位置可能被扩展用例插入。上面的图用注释列出扩展点，并通过 `<<extend>>` 表示对应扩展关系。
