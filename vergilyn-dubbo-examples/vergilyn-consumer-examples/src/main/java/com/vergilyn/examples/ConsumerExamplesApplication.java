package com.vergilyn.examples;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderServiceApi;

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

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 2000)
    private ProviderServiceApi providerServiceApi;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerExamplesApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(providerServiceApi.sayHello("vergilyn"));
    }
}
