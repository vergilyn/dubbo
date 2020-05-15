package com.vergilyn.examples.api;

import java.util.Map;

/**
 * @author vergilyn
 * @date 2020-05-14
 */
public interface CacheApi {

    int resetProviderIndex();

    Map<String, Object> consumerSideCache(Map<String, Object> param);
}
