package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderSecondApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author vergilyn
 * @date 2020-03-20
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
@Slf4j
public class ProviderSecondApiImpl implements ProviderSecondApi {
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String print(String str) {
        log.info("print(String str) >>>> arg: {}", str);
        return String.format("[%s][%s] >>>>>>>> print, %s", serviceName, this.getClass().getSimpleName(),str);
    }
}
