package com.vergilyn.examples.api;

/**
 * @author vergilyn
 * @date 2020-06-11
 */
public interface GroupApi {
    String GROUP_FIRST = "group-first";
    String GROUP_SECOND = "group-second";

    String invoke(String param);
}
