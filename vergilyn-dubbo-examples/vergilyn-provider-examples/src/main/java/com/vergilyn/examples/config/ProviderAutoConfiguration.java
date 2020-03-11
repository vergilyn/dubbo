package com.vergilyn.examples.config;

import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author vergilyn
 * @date 2020-03-09
 */
@Configuration
@EnableDubbo(scanBasePackages = "com.vergilyn.examples.service", multipleConfig = true)
@PropertySource("classpath:/dubbo-provider.properties")
public class ProviderAutoConfiguration {

    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(1000);
        return providerConfig;
    }
}
