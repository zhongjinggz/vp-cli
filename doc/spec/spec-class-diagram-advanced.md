# 类图 Class Diagram - 进阶

## 16 静态操作和静态属性 Static Operation & Static Attribute
- 注意：在 plantuml 文档中操作(operation)称为方法（method），属性(attribute)成为字段（field）

### 需求

`{static}` 用于标记作为类级成员的静态操作与静态属性。

```plantuml
@startuml
class utility {
    {static} count: int = 0
    {static} configPath: string
    {static} getInstance(): utility
    {static} reset(): void
}
@enduml
```

### 方案
TODO

## 17 抽象类 Abstract Class

### 需求

在抽象类中，`{abstract}` 用于声明没有方法体的抽象方法。

```plantuml
@startuml
abstract class shape {
    {abstract} draw(): void
    {abstract} area(): double
}
@enduml
```

### 方案
TODO

## 18 限定关联 Qualified Associations

### 需求

限定关联通过在被关联类一端用方括号 `[..]` 标出限定符（qualifier），表示以某个键来索引关联另一端的实例。

```plantuml
@startuml
class "Order" as order {
}

class "LineItem" as lineItem {
}

order [orderNumber] "1" -- "0..*" lineItem
@enduml
```

注：该例表示 `Order` 通过 `orderNumber` 限定可定位到多条 `LineItem`。

### 方案
TODO



## 19 泛型类 Generic Class
- 状态：待方案设计
- 注：如 `class Foo<T>`

### 需求

```plantuml
@startuml
class Stack<T> {
    + push(item: T): void
    + pop(): T
    + size(): int
}
@enduml
```

### 方案
TODO

## 20 关联类 Association Class

### 需求

关联类同时作为一条关联和一个类存在，通过 `(被关联类1, 被关联类2) .. 关联类` 语法来表示。

```plantuml
@startuml
class "Company" as company {
}

class "Person" as person {
}

class "Employment" as employment {
    + role: string
    + startDate: date
}

company "0..*" -- "1..*" person
(company, person) .. employment 
@enduml
```

注：`Employment` 为 `Company` 与 `Person` 之间的关联类。

### 方案
TODO

## 21 嵌套类/内部类 Nested Class

### 需求

```plantuml
@startuml
class "Outer" as outer {
    + inner: Inner
}

class "Inner" as inner {
}

outer +-- inner
@enduml
```

注：嵌套（内部）类通过 `+--` 组合关系表示隶属关系，或直接在类图中以包含的方式呈现。

### 方案
TODO

## 22 接口实现 Implementation (棒棒糖方式)
- 状态：待编码
### 需求

```plantuml
@startuml
class class1 {}
class class2 {}

circle interface1
circle interface2

class1 -- interface1
interface2 -- class2

@enduml
```

接口通过“棒棒糖（lollipop）”表示：接口渲染为一个小圆（`circle`），再用**实线**把它接到实现类上。

### 方案



## 多元关联 Multiple Association

### 需求

```plantuml
@startuml
class A {
   
}

class B {
}

class C {
}

diamond nary1

A "0..*" -- nary1
B "0..*" -- nary1
C "0..*" -- nary1



@enduml
```