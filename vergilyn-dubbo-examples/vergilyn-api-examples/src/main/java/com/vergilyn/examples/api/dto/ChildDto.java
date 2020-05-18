package com.vergilyn.examples.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author vergilyn
 * @date 2020-05-07
 */
@Data
public class ChildDto extends ParentDto implements Serializable {
    private String cid;
}
