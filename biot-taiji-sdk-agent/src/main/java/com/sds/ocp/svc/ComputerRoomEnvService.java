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
import com.sds.ocp.svc.dao.ComputerRoomEnvDAO;
import com.sds.ocp.svc.vo.LeakDetection;
import com.sds.ocp.svc.vo.PowerMeter;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.svc.vo.TaijiDevice;
import com.sds.ocp.util.DateUtil;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class ComputerRoomEnvService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "ecc-computerroomenv-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "PowerMeter";
	private static final String EDGE_MODEL_NAME2 				= "LeakDetect";
	
	private ComputerRoomEnvDAO computerRoomEnvDAO;

	@Autowired
	public void setComputerRoomEnvDAO(ComputerRoomEnvDAO computerRoomEnvDAO) {
		this.computerRoomEnvDAO = computerRoomEnvDAO;
	}	

	@Value("#{comProperties['computerroomenv.rootThingName'] != null ? comProperties['computerroomenv.rootThingName'] : 'dcmsBatch.Root102' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	@Value("#{comProperties['computerroomenv.searchParamFileName'] != null ? comProperties['computerroomenv.searchParamFileName'] : 'ecc-computerroomenv.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Override
	public void action() {
		
		long powerMeterTo = this.lastIngestTime ;
		List<PowerMeter> powerMeterList = getPowerMeterList();
		logger.debug("getPowerMeterList count : [{}]" , powerMeterList.size() );
		for (PowerMeter powerMeter : powerMeterList ) {
			sendPowerMeterMessage (powerMeter);
			powerMeterTo = powerMeterTo < powerMeter.getHisDate().getTime() ? powerMeter.getHisDate().getTime() : powerMeterTo ;
		}
		
		long leakDetectTo = this.lastIngestTime ;
		List<LeakDetection> leakDetectionList = getLeakDetectionList();
		logger.debug("getLeakDetectionList count : [{}]" , leakDetectionList.size() );
		for (LeakDetection leakDetection : leakDetectionList ) {
			sendLeakDetectionMessage (leakDetection);
			leakDetectTo = leakDetectTo < leakDetection.getHisDate().getTime() ? leakDetection.getHisDate().getTime() : leakDetectTo ;
		}
		
		long to = powerMeterTo < leakDetectTo ? leakDetectTo : powerMeterTo;		
		
		try {
			this.lastIngestTime = to ;
			savePropertiesSet();
		} catch (Exception e) {
			logger.error("savePropertiesSet fail.",e);
		}	
		
	}
	
	private List<PowerMeter> getPowerMeterList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return this.computerRoomEnvDAO.powerMeterDataList(new Timestamp(from), new Timestamp(to));

	}
	
	private List<LeakDetection> getLeakDetectionList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return this.computerRoomEnvDAO.leakDetectionDataList(new Timestamp(from), new Timestamp(to));

	}
	
	public void sendPowerMeterMessage (PowerMeter powerMeter) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("f", powerMeter.getF() );	   
	    message.addProperty("ua", powerMeter.getUa() );	 
	    message.addProperty("ub", powerMeter.getUb() );	 
	    message.addProperty("uc", powerMeter.getUc() );	 
	    message.addProperty("ia", powerMeter.getIa() );	 
	    message.addProperty("ib", powerMeter.getIb() );	 
	    message.addProperty("ic", powerMeter.getIc() );	 
	    message.addProperty("in", powerMeter.getIn() );	 
	    message.addProperty("cosa", powerMeter.getCosa() );	 
	    message.addProperty("cosb", powerMeter.getCosb() );	 
	    message.addProperty("cosc", powerMeter.getCosc() );	 
	    message.addProperty("cost", powerMeter.getCost() );	 
	    message.addProperty("pa", powerMeter.getPa() );	 
	    message.addProperty("pb", powerMeter.getPb() );	
	    message.addProperty("pc", powerMeter.getPc() );	
	    message.addProperty("pt", powerMeter.getPt() );	
	    message.addProperty("qa", powerMeter.getQa() );	 
	    message.addProperty("qb", powerMeter.getQb() );	
	    message.addProperty("qc", powerMeter.getQc() );	
	    message.addProperty("qt", powerMeter.getQt() );	
	    message.addProperty("sa", powerMeter.getSa() );	 
	    message.addProperty("sb", powerMeter.getSb() );	
	    message.addProperty("sc", powerMeter.getSc() );	
	    message.addProperty("st", powerMeter.getSt() );	
	    message.addProperty("pept", powerMeter.getPept() );	
	    message.addProperty("nept", powerMeter.getNept() );	
	    message.addProperty("peqt", powerMeter.getPeqt() );	
	    message.addProperty("neqt", powerMeter.getNeqt() );	
	    
	    sendEdgeAttrMessage( getEdgeThingName(powerMeter.getDeviceCode() ) , message , powerMeter.getHisDate().getTime() );

	}
	
	public void sendLeakDetectionMessage (LeakDetection leakDetection) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("leakPos", leakDetection.getLeakPos() );	   
	    message.addProperty("posOhm", leakDetection.getPosOhm() );	 
	    message.addProperty("detectOhm", leakDetection.getDetectOhm() );	 
	    message.addProperty("detectCurrent", leakDetection.getDetectCurrent()  );	 
	    message.addProperty("rhOhm", leakDetection.getRhOhm() );	 
	    
	    sendEdgeAttrMessage( getEdgeThingName2(leakDetection.getDeviceCode() ) , message , leakDetection.getHisDate().getTime() );

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
		
		File file =  PropertiesUtil.getPropertiesFile( PROPERTIES_DIR, PROPERTIES_THINGLIST_FILE_NAME );
		
		String thingListStr = getPropertiesValue( file , PROPERTIES_THINGLIST_FILE_NAME , PROPERTIES_THINGLIST_KEY );
		List<Object> thingList = null;
		if(!StringUtil.isEmpty(thingListStr)) {
			thingList = JsonUtil.fromJsonToList(thingListStr.getBytes());
		}
		else {
			thingList = new ArrayList<Object>();
		}
		
		List<TaijiDevice> taijiDeviceList = computerRoomEnvDAO.listTaijiDevice();
		if( thingList.size() < taijiDeviceList.size() ) {
			logger.debug("thingList size is small than consumptionTermList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(TaijiDevice taijiDevice :  taijiDeviceList ) {
				logger.debug(taijiDevice.toString());
				if( !thingList.contains( taijiDevice.getDeviceCode() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(taijiDevice.getDeviceCode()));
					if(taijiDevice.getDeviceCode().startsWith("leakdect")) {
						reqEdgeThing.setModelName(EDGE_MODEL_NAME2);
						reqEdgeThing.setThingName(getEdgeThingName2(taijiDevice.getDeviceCode()));
					}
					else {
						reqEdgeThing.setModelName(EDGE_MODEL_NAME);
						reqEdgeThing.setThingName(getEdgeThingName(taijiDevice.getDeviceCode()));
					}

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
	
	private String getEdgeThingName2(String deviceCode) {
		return EDGE_MODEL_NAME2 + "." + String.valueOf(deviceCode);
	}

}
