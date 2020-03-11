package com.vergilyn.examples.service;

import java.util.concurrent.TimeUnit;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderServiceApi;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author VergiLyn
 * @date 2019-11-28
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION
        /*, methods = {@Method(name = "sayGoodbye", timeout = 250, retries = 0)}*/)
public class ProviderServiceApiImpl implements ProviderServiceApi {

    /**
     * The default value of ${dubbo.application.name} is ${spring.application.name}
     */
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name) {
        sleep();
        return String.format("[%s] >>>>>>>> Hello, %s", serviceName, name);
    }

    @Override
    public String sayGoodbye(String name) {
        sleep();
        return String.format("[%s] >>>>>>>> Goodbye, %s", serviceName, name);
    }

    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}