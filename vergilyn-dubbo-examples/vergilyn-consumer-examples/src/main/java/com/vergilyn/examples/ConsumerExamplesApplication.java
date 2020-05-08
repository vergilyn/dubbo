package com.vergilyn.examples;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.api.ProviderSecondApi;
import com.vergilyn.examples.api.SubclassApi;
import com.vergilyn.examples.api.dto.ChildDto;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FIXME 2020-05-08 test-unit
 * @author VergiLyn
 * @date 2019-11-28
 */
@SpringBootApplication
@Slf4j
public class ConsumerExamplesApplication implements CommandLineRunner {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = false, retries = 3)
    private ProviderFirstApi firstApi;
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private ProviderSecondApi secondApi;
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private SubclassApi subclassApi;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerExamplesApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        log.info(firstApi.sayHello("vergilyn", 100));
//        log.info(secondApi.print("vergilyn"));

        /**
         * <a href="https://github.com/apache/dubbo/issues/6112">GenericService Invoke Don't Support Subclasses</a>
         * 通过 proxy-class rpc invoke 正常，并未用到 {@linkplain ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])}
         * 所以不会出现 {@linkplain Class#getMethod(String, Class[])} throw {@linkplain NoSuchMethodException}
         */
        log.info(subclassApi.hello(new ChildDto()) + "");

        log.info(">>>> finish <<<<");
    }
}
