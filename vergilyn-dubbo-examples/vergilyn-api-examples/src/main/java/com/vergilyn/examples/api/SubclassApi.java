package com.vergilyn.examples.api;

import com.vergilyn.examples.api.dto.ParentDto;

/**
 * <a href="https://github.com/apache/dubbo/issues/6112">issues#6112, GenericService Invoke Don't Support Subclasses</a>
 * @author vergilyn
 * @date 2020-05-07
 *
 * @see org.apache.dubbo.common.utils.ReflectUtils#findMethodByMethodSignature(java.lang.Class, java.lang.String, java.lang.String[])
 */
public interface SubclassApi {

    boolean hello(ParentDto dto);


}
