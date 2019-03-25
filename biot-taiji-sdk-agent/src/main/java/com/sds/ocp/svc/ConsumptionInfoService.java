package com.sds.ocp.svc;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.sds.ocp.OcpException;
import com.sds.ocp.svc.dao.ConsumptionInfoDAO;
import com.sds.ocp.svc.vo.ConsumptionInfo;
import com.sds.ocp.svc.vo.ConsumptionTerminal;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.util.DateUtil;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class ConsumptionInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "ecc-consumption-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "ConsumptionTerminal";
	
	private ConsumptionInfoDAO consumptionInfoDAO;
	
	@Autowired
	public void setConsumptionInfoDAO(ConsumptionInfoDAO consumptionInfoDAO) {
		this.consumptionInfoDAO = consumptionInfoDAO;
	}
	
	@Value("#{comProperties['consumption.rootThingName'] != null ? comProperties['consumption.rootThingName'] : 'Consumption.TerminalRoot' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	@Value("#{comProperties['consumption.searchParamFileName'] != null ? comProperties['consumption.searchParamFileName'] : 'ecc-consumption.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Override
	public void action() {
		
		logger.debug("action begin.");
		long to = this.lastIngestTime ;
		List<ConsumptionInfo> consumptionInfoList = getList();
		logger.info("consumptionInfoList count : [{}]" , consumptionInfoList.size() );
		for (ConsumptionInfo consumptionInfo : consumptionInfoList ) {
			sendMessage (consumptionInfo);
			to = to < consumptionInfo.getConDatetime().getTime() ? consumptionInfo.getConDatetime().getTime() : to ;
		}
		try {
			this.lastIngestTime = to ;
			if( DateUtil.isDataIssue(this.lastIngestTime, TimeZone.getDefault()) ) {
				logger.warn("Data collection time is more than 2 hours. last collection time[{}] " , new Timestamp(this.lastIngestTime));
			}
			savePropertiesSet();
		} catch (Exception e) {
			logger.error("savePropertiesSet fail.",e);
		}	
		
	}
	
	private List<ConsumptionInfo> getList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return consumptionInfoDAO.list(new Timestamp(from), new Timestamp(to));

	}
	
	public void sendMessage (ConsumptionInfo consumptionInfo) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("conLogInnerId", consumptionInfo.getConLogInnerId() );
	    message.addProperty("conDatetime", DateUtil.getTimeString(consumptionInfo.getConDatetime().getTime()));
	    message.addProperty("money", consumptionInfo.getMoney() );
	    message.addProperty("discountMoney", consumptionInfo.getDiscountMoney() );
	    message.addProperty("markId", consumptionInfo.getMarkId() );		   
	    
	    sendEdgeAttrMessage( getEdgeThingName(consumptionInfo.getConTerminalInnerId()) , message,consumptionInfo.getConDatetime().getTime());

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
		
		List<ConsumptionTerminal> consumptionTermList = consumptionInfoDAO.listConsumptionTerminal();
		if( thingList.size() < consumptionTermList.size() ) {
			logger.debug("thingList size is small than consumptionTermList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(ConsumptionTerminal consumptionTerminal : consumptionTermList) {
				logger.debug(consumptionTerminal.toString());
				if( !thingList.contains( consumptionTerminal.getConTerminalInnerId() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(consumptionTerminal.getConTerminalInnerId()));
					reqEdgeThing.setThingName(getEdgeThingName(consumptionTerminal.getConTerminalInnerId()));
					reqEdgeThing.setThingNickName(consumptionTerminal.getConTerminalName());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(consumptionTerminal.getConTerminalInnerId());
				}
			}
			
			devicedSendLoop(edgeThingList, addThingList , EDGE_MODEL_NAME.length() ) ;
			
			thingList.addAll(addThingList);
			setPropertiesValue( file , PROPERTIES_THINGLIST_KEY , JsonUtil.toJson(thingList) );
			
		}
		
	}	
	
	private String getEdgeThingName(int conTerminalInnerId) {
		return EDGE_MODEL_NAME + "." + String.valueOf(conTerminalInnerId);
	}

}
