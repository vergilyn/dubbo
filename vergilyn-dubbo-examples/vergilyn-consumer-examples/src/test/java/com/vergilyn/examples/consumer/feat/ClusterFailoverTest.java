package com.vergilyn.examples.consumer.feat;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/fault-tolerent-strategy.html">dubbo 集群容错</a>
 *
 * @author vergilyn
 * @date 2020-05-18
 * @see org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster
 * @see org.apache.dubbo.rpc.cluster.support.BroadcastCluster
 * @see org.apache.dubbo.rpc.cluster.support.FailbackCluster "failback"
 * @see org.apache.dubbo.rpc.cluster.support.FailfastCluster "failfast"
 * @see org.apache.dubbo.rpc.cluster.support.FailoverCluster "failover"
 * @see org.apache.dubbo.rpc.cluster.support.FailsafeCluster "failsafe"
 * @see org.apache.dubbo.rpc.cluster.support.ForkingCluster  "forking"
 * @see org.apache.dubbo.rpc.cluster.support.MergeableCluster "mergeable"
 * @see org.apache.dubbo.rpc.cluster.support.registry.ZoneAwareCluster "zone-aware"
 */
@Slf4j
public class ClusterFailoverTest extends AbstractSpringBootTest {

    /*
     * 集群容错方案     说明
     * failover       失败自动切换，自动重试其它服务器（默认）
     * failfast       快速失败，立即报错，只发起一次调用
     * failsafe       失败安全，出现异常时，直接忽略
     * failback       失败自动恢复，记录失败请求，定时重发
     * forking        并行调用多个服务器，只要一个成功即返回
     * broadcast      广播逐个调用所有提供者，任意一个报错则报错
     */
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, check = true
            , retries = 2, cluster = "failover")
    private ProviderFirstApi firstApi;

    @Test
    public void test(){
        String rs = firstApi.sayHello("failover", 1000);

        System.out.println(rs);
    }
}
