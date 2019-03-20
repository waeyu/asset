package com.sds.ocp.svc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.JsonObject;
import com.sds.afi.dao.SecuredDataSourceEncrypt;
import com.sds.ocp.OcpException;
import com.sds.ocp.rest.HttpConnection;
import com.sds.ocp.sdk.IotAuthManager;
import com.sds.ocp.sdk.IotConnectManager;
import com.sds.ocp.sdk.common.code.IotProtocolCode;
import com.sds.ocp.sdk.security.vo.ItaAuthVO;
import com.sds.ocp.svc.vo.BulkResult;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.svc.vo.ReqEdgeThingBulk;
import com.sds.ocp.util.DateUtil;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

public abstract class AbstractAgentService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static final String SEARCH_PARAM_KEY   = "lastIngestTimeStr";
	protected static final int    MAX_COUNT_BULK_EDGE_REGISTER    = 100;
	
	public static final String PROPERTIES_DIR = "conf" ;

	private String baseUrl;
	private String userId;
	private String userpass;
	
	private String customerId;
	private String apiUserId;
	private String apiUserpass;
	
	protected File searchParamPropertiesFile;
	
	private IotAuthManager authManager;
	private IotConnectManager connectManager;
	
	private String siteId;
	protected String thingName;
	
	protected int period;
	
	private HttpConnection httpConnection;
	
	protected long lastIngestTime;
	
	protected String searchParamFileName;
	
	private long waitMiliSecond;

	@Value("#{comProperties['openapi.baseurl'] != null ? comProperties['openapi.baseurl'] : 'http://local.insator.io:8088' }")
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Value("#{comProperties['portal.userId'] != null ? comProperties['portal.userId'] : 'taiji-agent@taiji.com.cn' }")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Value("#{comProperties['portal.userpass'] != null ? comProperties['portal.userpass'] : 'wLmjtpLsIPhoQvIPuWVG' }") // biotaiji1!
	public void setUserpass(String userpass) {
		this.userpass = SecuredDataSourceEncrypt.decrypt(userpass);
	}
	
	
	@Value("#{comProperties['openapi.customerId'] != null ? comProperties['openapi.customerId'] : 'C000000003' }")	
	public void setCustomerId(String customerId) { 
		this.customerId = customerId;
	}

	@Value("#{comProperties['openapi.userId'] != null ? comProperties['openapi.userId'] : 'taiji-agent' }")
	public void setApiUserId(String apiUserId) {
		this.apiUserId = apiUserId;
	}

	@Value("#{comProperties['openapi.userpass'] != null ? comProperties['openapi.userpass'] : 'wLmjtpLsIPhoQvIP' }") // biotaiji
	public void setApiUserpass(String apiUserpass) {
		this.apiUserpass = SecuredDataSourceEncrypt.decrypt(apiUserpass);
	}
	
	@Value("#{comProperties['platform.siteId'] != null ? comProperties['platform.siteId'] : 'S00002' }") 
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	@Value("#{comProperties['schedule.period'] != null ? comProperties['schedule.period'] : 5000 }")
	public void setPeriod(int period) {
		this.period = period;
	}
	
	@Value("#{comProperties['schedule.waitMiliSecond'] != null ? comProperties['schedule.waitMiliSecond'] : 3000 }")	
	public void setWaitMiliSecond(long waitMiliSecond) {
		this.waitMiliSecond = waitMiliSecond;
	}

	@Autowired	
	public void setHttpConnection(HttpConnection httpConnection) {
		this.httpConnection = httpConnection;
	}

	abstract public void action();	

	
	protected String getPropertiesValue(File file , String propertiesFileName, String propertiesKey) {			

		Properties properties = PropertiesUtil.fetchProperties(file);
		String propertiesValue = properties.getProperty(propertiesKey);
		
		logger.debug("getPropertiesValue key:[{}] , value:[{}].", propertiesKey, propertiesValue );
		
		return propertiesValue;
	}
	
	protected void setPropertiesValue( File file , String propertiesKey, String propertiesValue) {	
		
		Properties props = new Properties();
		props.setProperty( propertiesKey, propertiesValue );
		PropertiesUtil.saveProperties(file, props);
		
		logger.info("setPropertiesValue key:[{}] , value:[{}].", propertiesKey, propertiesValue );
	}
	
	protected void connectPlatform() {

		logger.info("connectPlatform begin.");
		
		this.authManager = new IotAuthManager();
		String userToken = authManager.userItaLogin(this.baseUrl, this.userId, this.userpass);
		if(StringUtil.isEmpty(userToken)) {
			throw new OcpException("fail to login.");
		}
		
		logger.debug("Success to get user token.[{}]" , userToken);
		
		ItaAuthVO authVo;
		try {
			authVo = authManager.getAuthCode(this.siteId, this.thingName, userToken, "webs");
		} catch (Exception e) {
			throw new OcpException("fail to get auth code.");
		}
		
		logger.debug("Success to get AuthCode siteId:[{}] thingName:[{}]." , siteId , thingName);

	    String connServerURI = getConnServerURI(authVo);
	    this.connectManager = new IotConnectManager(IotProtocolCode.WEBS, connServerURI);
	     
	    // 4. connect with authentication-info.
	    try {
			this.connectManager.connectThing(siteId, thingName, authVo);
			IotActionCallback userCallback = new IotActionCallback(this.siteId,this.thingName,this.waitMiliSecond);
			this.connectManager.setUserActionCallback(userCallback);
		} catch (Exception e) {
			throw new OcpException("fail to connect.");
		}
	    
		logger.info("Success to connect. siteId:[{}] thingName:[{}]." , siteId , thingName);
	    
	}

	protected void sendAttrMessage(JsonObject message, long msgDate) {
	    try {
			String ret = this.connectManager.sendMessage( message , "Basic-AttrGroup" , msgDate );
			if(ret==null) {
				throw new OcpException("Fail to send message. return is null.");
			}
			logger.debug("Send message msgDate:[{}] body:{}" , new Timestamp(msgDate) , message.toString() );
		} catch (Exception e) {
			logger.error("Fail to send message." , e );
			throw new OcpException("Fail to send message.");
		}
	}
	
	protected void sendEdgeAttrMessage(String edgeThingName , JsonObject message, long msgDate) {
		sendEdgeUserMessage(edgeThingName, "Basic-AttrGroup" , message, msgDate);
	}
	
	protected void sendEdgeUserMessage(String edgeThingName, String userMessageCode , JsonObject message , long msgDate) {
	    try {
			String ret = this.connectManager.sendEdgeMessage( edgeThingName, message , userMessageCode , msgDate );
			if(ret==null) {
				throw new OcpException("sendEdgeMessage fail. return is null.");
			}
			logger.debug("Send message[{}] msgDate:[{}] body:{}" , userMessageCode , new Timestamp(msgDate) , message.toString() );
		} catch (Exception e) {
			logger.error("Fail to send message. [{}/{}]" , edgeThingName, userMessageCode , e );
			throw new OcpException("Fail to send message.");
		}
	}
	
	protected List<String> requestEdgeThingBulkRegister(ReqEdgeThingBulk reqEdgeThingBulk) {
		
		String url = this.baseUrl + "/v1.0/sites/" + this.siteId + "/topThings/" + this.thingName +"/edgeList";		
		try {
			byte[] jsonBytes = httpConnection.post( url, JsonUtil.toJsonBytes(reqEdgeThingBulk) , makeBasicAuthHeaders() ) ;
			BulkResult bulkResult = JsonUtil.fromJson(jsonBytes, BulkResult.class);
			if(bulkResult!=null) {
				return bulkResult.getFailThings();
			}
			throw new OcpException("Return message is abnormal.");
			
		} catch (MalformedURLException e) {
			logger.error("url information is abnormal.",e);
			throw new OcpException(e);
		} catch (IOException e) {
			logger.error("request io exception." , e);
			throw new OcpException(e);
		}

	}
	
	protected void initSearchParameter() {
		
		this.searchParamPropertiesFile = PropertiesUtil.getPropertiesFile( PROPERTIES_DIR , this.searchParamFileName );
		
		String lastIngestTimeStr = getPropertiesValue( this.searchParamPropertiesFile , this.searchParamFileName , SEARCH_PARAM_KEY );

		if (StringUtil.isEmpty(lastIngestTimeStr)) {
			this.lastIngestTime = DateUtil.minusSeconds(System.currentTimeMillis(), this.period / 1000);
		} else {
			this.lastIngestTime = DateUtil.getTime(lastIngestTimeStr);
		}
		logger.debug("lastIngestTime: {}", new Timestamp(this.lastIngestTime));

	}
	
	protected void devicedSendLoop(List<ReqEdgeThing> edgeThingList, List<Object> addThingList, int edgeModelNameLen ) {
		
		while(!edgeThingList.isEmpty()) {
			logger.debug( "edgeThingList count is {}" , edgeThingList.size() );
			if( edgeThingList.size() > MAX_COUNT_BULK_EDGE_REGISTER ) {
				List<ReqEdgeThing> devidedEdgeThingList = new ArrayList<ReqEdgeThing>();
				Iterator<ReqEdgeThing> it = edgeThingList.iterator();
				int count = 0 ;
				while(it.hasNext()) {
					devidedEdgeThingList.add(it.next());
					it.remove();		
					count++;
					if( count >= MAX_COUNT_BULK_EDGE_REGISTER ) {
						break;
					}
				}
				devidedSend(devidedEdgeThingList, addThingList,edgeModelNameLen);
			}
			else {
				devidedSend(edgeThingList, addThingList,edgeModelNameLen);
				break;
			}
			
		}		
	}	
	
	private void devidedSend(List<ReqEdgeThing> edgeThingList, List<Object> addThingList, int edgeModelNameLen ) {
		
		ReqEdgeThingBulk reqEdgeThingBulk = new ReqEdgeThingBulk();
		reqEdgeThingBulk.setThings(edgeThingList);
		
		List<String> failList = requestEdgeThingBulkRegister(reqEdgeThingBulk);
		if( failList!=null && failList.size()>0 ) {					
			logger.info("failList count:{}.", failList.size() );
			for( String thingName : failList ) {
				if(thingName.length() > edgeModelNameLen+1 ) {
					String uniqueNum = thingName.substring(edgeModelNameLen+1);
					addThingList.remove(uniqueNum);
				}
				else {
					logger.warn("return thingName is abnormal.");
					throw new OcpException("return thingName is abnormal.");
				}
			}
		}
		
	}
	
	private Map<String, Object> makeBasicAuthHeaders() {
		
		String headerOrgValue = this.customerId + "+" + this.apiUserId + ":" + this.apiUserpass;
		String headerValue = "Basic " + Base64.encodeBase64String(headerOrgValue.getBytes());
		
		Map<String, Object> requestHeaders = new HashMap<String,Object>();
		requestHeaders.put("Authorization", headerValue);
		
		return requestHeaders;
		
	}
	
	private static String getConnServerURI(ItaAuthVO authVo){
	    return (authVo.isSslYn() ? "wss://" : "ws://").concat(authVo.getIp()).concat(":").concat(authVo.getPort());
	}


}
