package com.vergilyn.examples.consumer.issues;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * TODO 2020-05-15
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/generic-reference.html">dubbo 使用泛化调用</a></li>
 *
 * <li><a href="https://github.com/apache/dubbo/issues/6112">issues#6112, GenericService Invoke Don't Support Subclasses</a></li>
 * <li><a href="https://github.com/apache/dubbo/issues/6152">issues#6152, dubbo consumer 端泛化调用provider端接口时，报 ClassNotFoundException 异常</a></li>
 *
 * @author vergilyn
 * @date 2020-05-14
 */
@Slf4j
public class GenericInvokeTest extends AbstractSpringBootTest {
    @Autowired
    private ApplicationContext applicationContext;

    private org.apache.dubbo.rpc.service.GenericService genericService;

    @BeforeEach
    public void beforeEach(){
        // 引用远程服务
        // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        // 弱类型接口名
        reference.setInterface("com.vergilyn.examples.service.impl.GenericApiImpl");
        reference.setVersion(ApiConstants.SERVICE_VERSION);
        // 声明为泛化接口
        reference.setGeneric("true");

        // 用org.apache.dubbo.rpc.service.GenericService可以替代所有接口引用
        genericService = reference.get();
    }

    @Test
    public void subclass(){

    }

    @Test
    public void generic(){

        // 基本类型以及Date,List,Map等不需要转换，直接调用
        Object result = genericService.$invoke("invoke", new String[] {String.class.getName()}, new Object[] {"vergilyn"});
        System.out.println();
    }
}
