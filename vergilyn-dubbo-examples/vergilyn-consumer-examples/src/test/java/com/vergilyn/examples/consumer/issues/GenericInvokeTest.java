package com.vergilyn.examples.consumer.issues;

import java.lang.reflect.Method;
import java.util.Map;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.SubclassApi;
import com.vergilyn.examples.api.dto.ChildDto;
import com.vergilyn.examples.api.dto.ParentDto;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testng.collections.Maps;

/**
 *
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/generic-reference.html">dubbo 使用泛化调用</a></li>
 * <pre>
 *   泛化接口调用方式主要用于客户端没有 API 接口及模型类元的情况，参数及返回值中的所有 POJO 均用 Map 表示，通常用于框架集成，
 *   比如：实现一个通用的服务测试框架，可通过 GenericService 调用所有服务实现。
 * </pre>
 *
 * <li><a href="https://github.com/apache/dubbo/issues/6112">issues#6112, GenericService Invoke Don't Support Subclasses</a></li>
 * <li><a href="https://github.com/apache/dubbo/issues/6152">issues#6152, dubbo consumer 端泛化调用provider端接口时，报 ClassNotFoundException 异常</a></li>
 *
 * @author vergilyn
 * @date 2020-05-14
 *
 * @see InvokerInvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
 * @see com.vergilyn.examples.service.impl.GenericApiImpl
 */
@Slf4j
public class GenericInvokeTest extends AbstractSpringBootTest {
    private static final String GENERIC_INTERFACE_NAME = "com.vergilyn.examples.service.GenericApi";

    @Reference(interfaceName = GENERIC_INTERFACE_NAME, version = ApiConstants.SERVICE_VERSION, generic = true)
    private org.apache.dubbo.rpc.service.GenericService annoGenericService;

    /**
     * <pre>
     *   org.apache.dubbo.rpc.RpcException: Failed to invoke the method invoke in the service org.apache.dubbo.rpc.service.GenericService. Tried 3 times of the providers [...] (1/1) from the registry localhost:8848 on the consumer ....,
     *     cause: org.apache.dubbo.remoting.RemotingException: org.apache.dubbo.rpc.RpcException: com.vergilyn.examples.service.GenericApi.invoke(com.vergilyn.examples.api.dto.ChildDto)
     *   org.apache.dubbo.rpc.RpcException: com.vergilyn.examples.service.GenericApi.invoke(com.vergilyn.examples.api.dto.ChildDto)
     *   ....
     * </pre>
     *
     * @see  <a href="https://github.com/apache/dubbo/issues/6112">issues#6112, GenericService Invoke Don't Support Subclasses</a>
     *
     * @see org.apache.dubbo.common.utils.ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])
     */
    @Test
    public void issues6112(){
        Map<String, Object> param = Maps.newHashMap();
        param.put("pid", "parent-id");
        param.put("cid", "child-id");

        Object actual = annoGenericService.$invoke("invoke", new String[]
                {"com.vergilyn.examples.api.dto.ChildDto"}, new Object[]{param});

        System.out.println(actual);
    }

    /**
     *
     * @see org.apache.dubbo.common.utils.ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])
     */
    public static void main(String[] args) throws NoSuchMethodException {
        String methodName = "hello";

        // 注意看 `getMethod` javadoc
        Method p = SubclassApi.class.getMethod(methodName, ParentDto.class);
        System.out.println(p.getName());

        Method s = SubclassApi.class.getMethod(methodName, ChildDto.class.getSuperclass());
        System.out.println(s.getName());

        // java.lang.NoSuchMethodException: com.vergilyn.examples.api.SubclassApi.hello(com.vergilyn.examples.api.dto.ChildDto)
        Method h = SubclassApi.class.getMethod(methodName, ChildDto.class);
        System.out.println(h.getName());
    }

    @Test
    public void apiReferenceGeneric(){
        // 引用远程服务
        // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        // 弱类型接口名
        reference.setInterface(GENERIC_INTERFACE_NAME);
        reference.setVersion(ApiConstants.SERVICE_VERSION);
        // 声明为泛化接口
        reference.setGeneric("true");

        // 用org.apache.dubbo.rpc.service.GenericService可以替代所有接口引用
        org.apache.dubbo.rpc.service.GenericService genericService = reference.get();

        // 基本类型以及Date,List,Map等不需要转换，直接调用
        Object actual = genericService.$invoke("invoke", new String[] {String.class.getName()}, new Object[] {"api"});
        System.out.println(actual);

        Assertions.assertEquals("[provider-application][GenericApiImpl] >>>>>>>> print, api", actual);
    }

    @Test
    public void annoGeneric(){
        Object actual = annoGenericService.$invoke("invoke", new String[] {String.class.getName()}, new Object[] {"annotation"});
        System.out.println(actual);

        Assertions.assertEquals("[provider-application][GenericApiImpl] >>>>>>>> print, annotation", actual);

    }

    @Test
    public void genericParam(){
        String clazz = "com.vergilyn.examples.api.dto.ParentDto";

        // 用Map表示POJO参数，如果返回值为POJO也将自动转成Map
        Map<String, Object> param = Maps.newHashMap();

        // 注意：如果参数类型是接口，或者List等丢失泛型，可通过class属性指定类型。
        //param.put("class", "com.xxx.PersonImpl");

        param.put("pid", "parent-id");
        param.put("cid", "child-id");

        // 如果返回POJO将自动转成Map
        Object actual = annoGenericService.$invoke("invoke", new String[]{clazz}, new Object[]{param});
        System.out.println(actual);

        Assertions.assertTrue(actual instanceof Map);

        Map<String, Object> rs = (Map<String, Object>) actual;

        Assertions.assertEquals(clazz, rs.get("class"));
        Assertions.assertEquals(param.get("pid"), rs.get("pid"));

    }

    /**
     * 泛化调用数组参数
     */
    @Test
    public void arrayParam(){
        String[] params = {"a", "b"};

        Object actual = annoGenericService.$invoke("array", new String[]
                {"java.lang.String[]"}, new Object[]{params});

        System.out.println(actual);

        byte[] param = "a".getBytes();

        actual = annoGenericService.$invoke("array", new String[]
                {"byte[]"}, new Object[]{param});

        System.out.println(actual);

    }
}
