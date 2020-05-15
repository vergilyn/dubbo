package com.vergilyn.examples.consumer.feat;

import java.lang.reflect.Method;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.SubclassApi;
import com.vergilyn.examples.api.dto.ChildDto;
import com.vergilyn.examples.api.dto.ParentDto;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <a href="https://github.com/apache/dubbo/issues/6112">issues#6112, GenericService Invoke Don't Support Subclasses</a>
 * @author vergilyn
 * @date 2020-05-13
 *
 * @see org.apache.dubbo.common.utils.ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])
 */
@Slf4j
public class SubclassParamTest extends AbstractSpringBootTest {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private SubclassApi subclassApi;


    /**
     * 通过 proxy-class rpc invoke 正常，并未用到 {@linkplain ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])}
     * 所以不会出现 {@linkplain Class#getMethod(String, Class[])} throw {@linkplain NoSuchMethodException}
     */
    @Test
    public void test(){
        boolean hello = subclassApi.hello(new ChildDto());

        Assertions.assertTrue(hello);
    }

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
}
