package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.dto.ParentDto;
import com.vergilyn.examples.service.GenericApi;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vergilyn
 * @date 2020-05-14
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
@Slf4j
public class GenericApiImpl implements GenericApi {

    @Override
    public ParentDto invoke(ParentDto dto) {
        return dto;
    }

    @Override
    public String invoke(String param) {
        return String.format("[provider-application][%s] >>>>>>>> print, %s", this.getClass().getSimpleName(), param);
    }

    @Override
    public boolean invoke() {
        return true;
    }
}
