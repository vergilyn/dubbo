package com.vergilyn.examples.service;

import java.util.concurrent.TimeUnit;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author VergiLyn
 * @date 2019-11-28
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION
        /*, methods = {@Method(name = "sayGoodbye", timeout = 250, retries = 0)}*/)
@Slf4j
public class ProviderFirstApiImpl implements ProviderFirstApi {

    /**
     * The default value of ${dubbo.application.name} is ${spring.application.name}
     */
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name) {
        sleep();
        log.info("sayHello(String name) >>>> arg: {}", name);
        return String.format("[%s][%s] >>>>>>>> Hello, %s", serviceName, this.getClass().getSimpleName(), name);
    }

    @Override
    public String sayGoodbye(String name) {
        sleep();
        return String.format("[%s][%s] >>>>>>>> Goodbye, %s", serviceName, this.getClass().getSimpleName(), name);
    }

    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}