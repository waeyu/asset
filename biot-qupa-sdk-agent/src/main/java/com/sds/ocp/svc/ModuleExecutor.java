package com.sds.ocp.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("moduleExecutor")
public class ModuleExecutor {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LocatorInfoService locatorInfoService;
	
	@Autowired
	private TagInfoService tagInfoService;

    
    public void locatorExecute() {    	
    	try {
    		locatorInfoService.action();
    	}
    	catch ( Exception e) {
    		logger.error("locatorInfoService.action() fail.",e);
    	}
    }
    
    public void tagExecute() {    	
    	try {
    		tagInfoService.action();
    	}
    	catch ( Exception e) {
    		logger.error("tagInfoService.action() fail.",e);
    	}
    }

}