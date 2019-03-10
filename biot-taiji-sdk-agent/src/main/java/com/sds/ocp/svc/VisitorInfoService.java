package com.sds.ocp.svc;

import java.sql.Timestamp;
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
import com.sds.ocp.svc.dao.VisitorInfoDAO;
import com.sds.ocp.svc.vo.VisitorInfo;
import com.sds.ocp.util.DateUtil;

@Service
public class VisitorInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private VisitorInfoDAO visitorInfoDAO;

	@Value("#{comProperties['visitor.thingName'] != null ? comProperties['visitor.thingName'] : 'VisitSensor.vSensor1001' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	@Value("#{comProperties['visitor.searchParamFileName'] != null ? comProperties['visitor.searchParamFileName'] : 'ecc-visitor.properties' }") 
	public void setSearchParamFileName(String searchParamFileName) {
		this.searchParamFileName = searchParamFileName;
	}

	@Autowired
	public void setVisitorInfoDAO(VisitorInfoDAO visitorInfoDAO) {
		this.visitorInfoDAO = visitorInfoDAO;
	}
	
	public void action() {

		long to = 0 ;
		List<VisitorInfo> visitorInfoList = getList();
		logger.debug("getlist count : [{}]" , visitorInfoList.size() );
		for (VisitorInfo visitorInfo : visitorInfoList) {
			sendMessage (visitorInfo);
			to = to < visitorInfo.getOperDt().getTime() ? visitorInfo.getOperDt().getTime() : to ;
		}
		if(to==0) {
			to = System.currentTimeMillis() ;
		}
		try {
			this.lastIngestTime = to ;
			savePropertiesSet();
		} catch (Exception e) {
			logger.error("savePropertiesSet fail.",e);
		}		
		
	}

	public List<VisitorInfo> getList() {

		long from = this.lastIngestTime;
		long to = DateUtil.getToTime(from, this.period / 1000);
		
		logger.debug("[from:" + new Timestamp(from) + ", to:" + new Timestamp(to) + "]");
		return visitorInfoDAO.list(new Timestamp(from), new Timestamp(to));

	}
	
	public void sendMessage (VisitorInfo visitorInfo) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("orderCode", visitorInfo.getOrderCode());
	    message.addProperty("operDt", DateUtil.getTimeString(visitorInfo.getOperDt().getTime()));
	    
	    sendAttrMessage(message,visitorInfo.getOperDt().getTime());

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
