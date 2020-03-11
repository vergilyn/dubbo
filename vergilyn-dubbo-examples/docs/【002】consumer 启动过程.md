# 【002】consumer 启动过程

## 2. `vergilyn-consumer-examples`

```java
package com.vergilyn.examples.config;

@Configuration
@EnableDubbo(scanBasePackages = "com.vergilyn.examples.service", multipleConfig = true)
@PropertySource("classpath:/dubbo-consumer.properties")
public class ConsumerAutoConfiguration {

}
```

```java
package com.vergilyn.examples;

@SpringBootApplication
@Slf4j
public class ConsumerExamplesApplication {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 2000)
    private ProviderServiceApi providerServiceApi;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerExamplesApplication.class);
        application.run(args);
    }

    @Bean
    public ApplicationRunner runner() {
        return args -> log.info(providerServiceApi.sayHello("vergilyn"));
    }

}
```

dubbo-consumer.properties:  
```properties
dubbo.application.name=${spring.application.name}
dubbo.registry.address=nacos://127.0.0.1:8848
dubbo.consumer.timeout=1000
```

### 2.1 启动过程遇到的问题

#### 2.1.1 `No provider available from registry...`
```
Caused by: java.lang.IllegalStateException: 
Failed to check the status of the service com.vergilyn.examples.api.ProviderServiceApi. 
No provider available for the service com.vergilyn.examples.api.ProviderServiceApi:1.0.0 
from the url nacos://127.0.0.1:8848/org.apache.dubbo.registry.RegistryService
    ?application=dubbo-consumer-application&dubbo=2.0.2&init=false
    &interface=com.vergilyn.examples.api.ProviderServiceApi
    &methods=sayHello,sayGoodbye&pid=7620&register.ip=192.168.31.46
    &revision=1.0.0&side=consumer
    &sticky=false&timeout=2000&timestamp=1583814014425
    &version=1.0.0 
to the consumer 192.168.31.46 use dubbo version 

	at org.apache.dubbo.config.ReferenceConfig.createProxy(ReferenceConfig.java:349) ~[classes/:na]
	at org.apache.dubbo.config.ReferenceConfig.init(ReferenceConfig.java:258) ~[classes/:na]
	at org.apache.dubbo.config.ReferenceConfig.get(ReferenceConfig.java:158) ~[classes/:na]
	at org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor.getOrCreateProxy(ReferenceAnnotationBeanPostProcessor.java:274) ~[classes/:na]
	at org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor.doGetInjectedBean(ReferenceAnnotationBeanPostProcessor.java:143) ~[classes/:na]
```

（个人遇到）造成这exception的原因是：
```java
package org.apache.dubbo.registry.integration;

public class RegistryDirectory<T> extends AbstractDirectory<T> implements NotifyListener {

    // 返回false时，exception: No provider available        
    @Override
    public boolean isAvailable() {
        if (isDestroyed()) {
            return false;
        }
        // urlInvokerMap == null, 所以返回 false
        Map<String, Invoker<T>> localUrlInvokerMap = urlInvokerMap;
        if (localUrlInvokerMap != null && localUrlInvokerMap.size() > 0) {
            for (Invoker<T> invoker : new ArrayList<>(localUrlInvokerMap.values())) {
                if (invoker.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }
}
```

首先，确保provider已成功注册到nacos。