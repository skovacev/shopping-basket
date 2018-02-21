/**
 * 
 */
package com.consumption.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author esinkov 31. sij 2018. -- Initial implementation 
 *
 */
public abstract class BaseClass {
    
    private Logger logger = LoggerFactory.getLogger(BaseClass.class);
    
    /**
     * Get logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }
}
