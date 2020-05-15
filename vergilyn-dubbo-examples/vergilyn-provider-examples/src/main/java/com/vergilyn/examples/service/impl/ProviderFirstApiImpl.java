package com.vergilyn.examples.service.impl;

import java.time.LocalTime;
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
    public String sayHello(String name, long sleepMs) {
        LocalTime begin = LocalTime.now();
        sleep(sleepMs);
        LocalTime end = LocalTime.now();

        String result = String.format("[%s][%s][%s][%s] >>>> Hello, %s",
                serviceName, this.getClass().getSimpleName(),
                begin.toString(), end.toString(), name);

        log.info("result >>>> {}", result);
        return result;
    }

    @Override
    public String sayGoodbye(String name, long sleepMs) {
        LocalTime begin = LocalTime.now();
        sleep(sleepMs);
        LocalTime end = LocalTime.now();

        String result = String.format("[%s][%s][%s][%s] >>>> Goodbye, %s",
                serviceName, this.getClass().getSimpleName(),
                begin.toString(), end.toString(), name);

        log.info("result >>>> {}", result);
        return result;
    }

    private void sleep(long sleepMs){
        if (sleepMs <= 0){
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(sleepMs);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}