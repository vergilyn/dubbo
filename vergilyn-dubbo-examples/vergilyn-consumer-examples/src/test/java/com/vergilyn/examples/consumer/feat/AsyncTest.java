package com.vergilyn.examples.consumer.feat;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.AsyncApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/async-call.html">dubbo 异步调用</a>
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/async-execute-on-provider.html"> dubbo provider 异步执行 </a>
 *
 * @author vergilyn
 * @date 2020-05-18
 */
@Slf4j
public class AsyncTest extends AbstractSpringBootTest {

    /*
     * sent="true" 等待消息发出，消息发送失败将抛出异常。
     * sent="false" 不等待消息发出，将消息放入 IO 队列，即刻返回。
     *
     * 如果你只是想异步，完全忽略返回值，可以配置 return="false"，以减少 Future 对象的创建和管理成本
     */
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 10_000, check = true, retries = 3
            , async = true, sent = false)
    private AsyncApi asyncApi;

    @AfterEach
    public void afterEach(){
        sleep(10_000);
    }

    @Test
    public void future1(){
        System.out.printf("async before >>>> %s \r\n", LocalTime.now());
        // 此调用会立即返回null
        asyncApi.future(LocalTime.now(), 1000);
        System.out.printf("async after >>>> %s \r\n", LocalTime.now());

        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
        // 基于 ThreadLocal 实现
        CompletableFuture<Map<String, Map<String, Object>>> future = RpcContext.getContext().getCompletableFuture();

        // 为Future添加回调
        future.whenComplete((retValue, exception) -> {
            if (exception == null) {
                System.out.printf("async complete >>>> %s, result: %s \r\n", LocalTime.now(), retValue);
            } else {
                exception.printStackTrace();
            }
        });
    }

    // 第2种async-call写法
    @Test
    public void future2() throws ExecutionException, InterruptedException {

        CompletableFuture<Map<String, Map<String, Object>>> future = RpcContext.getContext().asyncCall(new Callable<Map<String, Map<String, Object>>>() {
            @Override
            public Map<String, Map<String, Object>> call() throws Exception {
                return asyncApi.nonFuture(LocalTime.now(), 1000);
            }
        });

        System.out.printf("async complete >>>> %s, result: %s \r\n", LocalTime.now(), future.get());
    }

    @Test
    public void nonFuture(){
        // 与 future 一样
        asyncApi.nonFuture(LocalTime.now(), 1000);

        CompletableFuture<Map<String, Map<String, Object>>> future = RpcContext.getContext().getCompletableFuture();

        future.whenComplete((retValue, exception) -> {
            if (exception == null) {
                System.out.printf("async complete >>>> %s, result: %s \r\n", LocalTime.now(), retValue);
            } else {
                exception.printStackTrace();
            }
        });

    }

    @Test
    public void jdk8(){
        // 与 future 一样
        asyncApi.jdk8(LocalTime.now(), 1000, false);

        CompletableFuture<Map<String, Map<String, Object>>> future = RpcContext.getContext().getCompletableFuture();

        future.whenComplete((retValue, exception) -> {
            if (exception == null) {
                System.out.printf("async complete >>>> %s, result: %s \r\n", LocalTime.now(), retValue);
            } else {
                exception.printStackTrace();
            }
        });
    }
}
