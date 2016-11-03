package com.dotions.commons.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description Retry action template.
 * 
 * <p>
 *   scene: Connection(JDBC, Network) fail, retry.
 * </p>
 * @author wangyunfei 2016-10-25
 * @version 1.0
 */
public class RetryTemplate {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryTemplate.class);
    
    private static final int DEFAULT_RETRY_TIMES = 1000;
    
    /**
     * Retry specific action.
     * 
     * <p>Default retry times limit is 1000.</p>
     * @param action The action which be retry
     * */
    public void retry(Action action) {
        retry(action, DEFAULT_RETRY_TIMES);
    }
    
    /**
     * Retry specific action. 
     * <p>When action execute not throws exception, 
     * we sure the action execute success.<p>
     * 
     * @param action The action which be retry
     * @param retryTimes Retry times limit
     * */
    public void retry(Action action, int retryTimes) {
        int times = 1;
        int sleep = 0;
        boolean flag = false;
        
        logger.info("Start retry task, actionName={}", action.getName());

        while (times <= retryTimes) {
            
            if (times < 90) {
                sleep = (int) Math.ceil(Math.tan(Math.toRadians(times)) * 10000);
            }            
            
            // whether or not throw execption
            try {
                // do something, atomic operation
                action.retry();
                flag = true;
            } catch (Exception e) {
                logger.warn("Retry action " + action.getName() + " fail, retryTimes=" + times, e);
            }
            
            // Check whether or not success.
            if(flag) {
                logger.info("Retry action {} success, retryTimes={}", action.getName(), times);
                break;
            }
            
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("Retry action {}, Sleep {}ms", action.getName(), sleep);
                }
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                // ignore
            }

            times++;
        }
        
        // fail
        if(!flag) {
            action.fail();
            logger.info("Retry action {} fail, retryTimes has reached the upper limit, retryTimes={}", action.getName(), retryTimes);
        }
        
    }
}