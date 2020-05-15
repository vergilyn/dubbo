package com.vergilyn.examples.service.impl;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.CacheApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author vergilyn
 * @date 2020-05-14
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
@Slf4j
public class CacheApiImpl implements CacheApi {
    private static final AtomicInteger _index = new AtomicInteger(0);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public int resetProviderIndex() {
        _index.set(0);
        return _index.get();
    }

    @Override
    public Map<String, Object> consumerSideCache(Map<String, Object> param) {
        Map<String, Object> rs = Maps.newHashMap();
        rs.put("param", param);

        Map<String, Object> provider = Maps.newHashMap();
        provider.put("index", _index.incrementAndGet());
        provider.put("time", LocalTime.now().toString());

        rs.put("provider", provider);
        return rs;
    }
}
