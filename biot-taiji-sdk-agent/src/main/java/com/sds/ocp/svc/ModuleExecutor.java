package com.sds.ocp.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("moduleExecutor")
public class ModuleExecutor {
	
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
    	visitorInfoService.action();
    }
    
    public void consumptionExecute() {    	
    	consumptionInfoService.action();
    }
    
    public void accessExecute() {    	
    	accessInfoService.action();
    }
    
    public void carInOutExecute() {    	
    	carInOutInfoService.action();
    }
    
    public void visionExecute() {    	
    	visionService.action();
    }
    
    public void buildingEnvExecute() {    	
    	buildingEnvService.action();
    }


    public void computerRoomEnvExecute() {    	
    	computerRoomEnvService.action();
    }    
    

}