# 架构 Architecture

## 0 Overview

###

#### Element Hierarchy

```plantuml
@startuml

abstract class MyElement {
    id
    name
}

abstract class MyClassifier {
        
}

class MyProperty {
    isStatic: boolean
    type: MyElement
    multiplicity: String
}

MyElement <|-- MyProperty
MyElement <|-- MyClassifier
MyElement <|-- MyDependency
MyElement <|-- MyAssociation
MyElement <|-- MyPackage

MyClassifier <|-- MyClass
MyClassifier <|-- MyInterface

MyPackage <|-- MyModal


@enduml
```

#### Dependency

```plantuml
@startuml

abstract class MyClassifier {
        
}

class MyDependency {
}


MyDependency "*" --> "client 1" MyClassifier
MyDependency "*" --> "1 supplier " MyClassifier


@enduml
```
#### Association

```plantuml
@startuml

abstract class MyClassifier {
        
}

class MyProperty {
    isStatic: boolean
    multiplicity: String
}


MyAssociation *--> "fromEnd 1" MyProperty
MyAssociation *--> "toEnd 1" MyProperty

MyProperty "associationEnd 0..*" --> "classifier 1" MyClassifier



@enduml
```

#### Classifier

```plantuml
@startuml

together {
    class MyProperty {
        isStatic: boolean
        multiplicity: String
    }
    
    abstract class MyClassifier {
    }
    
    class MyOperation{
        returnType: MyClassifier
    }
}
    

MyClassifier <|-- MyClass
MyClassifier <|-- MyInterface


MyProperty "associationEnd 0..*" --> "classifier 1" MyClassifier
MyClassifier "0..1" *--> "attribute 0..*" MyProperty
MyClassifier "1" *--> "0..*" MyOperation

MyOperation "1" *--> "0..*" MyParameter

@enduml
```


## 1 replace 'MyAssociationEnd' with 'MyProperty' 

### 现状：
- Visual Paradigm 模型中的 AssociationEnd 的 ParentId 为空
- Visual Paradigm 模型中 AssociationEnd 和 Attribute 是两个不同的类，与 UML1.4 相符，但 UML2.5中，两者合并为 Property 类。
     
### 需求
- 使 MyAssociationEnd 的父亲是 MyAssociation
- MyAssociationEnd 改名为 MyProperty 
#### Association

```plantuml
@startuml

abstract class MyClassifier {
        
}

class MyProperty {
    isStatic: boolean
    multiplicity: String
}

MyAssociation *--> "fromEnd 1" MyProperty
MyAssociation *--> "toEnd 1" MyProperty

MyProperty "associationEnd 0..*" --> "classifier 1" MyClassifier

@enduml
```

### 方案

1. MyAssociationEnd 改名为 MyProperty
2. 在 MyElementFactory 中增加 createRelatedElementWithParent(parent, vpElement), 在本例中代替原来的 createRelatedElement(), 否则会死循环
3. 在 MyAssociationFactory 中调用 createRelatedElementWithParent() 创建 fromEnd 和 toEnd 



## 2 merge 'MyProperty' to "MyAttribute" 


## 用 JSON 打印出内存中的元素树