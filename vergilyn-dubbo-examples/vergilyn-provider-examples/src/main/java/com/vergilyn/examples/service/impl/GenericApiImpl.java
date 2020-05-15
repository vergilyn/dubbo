package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.api.dto.ParentDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vergilyn
 * @date 2020-05-14
 */
// @org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
@Slf4j
public class GenericApiImpl{

    public boolean invoke(ParentDto dto) {
        return true;
    }

    public String invoke(String param) {
        return String.format("[provider-application][%s] >>>>>>>> print, %s", this.getClass().getSimpleName(), param);
    }

    public boolean invoke() {
        return true;
    }
}
