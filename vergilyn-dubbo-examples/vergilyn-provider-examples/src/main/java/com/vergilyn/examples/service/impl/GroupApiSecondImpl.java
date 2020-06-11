package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.GroupApi;

import lombok.extern.slf4j.Slf4j;

import static com.vergilyn.examples.api.GroupApi.GROUP_SECOND;

/**
 * @author vergilyn
 * @date 2020-06-11
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION, group = GROUP_SECOND)
@Slf4j
public class GroupApiSecondImpl implements GroupApi {

    @Override
    public String invoke(String param) {
        return GROUP_SECOND + ": " + param;
    }
}
