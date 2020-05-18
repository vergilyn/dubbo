package com.vergilyn.examples.service;

import com.vergilyn.examples.api.dto.ParentDto;

/**
 * @author vergilyn
 * @date 2020-05-14
 */
public interface GenericApi {
    ParentDto invoke(ParentDto dto);

    String invoke(String param);

    boolean invoke();
}
