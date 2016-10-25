package com.dotions.commons.retry;

/**
 * @Description Retry action definition.
 * @author wangyunfei 2016-10-25
 * @version 1.0
 */
public interface RetryAction {
    
    /**
     * Action Name.
     * */
    public String getName();
    
    /**
     * While retry fail, what we can do.
     * */
    public void fail();
    
    /**
     * Retry operation.
     * */
    public void retry();
    
}
