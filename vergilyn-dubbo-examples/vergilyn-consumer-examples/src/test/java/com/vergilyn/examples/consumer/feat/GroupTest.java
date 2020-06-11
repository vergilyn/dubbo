package com.vergilyn.examples.consumer.feat;

import com.vergilyn.examples.api.ApiConstants;
import com.vergilyn.examples.api.GroupApi;
import com.vergilyn.examples.consumer.AbstractSpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;

import static com.vergilyn.examples.api.GroupApi.GROUP_FIRST;
import static com.vergilyn.examples.api.GroupApi.GROUP_SECOND;

/**
 * <a href="http://dubbo.apache.org/zh-cn/docs/user/demos/service-group.html">服务分组</a>：
 * 当一个接口有多种实现时，可以用 group 区分。
 * <pre>
 *
 * </pre>
 *
 * @author vergilyn
 * @date 2020-06-11
 */
@Slf4j
public class GroupTest extends AbstractSpringBootTest {

    @Reference(version = ApiConstants.SERVICE_VERSION, group = GROUP_FIRST, check = false)
    private GroupApi firstGroupApi;

    @Reference(version = ApiConstants.SERVICE_VERSION, group = GROUP_SECOND, check = false)
    private GroupApi secondGroupApi;

    @Test
    public void firstGroupTest(){
        String first = firstGroupApi.invoke("first");
        System.out.println("first invoke >>>> " + first);
    }

    @Test
    public void secondGroupTest(){
        String second = secondGroupApi.invoke("second");
        System.out.println("second invoke >>>> " + second);
    }

}
