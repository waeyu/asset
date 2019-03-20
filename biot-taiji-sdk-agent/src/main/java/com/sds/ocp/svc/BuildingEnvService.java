package com.sds.ocp.svc;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.sds.ocp.OcpException;
import com.sds.ocp.svc.dao.BuildingEnvDAO;
import com.sds.ocp.svc.vo.BuildingEnvData;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.svc.vo.TaijiDevice;
import com.sds.ocp.util.DateUtil;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class BuildingEnvService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "ecc-buildingenv-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "Ensensor";
	
	private BuildingEnvDAO buildingEnvDAO;

	@Autowired
	public void setBuildingEnvDAO(BuildingEnvDAO buildingEnvDAO) {
		this.buildingEnvDAO = buildingEnvDAO;
	}

	@Value("#{comProperties['buildingenv.rootThingName'] != null ? comProperties['buildingenv.rootThingName'] : 'IbmsBatch.Root101' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	@Value("#{comProperties['buildingenv.searchParamFileName'] != null ? comProperties['buildingenv.searchParamFileName'] : 'ecc-buildingenv.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Override
	public void action() {
		
		logger.debug("action begin.");
		long to = this.lastIngestTime;
		List<BuildingEnvData> buildingEnvDataList = getList();
		logger.debug("getlist count : [{}]" , buildingEnvDataList.size() );
		for (BuildingEnvData buildingEnv : buildingEnvDataList ) {
			sendMessage (buildingEnv);
			to = to < buildingEnv.getHisDate().getTime() ? buildingEnv.getHisDate().getTime() : to ;
		}
		try {
			this.lastIngestTime = to ;
			savePropertiesSet();
		} catch (Exception e) {
			logger.error("savePropertiesSet fail.",e);
		}	
		
	}
	
	private List<BuildingEnvData> getList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return buildingEnvDAO.buildingEnvDataList(new Timestamp(from), new Timestamp(to));

	}
	
	public void sendMessage (BuildingEnvData buildingEnvData) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("temperature", buildingEnvData.getTemperature() );	    
	    message.addProperty("humidity", buildingEnvData.getHumidity() );
	    message.addProperty("co", buildingEnvData.getCo() );
	    message.addProperty("co2", buildingEnvData.getCo2() );
	    message.addProperty("voc", buildingEnvData.getVoc() );
	    message.addProperty("pm25", buildingEnvData.getPm25() );
	    message.addProperty("noise", buildingEnvData.getNoise() );	   
	    
	    sendEdgeAttrMessage( getEdgeThingName(buildingEnvData.getDeviceCode() ) , message ,buildingEnvData.getHisDate().getTime() );

	}
	
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		
		initSearchParameter();	
		connectPlatform();		
		addEdgeThings();	
		
	}
	
	@PreDestroy
	public void savePropertiesSet() throws Exception {
		setPropertiesValue( this.searchParamPropertiesFile , SEARCH_PARAM_KEY , DateUtil.getTimeString(this.lastIngestTime) );
	}
	
	private void addEdgeThings() {
		
		logger.debug("addEdgeThings begin.");
		
		File file =  PropertiesUtil.getPropertiesFile( PROPERTIES_DIR , PROPERTIES_THINGLIST_FILE_NAME );
		
		String thingListStr = getPropertiesValue( file , PROPERTIES_THINGLIST_FILE_NAME , PROPERTIES_THINGLIST_KEY );
		List<Object> thingList = null;
		if(!StringUtil.isEmpty(thingListStr)) {
			thingList = JsonUtil.fromJsonToList(thingListStr.getBytes());
		}
		else {
			thingList = new ArrayList<Object>();
		}
		
		List<TaijiDevice> taijiDeviceList = buildingEnvDAO.listTaijiDevice();
		if( thingList.size() < taijiDeviceList.size() ) {
			logger.debug("thingList size is small than consumptionTermList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(TaijiDevice taijiDevice :  taijiDeviceList ) {
				logger.debug(taijiDevice.toString());
				if( !thingList.contains( taijiDevice.getDeviceCode() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(taijiDevice.getDeviceCode()));
					reqEdgeThing.setThingName(getEdgeThingName(taijiDevice.getDeviceCode()));
					reqEdgeThing.setThingNickName(taijiDevice.getDeviceCN());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(taijiDevice.getDeviceCode());
				}
			}
			
			devicedSendLoop(edgeThingList, addThingList, EDGE_MODEL_NAME.length() ) ;
			
			thingList.addAll(addThingList);
			setPropertiesValue( file , PROPERTIES_THINGLIST_KEY , JsonUtil.toJson(thingList) );
			
		}
		
	}	
	
	private String getEdgeThingName(String deviceCode) {
		return EDGE_MODEL_NAME + "." + String.valueOf(deviceCode);
	}

}
