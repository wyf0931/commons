package com.dotions.commons.test.retry;


import java.util.Random;

import com.dotions.commons.retry.RetryAction;
import com.dotions.commons.retry.RetryTemplate;

public class RetryTest {

    public static void main(String[] args) throws InterruptedException {
        
        RetryTemplate retryTemplate = new RetryTemplate();
        
        final Random r = new Random();
        
        retryTemplate.retry(new RetryAction() {
            @Override
            public void retry() {
                System.out.println("Establish connection ..........");
                if(r.nextInt() % 3 != 0) {
                    throw new RuntimeException("Establish connection fail.");
                }
            }
            
            @Override
            public void fail() {
                System.out.println("Retry task fail...... rollback....");
            }
            
            @Override
            public String getName() {
                return "test";
            }
        }, 20);
    }

}
