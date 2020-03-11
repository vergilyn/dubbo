package com.vergilyn.examples.api;

/**
 * @author vergilyn
 * @date 2020-03-05
 */
public interface ProviderServiceApi {

    String sayHello(String name);

    default String sayGoodbye(String name) {
        return "Goodbye, " + name;
    }
}
