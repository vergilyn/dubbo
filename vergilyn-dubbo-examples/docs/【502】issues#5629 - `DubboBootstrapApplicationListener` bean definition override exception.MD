# 【502】issues#5629 - `DubboBootstrapApplicationListener` bean definition override exception.MD

+ [issues#5629 dubbo2.7.5中DubboBootstrapApplicationListener被注册多次][issues#5629]
+ [@EnableDubbo中的scanBasePackages不生效][issues#5591]
+ [A bean with that name has already been defined and overriding is disabled][issues#5610]
+ [issues#6231]
+ [issues#6250]

[issues#5591]: https://github.com/apache/dubbo/issues/5591
[issues#5629]: https://github.com/apache/dubbo/issues/5629
[issues#5610]: https://github.com/apache/dubbo/issues/5610
[issues#6231]: https://github.com/apache/dubbo/issues/6231
[issues#6250]: https://github.com/apache/dubbo/issues/6250

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The bean 'dubboBootstrapApplicationListener' could not be registered. A bean with that name has already been defined and overriding is disabled.

Action:

Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
```

- [spring中 allowBeanDefinitionOverriding(spring.main.allow-bean-definition-overriding) 分析](https://blog.csdn.net/liubenlong007/article/details/87885567)


> [issues#5629] mercyblitz: Please update the dependecy on your project's pom.xml
```xml
<dependency>
  <groupId>com.alibaba.spring</groupId>
  <artifactId>spring-context-support</artifactId>
  <version>1.0.6</version>
</dependency>
```
dubbo v2.7.6 已经依赖的是 v1.0.6


> [@EnableDubbo中的scanBasePackages不生效][issues#5591]  
> [A bean with that name has already been defined and overriding is disabled][issues#5610]  

貌似与 spring-framework 有关，刚好examples使用的是"spring-boot 2.2.2.RELEASE"，  
升级到 v2.2.6.RELEASE，依然存在问题。

## reason

**为什么"dubboBootstrapApplicationListener"会被registry 2次？**

第1次：
因为`@EnableDubboConfig`源码中存在`@Import(DubboConfigConfigurationRegistrar.class)`，
并且`DubboConfigConfigurationRegistrar implements org.springframework.context.annotation.ImportBeanDefinitionRegistrar`。
所以，启动时会调用`DubboConfigConfigurationRegistrar#registerBeanDefinitions(...)`。
其中会registry "dubboBootstrapApplicationListener"。

虽然，`@EnableDubboConfig`、`@DubboComponentScan`会重复调用`DubboBeanUtils#registerCommonBeans(...)`，
但通过源码`com.alibaba.spring.util.BeanRegistrar#registerInfrastructureBean(...)`可知，不会造成 repeat-registry。
（此时只是registry-bean，并未实例化！！！）

第2次：（可能不止下面这一处）
`ServiceAnnotationBeanPostProcessor#postProcessBeanDefinitionRegistry(...)`

## resolve

### 1  
application.properties 中将 `spring.main.allow-bean-definition-overriding = true`。

### 2 （fixed push）
`ServiceAnnotationBeanPostProcessor#postProcessBeanDefinitionRegistry(...)` 导致重复注册的代码：
```
AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, DubboBootstrapApplicationListener.class);

改为
BeanRegistrar.registerInfrastructureBean(registry, DubboBootstrapApplicationListener.BEAN_NAME,
                DubboBootstrapApplicationListener.class);  
```

### 3
`AnnotatedBeanDefinitionRegistryUtils#registerBeans(...)`判断重复注册

