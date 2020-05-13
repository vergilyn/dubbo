package com.vergilyn.examples.consumer;

import com.vergilyn.examples.ConsumerExamplesApplication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author vergilyn
 * @date 2020-05-13
 */
@SpringBootTest(classes = ConsumerExamplesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class AbstractSpringBootTest {
}
