package com.vergilyn.examples.consumer.issues;

import java.util.Map;

import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.Test;

/**
 * <li><a href="https://github.com/apache/dubbo/issues/6162">issues#6162, RpcContext Attatchment Bug in `ObjectToStringMap` for dubbo `2.7.6` </a>
 * <li><a href="https://github.com/apache/dubbo/pull/6163">pull#6163, fix#6162</a>
 * <li><a href="https://github.com/apache/dubbo/pull/6210">(Merged)pull#6210, fix object attachments iteration bug </a>
 *
 * @author vergilyn
 * @date 2020-05-20
 *
 * @see RpcContext#getAttachments()
 * @see RpcContext#getObjectAttachments()
 */

public class RpcContextAttachmentsMainTest {

    /**
     * FIXED 2020-05-28
     * <a href="https://github.com/apache/dubbo/pull/6210">(Merged)pull#6210, fix object attachments iteration bug </a>
     */
    @Test
    public void issues6162(){
        /* `getAttachments()` 每次都会 `new ObjectToStringMap(this.getObjectAttachments())`
         * 对map进行一层包装，所以`entrySet`为null
         */
        RpcContext.getContext().getAttachments().put("a", "b");

        System.out.println("`getAttachments()` before >>>> ");
        for (Map.Entry<String, String> entry : RpcContext.getContext().getAttachments().entrySet()) {
            System.out.println(entry.getKey() + "==>" + entry.getValue());
        }
        System.out.println("`getAttachments()` after >>>> ");
    }

    @Test
    public void insteadOf(){
        RpcContext.getContext().getObjectAttachments().put("a", "b");
        System.out.println("`getObjectAttachments()` before >>>> ");
        for (Map.Entry<String, Object> entry : RpcContext.getContext().getObjectAttachments().entrySet()) {
            System.out.println(entry.getKey() + "==>" + entry.getValue());
        }
        System.out.println("`getObjectAttachments()` after >>>> ");

    }
}
