# dubbo-spring-boot-project
+ [https://github.com/apache/dubbo-spring-boot-project][github, dubbo-spring-boot-project]

[github, dubbo-spring-boot-project]: https://github.com/apache/dubbo-spring-boot-project

## 1. remark
2020-03-05 >>>>  
**checkout from `dubbo-spring-boot-project origin/2.7.5-release`。**
本打算在此版本的基础之上调整成依赖 dubbo-v2.7.6。
**放弃了....还是等 dubbo-spring-boot 2.7.6。**

## 2. FAQ
### 2.1 `org.springframework.boot.bind.RelaxedDataBinder`
`RelaxedDataBinder`在 spring-boot 2.0 中已经被移除。  
建议使用新的`Binder.class`替代。

+ [Spring Boot Relaxed Binding 2.0](https://github.com/spring-projects/spring-boot/wiki/Relaxed-Binding-2.0)
- [Spring Boot 2.0 迁移指南](http://www.54tianzhisheng.cn/2018/03/06/SpringBoot2-Migration-Guide/)

The new Binding API has replaced a lot of the old classes used for relaxed binding  
and relaxed property resolution.

- The `RelaxedDataBinder` has been replaced by the `Binder` class. For example, the following POJO,
```
class FooProperties {

	private String bar;
	public String getBar() { ... }
    public void setBar(String bar) { ... }
}
```
can be bound as follows:
```
Binder binder = Binder.get(environment);
FooProperties foo = binder.bind("foo", Bindable.of(FooProperties.class)).get();
```
Details about the bind method can be found in the javadoc.

- The `RelaxedPropertyResolver` which was used to resolve properties in a relaxed way has also been removed.
Instead, properties should be read directly from the environment using the uniform format:
```
this.environment.containsProperty("spring.jpa.database-platform")
```
