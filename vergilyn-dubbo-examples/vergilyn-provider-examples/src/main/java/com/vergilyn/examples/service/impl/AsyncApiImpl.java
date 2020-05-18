package com.vergilyn.examples.service.impl;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.AsyncApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.AsyncContext;
import org.apache.dubbo.rpc.RpcContext;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/async-execute-on-provider.html"> dubbo provider 异步执行 </a>
 *
 * @author vergilyn
 * @date 2020-05-18
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
@Slf4j
public class AsyncApiImpl implements AsyncApi {

    @Override
    public CompletableFuture<Map<String, Map<String, Object>>> future(LocalTime time, long ms) {
        sleep(ms);

        RpcContext savedContext = RpcContext.getContext();

        // 建议为supplyAsync提供自定义线程池，避免使用JDK公用线程池
        // 业务执行已从Dubbo线程切换到业务线程，避免了对Dubbo线程池的阻塞。
        return CompletableFuture.supplyAsync(() -> result("future", LocalTime.now(), time, ms));
    }

    @Override
    public Map<String, Map<String, Object>> nonFuture(LocalTime time, long ms) {
        sleep(ms);

        final AsyncContext asyncContext = RpcContext.startAsync();

        new Thread(() -> {
            // 如果要使用上下文，则必须要放在第一句执行
            asyncContext.signalContextSwitch();
            // 写回响应
            asyncContext.write(result("nonFuture", LocalTime.now(), time, ms));
        }).start();

        return null;
    }

    @Override
    public Map<String, Map<String, Object>> jdk8(LocalTime time, long ms) {
        sleep(ms);
        return result("jdk8", LocalTime.now(), time, ms);
    }


    private static Map<String, Map<String, Object>> result(String mark, LocalTime pt, LocalTime ct, long ms){
        Map<String, Map<String, Object>> result = Maps.newHashMap();

        Map<String, Object> provider = Maps.newHashMap();
        provider.put("mark", mark);
        provider.put("time", pt);
        result.put("provider", provider);

        Map<String, Object> consumer = Maps.newHashMap();
        consumer.put("ms", ms);
        consumer.put("time", ct);
        result.put("consumer", consumer);

        return result;
    }

    private void sleep(long ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            // do nothing
        }

    }
}
