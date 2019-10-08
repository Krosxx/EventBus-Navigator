# EventBus 导航器

> A plugin for IDEA and Android Studio.

- 支持Kotlin语言
- 支持封装的YourBus.post函数
- 修改于 [likfe/eventbus3-intellij-plugin](https://github.com/likfe/eventbus3-intellij-plugin)

![](/pic/s1.png)

## 预览

Receiver function what is annotate with `@Subscribe`

### Kotlin subscriber function

![](pic/s1.png)

### Kotlin publisher expression

![](pic/s3.png)

### Java subscriber function
![](pic/s6.png)

### Java publisher expression

...


## 标记自封装post函数

  在函数名右键

![](pic/s4.png)

  随后可发现多出一标志

![](pic/s5.png)

### 安装

1. Marketplace

IDEA or Android Studio `Settings/Plugins/Marketplace` search `EventBus-Navigator`

2. 下载 [EventBus-Navigator.jar](https://github.com/Vove7/EventBus-Navigator/blob/master/EventBus-Navigator.jar) .

`Settings/Plugins/` Install Plugin from Disk.


## Warning

  目前（可能以后也）不支持java/kotlin post 基础类型 函数导航到kotlin接收函数，但Java可以。
  
基础类型包括:

```
"kotlin.Int" to "java.lang.Integer",
"kotlin.Boolean" to "java.lang.Boolean",
"kotlin.Char" to "java.lang.Character",
"kotlin.Double" to "java.lang.Double",
"kotlin.Float" to "java.lang.Float",
"kotlin.Long" to "java.lang.Long",
"kotlin.Any" to "java.lang.Object",
"kotlin.String" to "java.lang.String"
```

## Thanks

- [likfe/eventbus3-intellij-plugin](https://github.com/likfe/eventbus3-intellij-plugin)
- [kgmyshin/eventbus3-intellij-plugin](https://github.com/kgmyshin/eventbus3-intellij-plugin)