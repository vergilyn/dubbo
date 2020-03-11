package com.vergilyn.examples.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author vergilyn
 * @date 2020-03-10
 */
@Configuration
@EnableDubbo(scanBasePackages = "com.vergilyn.examples.service", multipleConfig = true)
@PropertySource("classpath:/dubbo-consumer.properties")
public class ConsumerAutoConfiguration {

}
