package com.sds.ocp.third.svc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.ocp.common.exception.OcpException;
import com.sds.ocp.common.msg.StatusEnum;
import com.sds.ocp.common.util.StringUtil;
import com.sds.ocp.third.svc.dao.ThingMDataDAO;
import com.sds.ocp.third.svc.dao.ThingMessageDataDAO;
import com.sds.ocp.third.svc.vo.ThingMsgDataIn;
import com.sds.ocp.third.svc.vo.ThingMsgDataOut;

@Service
public class ThingMessageDataService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ThingMessageDataDAO thingMessageDataDAO;
	
	@Autowired
	private ThingMDataDAO thingMDataDAO;
	
	
	public List<ThingMsgDataOut> getThingMsgData(ThingMsgDataIn thingMsgDataIn){
		
		if (logger.isDebugEnabled()) {
			logger.debug("getThingMsgData begin.");
		}
		
		String siteId = thingMDataDAO.getSiteId(thingMsgDataIn.getSiteCode());
		if(StringUtil.isEmpty(siteId)) {
			throw new OcpException(StatusEnum.BAD_REQUEST, "site is not exist." );
		}
		
		List<String> thingNameList = thingMDataDAO.listThingName(thingMsgDataIn.getThingModelName(), siteId);		
		if (thingNameList.isEmpty()) {
			throw new OcpException(StatusEnum.BAD_REQUEST, "Thing list is not exist." );
		}	
		if (logger.isDebugEnabled()) {
			logger.debug("thingNameList count is {} ", thingNameList.size() );
		}
		
		try {
			List<ThingMsgDataOut> thingMsgDataOut = thingMessageDataDAO.listThingMsgData(thingMsgDataIn, siteId , thingNameList);
			if(thingMsgDataOut.isEmpty()) {
				throw new OcpException(StatusEnum.NOT_FOUND , "data is not found." );
			}
			if (logger.isDebugEnabled()) {
				logger.debug("thingMsgDataOut count is {} ", thingMsgDataOut.size() );
			}
			return thingMsgDataOut;
		}
		catch (OcpException e1) {
			throw e1;
		}
		catch (Exception e) {
			logger.info("listThingMsgData fail." ,e );
			throw new OcpException(StatusEnum.SERVICE_UNAVAILABLE , "listThingMsgData fail." , e );
		}

	}

}
