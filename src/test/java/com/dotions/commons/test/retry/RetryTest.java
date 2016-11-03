package com.dotions.commons.test.retry;


import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.dotions.commons.retry.Action;
import com.dotions.commons.retry.RetryTemplate;

public class RetryTest {

    @Test
    public void test() throws InterruptedException {
        
        RetryTemplate retryTemplate = new RetryTemplate();
        
        final Random r = new Random();
        
        retryTemplate.retry(new Action() {
            public void retry() {
                System.out.println("Establish connection ..........");
                if(r.nextInt() % 3 != 0) {
                    throw new RuntimeException("Establish connection fail.");
                }
                System.out.println("Establish connection success");
            }
            
            public void fail() {
                System.out.println("Retry task fail...... rollback....");
            }
            
            public String getName() {
                return "test";
            }
        }, 20);
        
        Assert.assertTrue(true);
    }

}
