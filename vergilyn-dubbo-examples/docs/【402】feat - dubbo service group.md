# 【400】feat - dubbo service group.md

+ [服务分组](http://dubbo.apache.org/zh-cn/docs/user/demos/service-group.html)


## 2. FAQ
> [对同个接口的不同实现通过@Service设置不同group无法生效，zk中只注册了其中一个group](https://github.com/apache/dubbo/issues/6283)  
> mercyblitz: This issue will be resolved after Apache Dubbo 2.7.8 release.

### 2.1 注解模式下，同一接口无法注册到不同group
```java
// 
public interface GroupApi {
    String invoke(String param);
}

@org.apache.dubbo.config.annotation.Service(version = "1.0.0", group = "group-first")
public class GroupApiFirstImpl implements GroupApi {
    // ... 
}

@org.apache.dubbo.config.annotation.Service(version = "1.0.0", group = "group-second")
public class GroupApiSecondImpl implements GroupApi {
    // ... 
}
```

#### 环境
dubbo: v2.7.6 源码分支，v2.7.7
nacos: 1.2.1
jdk: 1.8
spring-boot: 2.2.6.RELEASE

#### 问题  
nacos 服务列表中只存在 `providers:com.vergilyn.examples.api.GroupApi:1.0.0:group-first`，而不存在`...:group-second`。

```text
2020-06-11 16:46:12.084  WARN 920 --- [           main] o.a.dubbo.config.context.ConfigManager   :  [DUBBO] Duplicate ServiceBean found, 
  there already has one default ServiceBean or more than two ServiceBeans have the same id, 
  you can try to give each ServiceBean a different id : 
  <dubbo:service beanName="ServiceBean:com.vergilyn.examples.api.GroupApi:1.0.0:group-second" />, dubbo version: 2.7.6.RELEASE, current host: 127.0.0.1
```

注解模式怎么指定 `different id`？

#### XML 模式
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <context:property-placeholder/>

    <dubbo:application name="demo-provider"/>
    <dubbo:registry address="nacos://127.0.0.1:8848"/>
    <dubbo:protocol name="dubbo" port="20880"/>

    <bean id="groupAService" class="org.apache.dubbo.samples.group.impl.GroupAServiceImpl"/>

    <bean id="groupBService" class="org.apache.dubbo.samples.group.impl.GroupBServiceImpl"/>

    <dubbo:service group="groupA" interface="org.apache.dubbo.samples.group.api.GroupService" ref="groupAService"/>

    <dubbo:service group="groupB" interface="org.apache.dubbo.samples.group.api.GroupService" ref="groupBService"/>

</beans>

```

spring 容器在启动的时候，会读取spring 默认的一些schema以及dubbo自定义的schema（`spring.schemas`, `spring.handlers`, `spring.factories`等资源文件）。  
每个schema都会对应一个自己的`NamespaceHandler`，`NamespaceHandler`里面通过`BeanDefinitionParser`来解析配置信息并转化为需要加载的bean对象。  
- `org.apache.dubbo.config.spring.schema.DubboNamespaceHandler`
- `org.apache.dubbo.config.spring.schema.DubboBeanDefinitionParser`

```java
package org.apache.dubbo.config.spring.schema;

public class DubboBeanDefinitionParser implements BeanDefinitionParser {
    
    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");

        if (StringUtils.isEmpty(id) && required) {
            // 省略代码...
            /** vergilyn-comment, 2020-06-12 >>>>
             * ex. 同一接口多个实现属于不用的group
             *   - org.apache.dubbo.samples.group.api.GroupService
             *   - org.apache.dubbo.samples.group.api.GroupService2
             *   - org.apache.dubbo.samples.group.api.GroupService[3+]
             */
            id = generatedBeanName;
            int counter = 2;
            while (parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter++);
            }
        }

        if (StringUtils.isNotEmpty(id)) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            /** vergilyn-comment, 2020-06-12 >>>> IMPORTANT!
             *  EX. {@link ServiceBean#id} or {@link org.apache.dubbo.config.ServiceConfig#id}
             */
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }

        // 省略代码...
    }
}
```

由`DubboBeanDefinitionParser`可知，dubbo-service-bean id:  
- org.apache.dubbo.samples.group.api.GroupService
- org.apache.dubbo.samples.group.api.GroupService2

所以，`ServiceConfig` --`@PostConstruct`--> `AbstractConfig#addIntoConfigManager()` ----> `ConfigManager#addIfAbsent`  

因为`id`不同，所以最终dubbo会注册2个不同的service:  
- providers:org.apache.dubbo.samples.group.api.GroupService::groupA
- providers:org.apache.dubbo.samples.group.api.GroupService::groupB

#### annotation 模式
1. 确实已经scan并registry-bean(`ServiceAnnotationBeanPostProcessor#registerServiceBean()`) `GroupApiFirstImpl` & `GroupApiSecondImpl`，service-bean-name: 
- `ServiceBean:com.vergilyn.examples.api.GroupApi:1.0.0:group-first`
- `ServiceBean:com.vergilyn.examples.api.GroupApi:1.0.0:group-second`

但是，service-bean-id 都是'com.vergilyn.examples.api.GroupApi'

2. `ServiceConfig` --`@PostConstruct`--> `AbstractConfig#addIntoConfigManager()`
dubbo 中将 dubbo.@Service 添加到 ConfigManager 时使用的是Map。  
`org.apache.dubbo.config.context.ConfigManager#addIfAbsent()`中，`GroupApiFirstImpl` 和 `GroupApiSecondImpl` 得到的 id/key是相同的都是`x.xx.GroupApi`，
导致只会export一次interface。



## other
xml 与 annotation 生成的 service-bean-name 不一样：
- xml: `DubboBeanDefinitionParser.parse(...)` 例如 'org.apache.dubbo.samples.group.api.GroupService{ | 2+}'
- anno: `ServiceAnnotationBeanPostProcessor.generateServiceBeanName(...)` 例如 'ServiceBean:org.apache.dubbo.samples.api.GroupService:1.0.0:groupA'
