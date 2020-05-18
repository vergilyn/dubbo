package com.vergilyn.examples.consumer;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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


    protected void sleep(long ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    protected void preventExit(){
        try {
            new Semaphore(0).acquire();
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
