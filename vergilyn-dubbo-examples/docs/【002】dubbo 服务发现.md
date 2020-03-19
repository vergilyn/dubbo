# 【002】consumer 启动过程

+ [服务引用（服务发现）](http://dubbo.apache.org/zh-cn/docs/source_code_guide/refer-service.html)


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

### 2.1 FAQ

#### 2.1.1 registry-center: NACOS, `No provider available...`
```
Caused by: java.lang.IllegalStateException: 
Failed to check the status of the service com.vergilyn.examples.api.ProviderServiceApi. 
No provider available for the service com.vergilyn.examples.api.ProviderServiceApi:1.0.0 
from the url nacos://127.0.0.1:8848/org.apache.dubbo.registry.RegistryService
    ?application=dubbo-consumer-application&dubbo=2.0.2&init=false
    &interface=com.vergilyn.examples.api.ProviderServiceApi
    &methods=sayHello,sayGoodbye&pid=7620&register.ip=127.0.0.1
    &revision=1.0.0&side=consumer
    &sticky=false&timeout=2000&timestamp=1583814014425
    &version=1.0.0 
to the consumer 127.0.0.1 use dubbo version 

	at org.apache.dubbo.config.ReferenceConfig.createProxy(ReferenceConfig.java:349) ~[classes/:na]
	at org.apache.dubbo.config.ReferenceConfig.init(ReferenceConfig.java:258) ~[classes/:na]
	at org.apache.dubbo.config.ReferenceConfig.get(ReferenceConfig.java:158) ~[classes/:na]
	at org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor.getOrCreateProxy(ReferenceAnnotationBeanPostProcessor.java:274) ~[classes/:na]
	at org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor.doGetInjectedBean(ReferenceAnnotationBeanPostProcessor.java:143) ~[classes/:na]
```

+ [issues#5871](https://github.com/apache/dubbo/issues/5871)
+ [issues#5885](https://github.com/apache/dubbo/issues/5885): 个人提的issues

dubbo中会有很多原因导致"no provider available"，个人实际遇到的是：因为nacos与dubbo v2.7.6的bug导致。

**异常原因分析过程**

1. 首先，确保provider已成功注册到nacos。

2. 根据异常堆栈信息找到异常位置 `ReferenceConfig#createProxy(...)`
```java
public class ReferenceConfig<T> extends ReferenceConfigBase<T> {

    private T createProxy(Map<String, String> map) {
        // 省略...

        if (urls.size() == 1) {
            // 获取 invoker 是相当重要的，也是造成"no provider available"的根本原因
            invoker = REF_PROTOCOL.refer(interfaceClass, urls.get(0));
        }
        // 省略...

        // check, 默认 true
        if (shouldCheck() && !invoker.isAvailable()) {
                    throw new IllegalStateException("Failed to check the status of the service "
                            + interfaceName
                            + ". No provider available for the service "
                            + (group == null ? "" : group + "/")
                            + interfaceName +
                            (version == null ? "" : ":" + version)
                            + " from the url "
                            + invoker.getUrl()
                            + " to the consumer "
                            + NetUtils.getLocalHost() + " use dubbo version " + Version.getVersion());
        }
    }
}
```

由源码可知，造成该exception的原因：shouldCheck() 返回 true，且 invoker.isAvailable() 返回false。

尝试修改`check = false`（<dubbo:reference check="false"/>），此时仍然报"no provider available"，但异常堆栈信息不同。
```text
Caused by: org.apache.dubbo.rpc.RpcException: No provider available from registry localhost:8848 
for service com.vergilyn.examples.api.ProviderServiceApi:1.0.0 on consumer 127.0.0.1 use dubbo version 4.0.9, 
please check status of providers(disabled, not registered or in blacklist).
	at org.apache.dubbo.registry.integration.RegistryDirectory.doList(RegistryDirectory.java:599) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.directory.AbstractDirectory.list(AbstractDirectory.java:75) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.list(AbstractClusterInvoker.java:291) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.invoke(AbstractClusterInvoker.java:256) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor.intercept(ClusterInterceptor.java:47) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster$InterceptorInvokerNode.invoke(AbstractCluster.java:92) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker.invoke(MockClusterInvoker.java:82) ~[classes/:na]
	at org.apache.dubbo.rpc.proxy.InvokerInvocationHandler.invoke(InvokerInvocationHandler.java:74) ~[classes/:na]
	at org.apache.dubbo.common.bytecode.proxy0.sayHello(proxy0.java) ~[classes/:na]
	at com.vergilyn.examples.ConsumerExamplesApplication.run(ConsumerExamplesApplication.java:30) [classes/:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	... 3 common frames omitted
```

此时注意，通过provider/nacos可知，启动provider时registry的 serviceName = "com.vergilyn.examples.api.ProviderServiceApi:1.0.0:"。
但是堆栈信息中提到的 service = "com.vergilyn.examples.api.ProviderServiceApi:1.0.0"，末尾缺少一个":"。
这其实是很关键的信息，我也是在了解了具体原因后，再回来看这些异常信息发现这是如此重要！！！
继续往后分析...

因为"check = false"，所以现在跟踪查看 `RegistryDirectory#isAvailable()`：  
```java
package org.apache.dubbo.registry.integration;

public class RegistryDirectory<T> extends AbstractDirectory<T> implements NotifyListener {

    // 返回false时，exception: No provider available        
    @Override
    public boolean isAvailable() {
        if (isDestroyed()) {
            return false;
        }
        // urlInvokerMap == null, return false
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

根据debug可知`urlInvokerMap == EMPTY`，所以`return false`。

**现在的问题，变成了为什么`urlInvokerMap == EMPTY`？**  

带着这个疑问继续往后看，回到修改成"check = false"时后的异常对战信息，查看为什么抛出异常：
```java
public class RegistryDirectory<T> extends AbstractDirectory<T> implements NotifyListener {
    private volatile boolean forbidden = false;
    
    @Override
    public List<Invoker<T>> doList(Invocation invocation) {
        if (forbidden) {  // true
            // 1. No service provider 2. Service providers are disabled
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "No provider available from registry " +
                    getUrl().getAddress() + " for service " + getConsumerUrl().getServiceKey() + " on consumer " +
                    NetUtils.getLocalHost() + " use dubbo version " + Version.getVersion() +
                    ", please check status of providers(disabled, not registered or in blacklist).");
        }

        // 省略...
        return invokers == null ? Collections.emptyList() : invokers;
    }
}
```

由源码可知，是因为`forbidden == true`。  
那么，**为什么 forbidden 会被设置成 true？**  

通过源码可知（find usages），只有 1个 地方会修改 forbidden：  
```java
public class RegistryDirectory<T> extends AbstractDirectory<T> implements NotifyListener {

    private void refreshInvoker(List<URL> invokerUrls) {
        if (invokerUrls.size() == 1
                    && invokerUrls.get(0) != null
                    && EMPTY_PROTOCOL.equals(invokerUrls.get(0).getProtocol())) {

            this.forbidden = true; // Forbid to access
            this.invokers = Collections.emptyList();
            routerChain.setInvokers(this.invokers);
            destroyAllInvokers(); // Close all invokers

        } else {
            this.forbidden = false; // Allow to access
        
        }
    }
}
```

通过debug可知，**会出现`protocol = empty`***，例如 url：
```text
# url
empty://127.0.0.1/com.vergilyn.examples.api.ProviderServiceApi
    ?application=dubbo-consumer-application
    &category=providers
    &check=false&dubbo=2.0.2&init=false
    &interface=com.vergilyn.examples.api.ProviderServiceApi
    &methods=sayHello,sayGoodbye
    &pid=13780&release=4.0.9&revision=1.0.0
    &side=consumer&sticky=false
    &timeout=2000&timestamp=1584498455807&version=1.0.0
```

**为什么会出现 protocol "empty://..." 调用 refreshInvoker() ？**

这涉及到dubbo的服务调用过程，consumer 启动时扫描到`@Reference`，会创建proxy，即`org.apache.dubbo.config.ReferenceConfig#createProxy(Map)`。
```java
package org.apache.dubbo.config;
public class ReferenceConfig<T> extends ReferenceConfigBase<T> {

    private T createProxy(Map<String, String> map) {
        if (urls.size() == 1) {
            invoker = REF_PROTOCOL.refer(interfaceClass, urls.get(0));
        }
    }
}
```

其调用链：  
```text
ReferenceConfig.createProxy()
            ↓
RegistryProtocol.refer()
            ↓
RegistryProtocol.doRefer()
            ↓
RegistryDirectory.subscribe()
            ↓
FailbackRegistry.subscribe()
            ↓
NacosRegistry.doSubscribe()
```

关键代码：
```java
package org.apache.dubbo.registry.nacos;

public class NacosRegistry extends FailbackRegistry {
    @Override
    public void doSubscribe(final URL url, final NotifyListener listener) {
        Set<String> serviceNames = getServiceNames(url, listener);
        doSubscribe(url, listener, serviceNames);
    }

    private void doSubscribe(final URL url, final NotifyListener listener, final Set<String> serviceNames) {
        execute(namingService -> {
            List<Instance> instances = new LinkedList();
            for (String serviceName : serviceNames) {
                instances.addAll(namingService.getAllInstances(serviceName));
                subscribeEventListener(serviceName, url, listener);
            }
            notifySubscriber(url, listener, instances);
        });
    }

    private void subscribeEventListener(String serviceName, final URL url, final NotifyListener listener)
            throws NacosException {
        EventListener eventListener = event -> {
            if (event instanceof NamingEvent) {
                NamingEvent e = (NamingEvent) event;
                notifySubscriber(url, listener, e.getInstances());
            }
        };
        namingService.subscribe(serviceName, eventListener);
    }

    private void notifySubscriber(URL url, NotifyListener listener, Collection<Instance> instances) {
        List<Instance> healthyInstances = new LinkedList<>(instances);
        if (healthyInstances.size() > 0) {
            // Healthy Instances
            filterHealthyInstances(healthyInstances);
        }

        List<URL> urls = toUrlWithEmpty(url, healthyInstances);

        NacosRegistry.this.notify(url, listener, urls);
    }


    private Set<String> getServiceNames(URL url, NotifyListener listener) {
        if (isAdminProtocol(url)) {
            // ...
        } else {
            return getServiceNames0(url);
        }
    }

    private Set<String> getServiceNames0(URL url) {
        NacosServiceName serviceName = createServiceName(url);

        final Set<String> serviceNames;

        if (serviceName.isConcrete()) { // is the concrete service name
            serviceNames = new LinkedHashSet<>();
            serviceNames.add(serviceName.toString());

            /* vergilyn-question, 2020-03-18 >>>> 注释，由于"no provider "
             *   https://github.com/apache/dubbo/issues/5871
             *   https://github.com/apache/dubbo/issues/5885
             */
            // Add the legacy service name since 2.7.6
            // serviceNames.add(getLegacySubscribedServiceName(url));
        } else {
            serviceNames = filterServiceNames(serviceName);
        }

        return serviceNames;
    }
}
```

因为 legacy-subscribed 会创建一个serviceNames = "com.vergilyn.examples.api.ProviderServiceApi:1.0.0"。  
provider并未提供该service，其提供的是"com.vergilyn.examples.api.ProviderServiceApi:1.0.0:" （最后的 冒号）。 

因为subscribe了naocs，但是实际不存在"com.vergilyn.examples.api.ProviderServiceApi:1.0.0"，所以 `instances == EMPTY`。  
正式由于此，导致创建了一个empty的protocol，进而导致后面 refreshInvokers() 时满足特定导致 invokers#destroy() 并将 invokers 赋值 empty。  

...至此，前面提到的问题其实都得到了解决。  
2020-03-19 >>>> 暂时解决方案，把legacy-subscribe-services-name注释掉，让其不生成无效的 serviceName。  

> 创建 empty protocol 目的
> https://github.com/apache/dubbo/issues/5871
> 生成empty的protocol是正常的。否则会出现服务端全部下线，但是客户端还有一个订阅的服务端的代理一直存在的情况

#### 2.1.2 "Invoke remote method timeout."
基于 `2.1.1` 解决方案后产生的exception。  
其中的 `169.254.**.***`、`10.2.**.**`都是本机的IP （即 127.0.0.1）
```text
Caused by: org.apache.dubbo.rpc.RpcException: Failed to invoke the method sayHello in the service com.vergilyn.examples.api.ProviderServiceApi. 
Tried 3 times of the providers [169.254.**.***:20880] (1/1) from the registry localhost:8848 
on the consumer 10.2.**.** using the dubbo version 2.7.6.RELEASE. 
Last error is: Invoke remote method timeout. method: sayHello, 
provider: dubbo://169.254.**.***:20880/com.vergilyn.examples.api.ProviderServiceApi
    ?anyhost=true&application=dubbo-consumer-application
    &category=providers&check=false&deprecated=false
    &dubbo=2.0.2&dynamic=true&generic=false&init=false
    &interface=com.vergilyn.examples.api.ProviderServiceApi
    &methods=sayHello,sayGoodbye
    &path=com.vergilyn.examples.api.ProviderServiceApi
    &pid=6420&protocol=dubbo
    &register.ip=10.2.**.**&release=2.7.6.RELEASE
    &remote.application=dubbo-provider-application&revision=1.0.0
    &side=consumer&sticky=false&timeout=5000&timestamp=1584606320503&version=1.0.0,
cause: org.apache.dubbo.remoting.TimeoutException: Waiting server-side response timeout by scan timer. 
start time: 2020-03-19 16:30:03.495, end time: 2020-03-19 16:30:08.504, 
client elapsed: 0 ms, server elapsed: 5009 ms, timeout: 5000 ms, 
request: Request [id=2, version=2.0.2, twoway=true, event=false, broken=false, data=RpcInvocation [methodName=sayHello, 
    parameterTypes=[class java.lang.String], arguments=[vergilyn], 
    attachments={path=com.vergilyn.examples.api.ProviderServiceApi, 
    remote.application=dubbo-consumer-application, 
    interface=com.vergilyn.examples.api.ProviderServiceApi, version=1.0.0, timeout=5000}]], channel: /169.254.**.***:65396 -> /169.254.**.***:20880
	at org.apache.dubbo.rpc.cluster.support.FailoverClusterInvoker.doInvoke(FailoverClusterInvoker.java:113) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.invoke(AbstractClusterInvoker.java:259) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor.intercept(ClusterInterceptor.java:47) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster$InterceptorInvokerNode.invoke(AbstractCluster.java:92) ~[classes/:na]
	at org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker.invoke(MockClusterInvoker.java:82) ~[classes/:na]
	at org.apache.dubbo.rpc.proxy.InvokerInvocationHandler.invoke(InvokerInvocationHandler.java:74) ~[classes/:na]
	at org.apache.dubbo.common.bytecode.proxy0.sayHello(proxy0.java) ~[classes/:na]
	at com.vergilyn.examples.ConsumerExamplesApplication.run(ConsumerExamplesApplication.java:30) [classes/:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	... 3 common frames omitted
Caused by: java.util.concurrent.ExecutionException: org.apache.dubbo.remoting.TimeoutException: Waiting server-side response timeout by scan timer.
    start time: 2020-03-19 16:30:03.495, 
    end time: 2020-03-19 16:30:08.504, 
    client elapsed: 0 ms, server elapsed: 5009 ms, timeout: 5000 ms, 
    request: Request [id=2, version=2.0.2, twoway=true, event=false, broken=false, data=RpcInvocation [methodName=sayHello, 
        parameterTypes=[class java.lang.String], arguments=[vergilyn], 
        attachments={path=com.vergilyn.examples.api.ProviderServiceApi, 
        remote.application=dubbo-consumer-application, 
        interface=com.vergilyn.examples.api.ProviderServiceApi, version=1.0.0, timeout=5000}]], 
    channel: /169.254.65.244:65396 -> /169.254.65.244:20880
	at java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:357) ~[na:1.8.0_171]
	at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1915) ~[na:1.8.0_171]
	at org.apache.dubbo.rpc.AsyncRpcResult.get(AsyncRpcResult.java:181) ~[classes/:na]
    ......
```

1. provider 已启动，并在nacos注册了正确的service。
2. `telnet 127.0.0.1 20880`正常（provider中配置的 dubbo/netty 通信端口）

其实已经到达 provider，但是consumer使用的序列化方式是 hessian2。但是在provider忘记依赖 hessian2（不清楚默认序列化方式什么）。
所以导致 序列化错误，provider无法正常处理。

**2020-03-19，尽快解决log打印不完整问题。例如此exception，在provider端并没有任何异常，但实际源码中有打印log，但是console缺没有。**
（"2.1.1"也遇到了，部分dubbo源码中的log.error并未打印出来~~）

