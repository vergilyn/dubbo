package com.vergilyn.examples.consumer;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/service-downgrade.html">dubbo 服务降级</a>
 *
 * <br/>
 * FAQ:
 * 1. Not found class ProviderFirstApiMock, cause: mock="ProviderFirstApiMock"
 * <a href="https://www.jianshu.com/p/ce8de35986cf">dubbo的Mock功能与源码实现</a>
 *
 * 2. (FIXME 2020-05-13)
 * ProviderFirstApiMock 无法使用SpEL, serviceName = null
 * @author vergilyn
 * @date 2020-05-13
 */
@Slf4j
public class DubboMockTest extends AbstractSpringBootTest{

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 100, retries = 1
            , mock = "com.vergilyn.examples.mock.ProviderFirstApiMock")
    private ProviderFirstApi firstApi;

    @Test
    public void mock(){
        String rs = firstApi.sayHello("vergilyn", 1000);

        log.info("firstApi.sayHello(...) response >>>> {}", rs);

    }
}
