package com.vergilyn.examples.api;

/**
 * @author vergilyn
 * @date 2020-03-05
 */
public interface ProviderFirstApi {

    String sayHello(String name, long sleepMs);

    default String sayGoodbye(String name, long sleepMs) {
        return "Goodbye, " + name;
    }
}
