package com.vergilyn.examples.api;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author vergilyn
 * @date 2020-05-18
 */
public interface AsyncApi {

    /**
     * 定义CompletableFuture签名的接口
     */
    CompletableFuture<Map<String, Map<String, Object>>> future(LocalTime time, long ms);

    /**
     * Dubbo提供了一个类似Serverlet 3.0的异步接口AsyncContext，在没有CompletableFuture签名接口的情况下，也可以实现Provider端的异步执行
     */
    Map<String, Map<String, Object>> nonFuture(LocalTime time, long ms);

    /**
     * 利用Java 8提供的default接口实现，重载一个带有CompletableFuture签名的方法。
     */
    Map<String, Map<String, Object>> jdk8(LocalTime time, long ms);


    // `justForOverride` is totally optional, you can use any parameter type as long as java allows your to do that.
    default CompletableFuture<Map<String, Map<String, Object>>> jdk8(LocalTime time, long ms, boolean justForOverride){
        return CompletableFuture.completedFuture(jdk8(time, ms));
    }
}
