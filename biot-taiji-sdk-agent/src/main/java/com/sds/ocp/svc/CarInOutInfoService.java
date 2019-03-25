package com.sds.ocp.svc;

import java.sql.Timestamp;
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
import com.sds.ocp.svc.dao.CarInOutInfoDAO;
import com.sds.ocp.svc.vo.CarInOutInfo;
import com.sds.ocp.util.DateUtil;

@Service
public class CarInOutInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private CarInOutInfoDAO carInOutInfoDAO;

	@Value("#{comProperties['carinout.thingName'] != null ? comProperties['carinout.thingName'] : 'CarInOutSensor.Entrance1' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	@Value("#{comProperties['carinout.searchParamFileName'] != null ? comProperties['carinout.searchParamFileName'] : 'ecc-carinout.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Autowired
	public void setCarInOutInfoDAO(CarInOutInfoDAO carInOutInfoDAO) {
		this.carInOutInfoDAO = carInOutInfoDAO;
	}	
	
	public void action() {

		long to = this.lastIngestTime ;
		List<CarInOutInfo> carInOutInfoList = getList();
		logger.info("carInOutInfoList count : [{}]" , carInOutInfoList.size() );
		for (CarInOutInfo carInOutInfo : carInOutInfoList) {
			sendMessage (carInOutInfo);
			to = to < carInOutInfo.getCrdtm().getTime() ? carInOutInfo.getCrdtm().getTime() : to ;
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

	public List<CarInOutInfo> getList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return carInOutInfoDAO.carInOutList(new Timestamp(from), new Timestamp(to));

	}
	
	public void sendMessage (CarInOutInfo carInOutInfo) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("carCode", carInOutInfo.getCarCode());
	    message.addProperty("crdtm", DateUtil.getTimeString(carInOutInfo.getCrdtm().getTime()));
	    message.addProperty("inOut", carInOutInfo.getInOut());
	    
	    sendAttrMessage(message,carInOutInfo.getCrdtm().getTime());

	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		
		initSearchParameter();
		connectPlatform();
	}
	
	@PreDestroy
	public void savePropertiesSet() throws Exception {
		setPropertiesValue( this.searchParamPropertiesFile , SEARCH_PARAM_KEY , DateUtil.getTimeString(this.lastIngestTime) );
	}

}
