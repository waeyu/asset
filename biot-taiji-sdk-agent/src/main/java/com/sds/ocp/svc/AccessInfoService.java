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
import com.sds.ocp.svc.dao.AccessInfoDAO;
import com.sds.ocp.svc.vo.AccessAlarm;
import com.sds.ocp.svc.vo.AccessCard;
import com.sds.ocp.svc.vo.AccessDoor;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.util.DateUtil;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class AccessInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "ecc-access-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "DoorSensor";

	
	private AccessInfoDAO accessInfoDAO;
	
	@Autowired
	public void setAccessInfoDAO(AccessInfoDAO accessInfoDAO) {
		this.accessInfoDAO = accessInfoDAO;
	}
	
	@Value("#{comProperties['access.rootThingName'] != null ? comProperties['access.rootThingName'] : 'DoorSystem.DoorRoot100' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}	

	@Value("#{comProperties['access.searchParamFileName'] != null ? comProperties['access.searchParamFileName'] : 'ecc-access.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Override
	public void action() {
		
		logger.debug("action begin.");
		
		List<AccessDoor> accessDoorList = getAccessDoorList();
		logger.debug("getAccessDoorList count : [{}]" , accessDoorList.size() );
		for (AccessDoor accessDoor : accessDoorList ) {
			sendAccessDoorMessage (accessDoor);
		}
		
		long alarmTo = this.lastIngestTime ;
		List<AccessAlarm> accessAlarmList = getAccessAlarmList();
		logger.debug("getAccessAlarmList count : [{}]" , accessAlarmList.size() );
		for (AccessAlarm accessAlarm : accessAlarmList ) {
			sendAccessAlarmMessage (accessAlarm);
			alarmTo = alarmTo < accessAlarm.getAlarmEventTime().getTime() ? accessAlarm.getAlarmEventTime().getTime() : alarmTo ;
		}
		
		long cardTo = this.lastIngestTime ;
		List<AccessCard> accessCardList = getAccessCardList();
		logger.debug("getAccessCardList count : [{}]" , accessCardList.size() );
		for (AccessCard accessCard : accessCardList ) {
			sendAccessCardMessage (accessCard);
			cardTo = cardTo < accessCard.getCardEventTime().getTime() ? accessCard.getCardEventTime().getTime() : cardTo ;
		}
		
		long to = alarmTo < cardTo ? cardTo : alarmTo;		
		
		try {
			this.lastIngestTime = to ;
			savePropertiesSet();
		} catch (Exception e) {
			logger.error("savePropertiesSet fail.",e);
		}	
		
	}
	
	
	private List<AccessDoor> getAccessDoorList() {

		long from = this.lastIngestTime;
		return this.accessInfoDAO.listAccessDoorAfter(new Timestamp(from));

	}
	

	
	private List<AccessAlarm> getAccessAlarmList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return this.accessInfoDAO.listAccessAlarm(new Timestamp(from), new Timestamp(to));

	}
	

	
	private List<AccessCard> getAccessCardList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return this.accessInfoDAO.listAccessCard(new Timestamp(from), new Timestamp(to));

	}
	
	private void sendAccessDoorMessage (AccessDoor accessDoor) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("lockStatus", accessDoor.getLockStatus() );   
	    
	    sendEdgeUserMessage( getEdgeThingName(accessDoor.getDoorId() ) , "accessDoor" , message , accessDoor.getAlarmTime().getTime());

	}
	
	private void sendAccessAlarmMessage (AccessAlarm accessAlaram) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("alarmIndex", accessAlaram.getAlarmIndex() );
	    message.addProperty("alarmEventTime", DateUtil.getTimeString(accessAlaram.getAlarmEventTime().getTime()));
	    message.addProperty("eventNote", accessAlaram.getEventNote() );		   
	    
	    sendEdgeUserMessage( getEdgeThingName(accessAlaram.getDoorId() ) , "accessAlarm" , message , accessAlaram.getAlarmEventTime().getTime());

	}
	
	private void sendAccessCardMessage (AccessCard accessCard) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("cardIndex", accessCard.getCardIndex() );
	    message.addProperty("cardEventTime", DateUtil.getTimeString(accessCard.getCardEventTime().getTime()));
	    message.addProperty("cardId", accessCard.getCardId() );
	    message.addProperty("inOut", accessCard.getInOut() );   
	    
	    sendEdgeUserMessage( getEdgeThingName(accessCard.getDoorId() ) , "accessCard" , message , accessCard.getCardEventTime().getTime());

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
		
		List<AccessDoor> accessDoorList = accessInfoDAO.listAccessDoor();
		if( thingList.size() < accessDoorList.size() ) {
			logger.debug("thingList size is small than accessDoorList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(AccessDoor accessDoor : accessDoorList) {
				logger.debug(accessDoor.toString());
				if( !thingList.contains( accessDoor.getDoorId() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(accessDoor.getDoorId()));
					reqEdgeThing.setThingName(getEdgeThingName(accessDoor.getDoorId()));
					reqEdgeThing.setThingNickName(accessDoor.getDoorName());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(accessDoor.getDoorId());
				}
			}
			
			devicedSendLoop(edgeThingList, addThingList, EDGE_MODEL_NAME.length()) ;
			
			thingList.addAll(addThingList);
			setPropertiesValue( file , PROPERTIES_THINGLIST_KEY , JsonUtil.toJson(thingList) );
			
		}
		
	}

	private String getEdgeThingName(int conTerminalInnerId) {
		return EDGE_MODEL_NAME + "." + String.valueOf(conTerminalInnerId);
	}

}
