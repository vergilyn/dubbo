package com.vergilyn.examples;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.api.ProviderSecondApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author VergiLyn
 * @date 2019-11-28
 */
@SpringBootApplication
@Slf4j
public class ConsumerExamplesApplication implements CommandLineRunner {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = false, retries = 1)
    private ProviderFirstApi firstApi;
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private ProviderSecondApi secondApi;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerExamplesApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(firstApi.sayHello("vergilyn", 1200));
        log.info(secondApi.print("vergilyn"));

        log.info(">>>> finish <<<<");
    }
}
