package com.vergilyn.examples.consumer.issues;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.support.MockInvoker;
import org.junit.jupiter.api.Test;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/service-downgrade.html">dubbo 服务降级</a>
 *
 * <br/>
 * FAQ:
 * 1. Not found class ProviderFirstApiMock, cause: mock="ProviderFirstApiMock"
 * <a href="https://www.jianshu.com/p/ce8de35986cf">dubbo的Mock功能与源码实现</a>
 * 应该是 mock = "com.vergilyn.examples.mock.ProviderFirstApiMock"
 *
 * 2. (FIXME 2020-05-13)
 * ProviderFirstApiMock 无法使用SpEL（或@Autowried），serviceName = null。
 * 通过源码{@linkplain MockInvoker#invoke(Invocation)} -> {@linkplain MockInvoker#getMockObject(String, Class)}可知，
 * `xxxMock` 只是简单的 {@linkplain Class#newInstance()}
 *
 * @author vergilyn
 * @date 2020-05-13
 *
 * @see MockInvoker#invoke(org.apache.dubbo.rpc.Invocation)
 * @see MockInvoker#getMockObject(String, Class)
 * @see org.apache.dubbo.rpc.support.MockInvokerTest
 */
@Slf4j
public class ServiceDowngradeTest extends AbstractSpringBootTest {

    /**
     * force: 屏蔽请求，直接返回某个值
     * fail: 许请求，在请求失败的时候，再返回某个值
     *
     * Normalize mock string:
     *
     * <ol>
     * <li>return => return null</li>
     * <li>fail => default</li>
     * <li>force => default</li>
     * <li>fail:throw/return foo => throw/return foo</li>
     * <li>force:throw/return foo => throw/return foo</li>
     * </ol>
     */
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, retries = 1
            , mock = "com.vergilyn.examples.mock.ProviderFirstApiMock")
    private ProviderFirstApi firstApi;

    @Test
    public void mock(){
        String rs = firstApi.sayHello("vergilyn", 1000);

        log.info("firstApi.sayHello(...) response >>>> {}", rs);
    }
}
