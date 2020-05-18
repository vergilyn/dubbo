package com.vergilyn.examples.consumer.feat;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.SubclassApi;
import com.vergilyn.examples.api.dto.ChildDto;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;
import com.vergilyn.examples.consumer.issues.GenericInvokeTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vergilyn
 * @date 2020-05-13
 *
 */
@Slf4j
public class SubclassParamTest extends AbstractSpringBootTest {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private SubclassApi subclassApi;


    /**
     * 通过 proxy-class rpc invoke 正常，并未用到 {@linkplain ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])}
     * 所以不会出现 {@linkplain Class#getMethod(String, Class[])} throw {@linkplain NoSuchMethodException}
     *
     * @see GenericInvokeTest#issues6112()
     */
    @Test
    public void test(){
        boolean hello = subclassApi.hello(new ChildDto());

        Assertions.assertTrue(hello);
    }

}
