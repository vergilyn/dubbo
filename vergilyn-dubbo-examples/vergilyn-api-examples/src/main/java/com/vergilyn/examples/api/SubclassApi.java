package com.vergilyn.examples.api;

import java.lang.reflect.Method;

import com.vergilyn.examples.api.dto.ChildDto;
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

    public static void main(String[] args) throws NoSuchMethodException {
        String methodName = "hello";

        // 注意看 `getMethod` javadoc
        Method p = SubclassApi.class.getMethod(methodName, ParentDto.class);
        System.out.println(p.getName());

        Method s = SubclassApi.class.getMethod(methodName, ChildDto.class.getSuperclass());
        System.out.println(s.getName());

        // java.lang.NoSuchMethodException: com.vergilyn.examples.api.SubclassApi.hello(com.vergilyn.examples.api.dto.ChildDto)
        Method h = SubclassApi.class.getMethod(methodName, ChildDto.class);
        System.out.println(h.getName());
    }
}
