package com.sds.ocp.svc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.sds.ocp.OcpException;
import com.sds.ocp.svc.dao.VisionDAO;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.svc.vo.Vision;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class VisionService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "ecc-vision-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "VisionPlayer";
	
	private VisionDAO visionDAO;
	
	@Autowired
	public void setVisionDAO(VisionDAO visionDAO) {
		this.visionDAO = visionDAO;
	}	
	
	@Value("#{comProperties['vision.rootThingName'] != null ? comProperties['vision.rootThingName'] : 'PlayerBatch.Root101' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}	

	@Override
	public void action() {
		
		logger.debug("action begin.");
		List<Vision> visionList = getList();
		logger.debug("getlist count : [{}]" , visionList.size() );
		for (Vision vision : visionList ) {
			sendMessage (vision);
		}
		
	}
	
	private List<Vision> getList() {
		return visionDAO.list();
	}
	
	public void sendMessage (Vision vision) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("pstatus", vision.getPstatus() );
	    message.addProperty("pip", vision.getPip() );
	    message.addProperty("pmac", vision.getPmac() );
	    message.addProperty("pver", vision.getPver() );
	    message.addProperty("pvolume", vision.getPvolume() );		   
	    
	    sendEdgeAttrMessage( getEdgeThingName(vision.getPid()) , message , System.currentTimeMillis() );

	}
	
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		
		connectPlatform();		
		addEdgeThings();	
		
	}
	
	private void addEdgeThings() {
		
		logger.debug("addEdgeThings begin.");
		
		File file =  PropertiesUtil.getPropertiesFile( PROPERTIES_THINGLIST_FILE_NAME );
		
		String thingListStr = getPropertiesValue( file , PROPERTIES_THINGLIST_FILE_NAME , PROPERTIES_THINGLIST_KEY );
		List<Object> thingList = null;
		if(!StringUtil.isEmpty(thingListStr)) {
			thingList = JsonUtil.fromJsonToList(thingListStr.getBytes());
		}
		else {
			thingList = new ArrayList<Object>();
		}
		
		List<Vision> visionList = visionDAO.list();
		if( thingList.size() < visionList.size() ) {
			logger.debug("thingList size is small than visionList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(Vision vision : visionList) {
				logger.debug(vision.toString());
				if( !thingList.contains( vision.getPid() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(vision.getPid()));
					reqEdgeThing.setThingName(getEdgeThingName(vision.getPid()));
					reqEdgeThing.setThingNickName(vision.getPname());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(vision.getPid());
				}
			}
			
			devicedSendLoop(edgeThingList, addThingList, EDGE_MODEL_NAME.length() ) ;
			
			thingList.addAll(addThingList);
			setPropertiesValue( file , PROPERTIES_THINGLIST_KEY , JsonUtil.toJson(thingList) );
			
		}
		
	}
	
	private String getEdgeThingName(String pid) {
		return EDGE_MODEL_NAME + "." + String.valueOf(pid);
	}

}
