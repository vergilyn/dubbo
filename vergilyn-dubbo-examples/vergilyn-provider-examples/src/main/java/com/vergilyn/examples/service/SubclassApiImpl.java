package com.vergilyn.examples.service;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.SubclassApi;
import com.vergilyn.examples.api.dto.ParentDto;

/**
 * @author vergilyn
 * @date 2020-05-07
 */
@org.apache.dubbo.config.annotation.Service(version = ApiConstants.SERVICE_VERSION)
public class SubclassApiImpl implements SubclassApi {
    @Override
    public boolean hello(ParentDto dto) {
        return true;
    }


}
