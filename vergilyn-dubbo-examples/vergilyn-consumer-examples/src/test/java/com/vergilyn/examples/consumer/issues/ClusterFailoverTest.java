package com.vergilyn.examples.consumer.issues;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/fault-tolerent-strategy.html">dubbo 集群容错</a>
 *
 *
 * @author vergilyn
 * @date 2020-05-13
 *
 * @see org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster
 * @see org.apache.dubbo.rpc.cluster.support.BroadcastCluster
 * @see org.apache.dubbo.rpc.cluster.support.FailbackCluster "failback"
 * @see org.apache.dubbo.rpc.cluster.support.FailfastCluster "failfast"
 * @see org.apache.dubbo.rpc.cluster.support.FailoverCluster "failover"
 * @see org.apache.dubbo.rpc.cluster.support.FailsafeCluster "failsafe"
 * @see org.apache.dubbo.rpc.cluster.support.ForkingCluster  "forking"
 * @see org.apache.dubbo.rpc.cluster.support.MergeableCluster "mergeable"
 * @see org.apache.dubbo.rpc.cluster.support.registry.ZoneAwareCluster "zone-aware"
 *
 */
@Slf4j
public class ClusterFailoverTest extends AbstractSpringBootTest {

    /*
     * Cluster strategy, legal values include: failover, failfast, failsafe, failback, forking
     *
     * 集群容错方案              说明
     * failover       失败自动切换，自动重试其它服务器（默认）
     * failfast       快速失败，立即报错，只发起一次调用
     * failsafe       失败安全，出现异常时，直接忽略
     * failback       失败自动恢复，记录失败请求，定时重发
     * forking        并行调用多个服务器，只要一个成功即返回
     * broadcast      广播逐个调用所有提供者，任意一个报错则报错
     */
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, check = true
            , retries = 2, cluster = "failfast")
    private ProviderFirstApi firstApi;

    @Test
    public void test(){
        String rs = firstApi.sayHello("vergilyn", 1000);

        System.out.println(rs);
    }

    /**
     * <a href="https://github.com/apache/dubbo/issues/6108">issues#6108, v2.7.6 dubbo.consumer.cluster = failfast 失效</a>
     *
     * ```
     *   @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, check = true
     *             , retries = 2, cluster = "failfast")
     *   private ProviderFirstApi firstApi;
     * ```
     *
     * exception:
     * ```
     * org.apache.dubbo.rpc.RpcException: Failed to invoke the method sayHello in the service com.vergilyn.examples.api.ProviderFirstApi.
     *     Tried 3 times of the providers ...
     *
     *   at org.apache.dubbo.rpc.cluster.support.FailoverClusterInvoker.doInvoke(FailoverClusterInvoker.java:119)
     *   at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.invoke(AbstractClusterInvoker.java:264)
     *   at org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor.intercept(ClusterInterceptor.java:51)
     *   at org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster$InterceptorInvokerNode.invoke(AbstractCluster.java:108)
     *   at org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker.invoke(MockClusterInvoker.java:86)
     *   at org.apache.dubbo.rpc.proxy.InvokerInvocationHandler.invoke(InvokerInvocationHandler.java:96)
     *   at org.apache.dubbo.common.bytecode.proxy0.sayHello(proxy0.java)
     *   at com.vergilyn.examples.consumer.issues.ClusterFailoverTest.test(ClusterFailoverTest.java:48)
     * 	 ....
     * ```
     *
     * 虽然配置的是`failfast`，但实际是`failover`
     */
    @Test
    public void issues6108(){

    }
}
