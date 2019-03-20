package com.sds.ocp.third.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.ocp.third.svc.dao.VisitorInfoDAO;
import com.sds.ocp.third.svc.vo.VisitorInfo;

@Service
public class VisitorInfoService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private VisitorInfoDAO visitorInfoDAO;

	@Autowired
	public void setVisitorInfoDAO(VisitorInfoDAO visitorInfoDAO) {
		this.visitorInfoDAO = visitorInfoDAO;
	}
	
	public VisitorInfo getVisitorInfo(String orderCode) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("### VisitorInfoService - getVisitorInfo() : orderCode: {}", orderCode );
		}
		
		return visitorInfoDAO.selectVisitorInfo(orderCode);
	}

}
