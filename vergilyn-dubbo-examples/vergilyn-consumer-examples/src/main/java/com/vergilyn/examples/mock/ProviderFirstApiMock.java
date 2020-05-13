package com.vergilyn.examples.mock;

import com.vergilyn.examples.api.ProviderFirstApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author vergilyn
 * @date 2020-05-13
 */
@Component
@Slf4j
public class ProviderFirstApiMock implements ProviderFirstApi {
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name, long sleepMs) {

        String result = String.format("[%s][%s] >>>> Hello, %s",
                serviceName, this.getClass().getSimpleName(), name);

        log.info("result >>>> {}", result);
        return result;
    }
}
