# 【500】issues#5966 - cluster failover strategy unavailable.md

+ [dubbo 集群容错](http://dubbo.apache.org/zh-cn/docs/user/demos/fault-tolerent-strategy.html)

+ [issues#5966, @Reference注解中cluster=“failfast"失效](https://github.com/apache/dubbo/issues/5966)

+ [issues#6108, v2.7.6 dubbo.consumer.cluster = failfast 失效](https://github.com/apache/dubbo/issues/6108)
+ [pull#6110, solution issues#6108](https://github.com/apache/dubbo/pull/6110)


| strategy          | remark |
| :---------------- | :----- |
| failover(default) | 失败自动切换，自动重试其它服务器（默认） |
| failfast          | 快速失败，立即报错，只发起一次调用 |
| failsafe          | 失败安全，出现异常时，直接忽略 |
| failback          | 失败自动恢复，记录失败请求，定时重发 |
| forking           | 并行调用多个服务器，只要一个成功即返回 |
| broadcast         | 广播逐个调用所有提供者，任意一个报错则报错 |

```text
@Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, check = true
         , retries = 2, cluster = "failfast")
private ProviderFirstApi firstApi;

org.apache.dubbo.rpc.RpcException: Failed to invoke the method sayHello in the service com.vergilyn.examples.api.ProviderFirstApi.
     Tried 3 times of the providers ...

   at org.apache.dubbo.rpc.cluster.support.FailoverClusterInvoker.doInvoke(FailoverClusterInvoker.java:119)
   at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.invoke(AbstractClusterInvoker.java:264)
   at org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor.intercept(ClusterInterceptor.java:51)
   at org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster$InterceptorInvokerNode.invoke(AbstractCluster.java:108)
   at org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker.invoke(MockClusterInvoker.java:86)
   at org.apache.dubbo.rpc.proxy.InvokerInvocationHandler.invoke(InvokerInvocationHandler.java:96)
   at org.apache.dubbo.common.bytecode.proxy0.sayHello(proxy0.java)
   at com.vergilyn.examples.consumer.issues.ClusterFailoverTest.test(ClusterFailoverTest.java:48)
   ....
```

根据log可知实际是`FailoverClusterInvoker`，并且重试了3次。

### cause
1. `org.apache.dubbo.config.ReferenceConfig#createProxy(...)`
构建注册url`url.getParameter("cluster") = null`

2. `org.apache.dubbo.registry.integration.RegistryProtocol#refer(...)`

3. `Cluster$Adaptive` 
`url.getParameter("cluster", default: "failover") = "failover"`


个人解决思路：  
1. 可以修改 code-generator(即 `Cluster$Adaptive` )，不使用`url.getUrl()`，而是使用`url.getConsumerUlr()`（包含 failover-strategy）。

但是这种做法貌似不满足 [配置加载流程](http://dubbo.apache.org/zh-cn/docs/user/configuration/configuration-load-process.html)

