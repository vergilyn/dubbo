# 【001】provider 启动过程

## 扩展知识
### 1. `org.apache.dubbo.config.spring.context.annotation.DubboConfigConfigurationRegistrar`
- `org.springframework.context.annotation.ImportBeanDefinitionRegistrar`

### 2. `org.apache.dubbo.config.spring.context.annotation.DubboConfigConfiguration`
- `com.alibaba.spring.beans.factory.annotation.@EnableConfigurationBeanBinding`

```
@EnableConfigurationBeanBindings({
        @EnableConfigurationBeanBinding(prefix = "dubbo.application", type = ApplicationConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.module", type = ModuleConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.registry", type = RegistryConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.protocol", type = ProtocolConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.monitor", type = MonitorConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.provider", type = ProviderConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.consumer", type = ConsumerConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.config-center", type = ConfigCenterBean.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.metadata-report", type = MetadataReportConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.metrics", type = MetricsConfig.class),
        @EnableConfigurationBeanBinding(prefix = "dubbo.ssl", type = SslConfig.class)
})
public static class Single {

}
```
alibaba自己的扩展`com.alibaba.spring:spring-context-support:1.0.6`。
猜测其用途： （意会~~~~）

### 3. `org.springframework.context.EnvironmentAware`
```
// com.alibaba.spring:spring-context-support:1.0.6
public class ConfigurationBeanBindingRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {

        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);

        this.environment = (ConfigurableEnvironment) environment;

    }
}
```

### 4. 执行顺序： Constructor >> @Autowired >> @PostConstruct
             

## 2. `vergilyn-provider-examples`

```JAVA
package com.vergilyn.examples.config;

import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.vergilyn.examples.service")
@PropertySource("classpath:/dubbo-provider.properties")
public class ProviderAutoConfiguration {

    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(1000);
        return providerConfig;
    }
}
```

`dubbo-provider.properties`:
```
dubbo.application.name=${spring.application.name}
dubbo.registry.address=nacos://127.0.0.1:8848
dubbo.protocol.name=dubbo
dubbo.protocol.port=20880
```

### 2.1 启动过程中遇到的问题

#### 2.1.1 `The bean 'dubboBootstrapApplicationListener' could not be registered. A bean with that name has already been defined and overriding is disabled.`
```
***************************
APPLICATION FAILED TO START
***************************

Description:

The bean 'dubboBootstrapApplicationListener' could not be registered. A bean with that name has already been defined and overriding is disabled.

Action:

Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
```

原因很简单，可以参考：[spring中 allowBeanDefinitionOverriding(spring.main.allow-bean-definition-overriding) 分析](https://blog.csdn.net/liubenlong007/article/details/87885567)
**解决方法**：application.properties 中将 `spring.main.allow-bean-definition-overriding = true`。

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

#### 2.1.2 `java.lang.IllegalStateException: No such extension org.apache.dubbo.rpc.Protocol by name dubbo`
```
2020-03-09 21:14:11.560 ERROR 10504 --- [           main] o.s.boot.SpringApplication               : Application run failed

java.lang.IllegalStateException: No such extension org.apache.dubbo.rpc.Protocol by name dubbo
	at org.apache.dubbo.common.extension.ExtensionLoader.findException(ExtensionLoader.java:599) ~[classes/:na]
	at org.apache.dubbo.common.extension.ExtensionLoader.createExtension(ExtensionLoader.java:606) ~[classes/:na]
	at org.apache.dubbo.common.extension.ExtensionLoader.getExtension(ExtensionLoader.java:405) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.findConfigedPorts(ServiceConfig.java:644) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:444) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExportUrls(ServiceConfig.java:325) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExport(ServiceConfig.java:300) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.export(ServiceConfig.java:206) ~[classes/:na]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.lambda$exportServices$15(DubboBootstrap.java:916) ~[classes/:na]
	at java.util.HashMap$Values.forEach(HashMap.java:981) ~[na:1.8.0_201]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.exportServices(DubboBootstrap.java:904) ~[classes/:na]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.start(DubboBootstrap.java:744) ~[classes/:na]
	...
```

因为`dubbo-provider.properties`中配置了`dubbo.protocol.name = dubbo`，  
但是未依赖其实现`org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol`：  
```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-rpc-dubbo</artifactId>
    <version>${revision}</version>
</dependency>
```

#### 2.1.3 `org.apache.dubbo.rpc.RpcException: Unsupported server type: netty...`
```
org.apache.dubbo.rpc.RpcException: Unsupported server type: netty, url: dubbo://127.0.0.1:20880/com.vergilyn.examples.api.ProviderServiceApi
?anyhost=true&application=dubbo-provider-application&bind.ip=127.0.0.1&bind.port=20880
&channel.readonly.sent=true&codec=dubbo&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false
&heartbeat=60000&interface=com.vergilyn.examples.api.ProviderServiceApi&methods=sayHello,sayGoodbye&pid=9528
&release=&revision=1.0.0&sayGoodbye.retries=0&sayGoodbye.return=true&sayGoodbye.timeout=250&side=provider
&timeout=1000&timestamp=1583763120764&version=1.0.0
	at org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol.createServer(DubboProtocol.java:341) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol.openServer(DubboProtocol.java:320) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol.export(DubboProtocol.java:303) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.ProtocolListenerWrapper.export(ProtocolListenerWrapper.java:62) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper.export(ProtocolFilterWrapper.java:153) ~[classes/:na]
	at org.apache.dubbo.rpc.Protocol$Adaptive.export(Protocol$Adaptive.java) ~[classes/:na]
	at org.apache.dubbo.registry.integration.RegistryProtocol.lambda$doLocalExport$2(RegistryProtocol.java:244) ~[classes/:na]
	at java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1660) ~[na:1.8.0_201]
	at org.apache.dubbo.registry.integration.RegistryProtocol.doLocalExport(RegistryProtocol.java:242) ~[classes/:na]
	at org.apache.dubbo.registry.integration.RegistryProtocol.export(RegistryProtocol.java:199) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.ProtocolListenerWrapper.export(ProtocolListenerWrapper.java:60) ~[classes/:na]
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper.export(ProtocolFilterWrapper.java:151) ~[classes/:na]
	at org.apache.dubbo.rpc.Protocol$Adaptive.export(Protocol$Adaptive.java) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:492) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExportUrls(ServiceConfig.java:325) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.doExport(ServiceConfig.java:300) ~[classes/:na]
	at org.apache.dubbo.config.ServiceConfig.export(ServiceConfig.java:206) ~[classes/:na]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.lambda$exportServices$15(DubboBootstrap.java:916) ~[classes/:na]
	at java.util.HashMap$Values.forEach(HashMap.java:981) ~[na:1.8.0_201]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.exportServices(DubboBootstrap.java:904) ~[classes/:na]
	at org.apache.dubbo.config.bootstrap.DubboBootstrap.start(DubboBootstrap.java:744) ~[classes/:na]
```

同`2.1.2`的原因一样，
未依赖其实现类`org.apache.dubbo.remoting.transport.netty4.NettyTransporter`：  
```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-remoting-netty4</artifactId>
    <version>${revision}</version>
</dependency>
```


2020-03-11 >>>>
`DubboBootstrap#exportServices()`
```
configManager.getServices() 的 add时机？
```    