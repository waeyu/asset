package com.sds.ocp.util;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity 처리 유틸클래스
 * 
 * @author 김태호 (th71.kim@samsung.com)
 * @author 박준수 (trust.park@samsung.com)
 * @since 2010. 3. 26
 * 
 */
public final class VelocityUtil {


	private static final Logger LOG = LoggerFactory.getLogger(VelocityUtil.class);

	private static final String PACKAGE_NAME = "org.apache.velocity.runtime.resource.loader";
	private static final String LOADER_CLASS_NAME = "StringResourceLoader";

	//private static final String PACKAGE_NAME = VelocityUtil.class.getPackage().getName();
	//private static final String LOADER_CLASS_NAME = "VelocityStringResourceLoader";
	//private static final VelocityStringResourceLoader RESOURCE_LOADER;
	//private static final ResourceLoader RESOURCE_LOADER;
	
	private static final String LOADER_CLASS_FULLNAME = PACKAGE_NAME + "." + LOADER_CLASS_NAME;
	
	public VelocityUtil() {
		throw new AssertionError();
	}

	static {
		java.util.Properties props = new java.util.Properties();
		props.put("resource.loader", "srs");
		props.put("srs.resource.loader.public.name", LOADER_CLASS_NAME);
		props.put("srs.resource.loader.class", LOADER_CLASS_FULLNAME);
		props.put("input.encoding", "UTF-8");

		Velocity.init(props);

//		RESOURCE_LOADER = (StringResourceLoader) Velocity.getTemplate(
//				StringResourceLoader.EMPTY_TEMPLATE_NAME).getResourceLoader();
	}

	/**
	 * @param templateSourceStr
	 *            Template Source
	 * @param param
	 *            Template Value
	 * @param logTag Template Name
	 * @return
	 */
	public static String make(String templateSourceStr, Map<String, Object> param, String logTag) { 
		LOG.debug("velocity");

		if (StringUtil.isEmpty(templateSourceStr)) {
			throw new IllegalArgumentException("Invalid template");
		}
		if (param == null || param.isEmpty()) {
			throw new IllegalArgumentException("Invalid value map");
		}

		StringWriter writer = new StringWriter();

//		RESOURCE_LOADER.setTemplate(Thread.currentThread().getName(), templateSourceStr);
//		Template source = Velocity.getTemplate(Thread.currentThread().getName());
//		if (source == null) {
//			throw new IllegalStateException("template is null!");
//		}
		
		VelocityContext context = new VelocityContext(param);
		
		Velocity.evaluate(context, writer, logTag, templateSourceStr);
//		source.merge(context, writer);

		return writer.toString();
	}
	
	/**
	 * @param templateSourceStr
	 *            Template Source
	 * @param param
	 *            Template Value
	 * @return
	 */
	public static String make(String templateSourceStr, Map<String, Object> param) {
		String logtag = templateSourceStr.substring(0, 3);
		return make(templateSourceStr, param, logtag);
	}
}
