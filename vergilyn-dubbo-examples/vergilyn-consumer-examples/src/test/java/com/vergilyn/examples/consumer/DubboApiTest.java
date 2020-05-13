package com.vergilyn.examples.consumer;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.ProviderFirstApi;
import com.vergilyn.examples.api.ProviderSecondApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vergilyn
 * @date 2020-05-13
 */
@Slf4j
public class DubboApiTest extends AbstractSpringBootTest{

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = false, retries = 3)
    private ProviderFirstApi firstApi;
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 1000, check = true)
    private ProviderSecondApi secondApi;

    @Test
    public void first(){
        String rs = firstApi.sayHello("vergilyn", 100);

        log.info("firstApi.sayHello(...) response >>>> {}", rs);
    }

    @Test
    public void second(){
        String str = "vergilyn";
        String rs = secondApi.print(str);

        log.info("secondApi.print(...) response >>>> {}", rs);

        Assertions.assertTrue(() -> StringUtils.contains(rs, "ProviderSecondApi")
                                && StringUtils.contains(rs, str));
    }
}
