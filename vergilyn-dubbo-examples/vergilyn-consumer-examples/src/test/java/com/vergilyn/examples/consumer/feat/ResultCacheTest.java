package com.vergilyn.examples.consumer.feat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.CacheApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.collections.Lists;
import org.testng.collections.Maps;

/**
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/user/demos/result-cache.html">dubbo 结果缓存</a></li>
 * <li><a href="http://dubbo.apache.org/zh-cn/docs/dev/impls/cache.html">dubbo 缓存扩展</a></li>
 *
 * <p> lru: 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
 * <p> threadlocal: 当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
 * <p> jcache: 与JSR107集成，可以桥接各种缓存实现。
 *
 * <p>
 *
 * <p> key 生成规则规则：{@linkplain org.apache.dubbo.cache.filter.CacheFilter#invoke(Invoker, Invocation)} -> {@linkplain StringUtils#toArgumentString(java.lang.Object[])}
 * <p> （底层是 JSON.toJsonString(..) 而不是 Object.toString()）
 *
 * @author vergilyn
 * @date 2020-05-1
 *
 * @see org.apache.dubbo.cache.filter.CacheFilter CacheFilter
 *
 * @see org.apache.dubbo.cache.CacheFactory CacheFactory
 * @see org.apache.dubbo.cache.support.jcache.JCacheFactory JCacheFactory
 * @see org.apache.dubbo.cache.support.lru.LruCacheFactory LruCacheFactory
 * @see org.apache.dubbo.cache.support.threadlocal.ThreadLocalCacheFactory ThreadLocalCacheFactory
 * @see org.apache.dubbo.cache.support.expiring.ExpiringCacheFactory ExpiringCacheFactory
 *
 */
@Slf4j
public class ResultCacheTest extends AbstractSpringBootTest {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * legal values include: lru, threadlocal, jcache
     */
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 3600_000, retries = 1
            , cache = "lru")
    private CacheApi lruCacheApi;

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 3600_000, retries = 1
            , cache = "threadlocal")
    private CacheApi threadlocalCacheApi;


    private static final AtomicInteger _INDEX = new AtomicInteger(0);

    @BeforeEach
    public void beforeEach(){
        lruCacheApi.resetProviderIndex();
    }

    /**
     * 次请求参数不一样，所以 cache-key 不一样，会多次请求
     */
    @Test
    // @RepeatedTest(5)
    public void unusedCache(){
        List<Map<String, Object>> rs = Lists.newArrayList();

        for (int i = 1, len = 5; i <= len; i++){
            Map<String, Object> param = Maps.newHashMap();
            param.put("index", _INDEX.incrementAndGet());

            Map<String, Object> map = lruCacheApi.consumerSideCache(param);
            System.out.println(map);
            rs.add(map);
        }

        Integer[] actual = rs.stream().map(map -> {
            Map<String, Object> provider = (Map<String, Object>) map.get("provider");
            return provider.get("index");
        }).toArray(Integer[]::new);

        Assertions.assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, actual);
    }

    /**
     * 每次请求参数一样，所以 cache-key 一样，只请求1次，第2+次使用consumer-side cache
     */
    @Test
    public void usedCache(){
        List<Map<String, Object>> rs = Lists.newArrayList();

        for (int i = 1, len = 5; i <= len; i++){
            Map<String, Object> param = Maps.newHashMap();
            param.put("index", 0);

            Map<String, Object> map = lruCacheApi.consumerSideCache(param);
            System.out.println(map);
            rs.add(map);
        }

        Integer[] actual = rs.stream().map(map -> {
            Map<String, Object> provider = (Map<String, Object>) map.get("provider");
            return provider.get("index");
        }).toArray(Integer[]::new);

        Assertions.assertArrayEquals(new Integer[]{1, 1, 1, 1, 1}, actual);
    }
}
