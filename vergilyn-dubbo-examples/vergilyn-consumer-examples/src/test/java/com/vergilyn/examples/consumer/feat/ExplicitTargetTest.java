package com.vergilyn.examples.consumer.feat;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;

/**
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/explicit-target.html">直连提供者</a></li>
 *
 * <pre>
 *   在开发及测试环境下，经常需要绕过注册中心，只测试指定服务提供者，这时候可能需要点对点直连，
 *   点对点直连方式，将以服务接口为单位，忽略注册中心的提供者列表，A 接口配置点对点，不影响 B 接口从注册中心获取列表。
 * </pre>
 *
 * @author vergilyn
 * @date 2020-05-18
 */
@Slf4j
public class ExplicitTargetTest extends AbstractSpringBootTest {

    // 1. 配置 url 指向提供者，将绕过注册中心，多个地址用分号隔开
    // 2. 在 JVM 启动参数中加入-D参数映射服务地址 java -Dcom.alibaba.xxx.XxxService=dubbo://localhost:20890
    // 3. properties配置文件映射 com.alibaba.xxx.XxxService=dubbo://localhost:20890
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = false, retries = 3
            , url = "dubbo://localhost:20880")
    private ProviderFirstApi firstApi;

    @Test
    public void test(){
        String rs = firstApi.sayHello("vergilyn", 100);

        System.out.println(rs);
    }
}
