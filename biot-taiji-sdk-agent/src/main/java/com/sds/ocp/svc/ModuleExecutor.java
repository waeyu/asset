package com.sds.ocp.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("moduleExecutor")
public class ModuleExecutor {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private VisitorInfoService visitorInfoService;
	
	@Autowired
	private ConsumptionInfoService consumptionInfoService;
	
	@Autowired
	private AccessInfoService accessInfoService;
	
	@Autowired
	private CarInOutInfoService carInOutInfoService;
	
	@Autowired
	private VisionService visionService;
	
	@Autowired
	private BuildingEnvService buildingEnvService;
	
	@Autowired
	private ComputerRoomEnvService computerRoomEnvService;
    
    public void visitorExecute() {    	
    	try {
    		visitorInfoService.action();
    	}
    	catch ( Exception e) {
    		logger.error("visitorInfoService.action() fail.",e);
    	}
    }
    
    public void consumptionExecute() {    	
    	try {
    		consumptionInfoService.action();
    	}
    	catch ( Exception e) {
    		logger.error("consumptionInfoService.action() fail.",e);
    	}
    }
    
    public void accessExecute() {    
    	try {
    		accessInfoService.action();
		}
		catch ( Exception e) {
			logger.error("accessInfoService.action() fail.",e);
		}
    }
    
    public void carInOutExecute() {    	
    	try { 
    		carInOutInfoService.action();
		}
		catch ( Exception e) {
			logger.error("carInOutInfoService.action() fail.",e);
		}
    }
    
    public void visionExecute() {    	
    	try {
	    	visionService.action();
		}
		catch ( Exception e) {
			logger.error("visionService.action() fail.",e);
		}
    }
    
    public void buildingEnvExecute() {    	
    	try {
    		buildingEnvService.action();
		}
		catch ( Exception e) {
			logger.error("buildingEnvService.action() fail.",e);
		}
    }


    public void computerRoomEnvExecute() {    	
    	try {
    		computerRoomEnvService.action();
		}
		catch ( Exception e) {
			logger.error("computerRoomEnvService.action() fail.",e);
		}
    }    
    

}