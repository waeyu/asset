
package com.sds.ocp.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Json 변환 관련 유틸리티 제공 메소드
 * 
 * @author 손성훈
 */
public class JsonUtil {
	private static final Logger								LOGGER				= LoggerFactory.getLogger(JsonUtil.class);

	private static final ObjectMapper						OBJECT_MAPPER		= new ObjectMapper();
	private static final TypeReference<Map<String, Object>>	TYPE_REF_MAP		= new TypeReference<Map<String, Object>>() {};
	private static final TypeReference<Map<String, String>>	TYPE_REF_MAP_STR	= new TypeReference<Map<String, String>>() {};
	private static final TypeReference<List<Object>>		TYPE_REF_LIST		= new TypeReference<List<Object>>() {};
	private static final TypeReference<String>				TYPE_REF_STR		= new TypeReference<String>() {};
	private static final TypeReference<Long>				TYPE_REF_LONG		= new TypeReference<Long>() {};
	private static final TypeReference<Double>				TYPE_REF_DOUBLE		= new TypeReference<Double>() {};
	private static final TypeFactory						TYPE_FACTORY		= TypeFactory.defaultInstance();

	static {
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
		OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	/**
	 * 입력한 객체를 json 문자열로 변환한다.
	 * 
	 * @param obj
	 *        변환 대상 객체 (VO, Map 가능)
	 * @return json 문자열
	 */
	public static String toJson(Object obj) {
		return toJson(obj, false);
	}

	/**
	 * 입력한 객체를 json 문자열로 변환한다.
	 * 
	 * @param obj
	 *        변환 대상 객체 (VO, Map 가능)
	 * @param prettyPrint
	 *        들여쓰기 형태로 출력할지 여부
	 * @return json 문자열
	 */
	public static String toJson(Object obj, boolean prettyPrint) {
		LOGGER.debug("toJson");
		try {
			if (prettyPrint) {
				return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			} else {
				return OBJECT_MAPPER.writeValueAsString(obj);
			}
		} catch (IOException e) {
			LOGGER.error("Exception occurred serializing JSON (Object to JSON content string)", e);
			return null;
		}
	}

	/**
	 * 입력한 객체를 json 바이트 배열로 변환한다.
	 * 
	 * @param obj
	 *        변환 대상 객체 (VO, Map 가능)
	 * @return json 데이터 바이트 배열
	 */
	public static byte[] toJsonBytes(Object obj) {
		return toJsonBytes(obj, false);
	}

	/**
	 * 입력한 객체를 json 바이트 배열로 변환한다.
	 * 
	 * @param obj
	 *        변환 대상 객체 (VO, Map 가능)
	 * @param prettyPrint
	 *        들여쓰기 형태로 출력할지 여부
	 * @return json 데이터 바이트 배열
	 */
	public static byte[] toJsonBytes(Object obj, boolean prettyPrint) {
		LOGGER.debug("toJsonBytes");
		try {
			if (prettyPrint) {
				return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj);
			} else {
				return OBJECT_MAPPER.writeValueAsBytes(obj);
			}
		} catch (IOException e) {
			LOGGER.error("Exception occurred serializing JSON (Object to JSON content bytes)", e);
			return null;
		}
	}

	/**
	 * json 문자열을 대상 클래스 객체로 변환한다.
	 * 
	 * @param jsonStr
	 *        json문자열
	 * @param clazz
	 *        변환 대상 클래스 (VO 클래스 가능)
	 * @return 변환된 clazz 타입 객체
	 */
	public static <T> T fromJson(String jsonStr, Class<T> clazz) {
		LOGGER.debug("fromJson");
		try {
			return OBJECT_MAPPER.readValue(jsonStr, clazz);
		} catch (IOException e) {
			LOGGER.error("Exception occurred deserializing JSON (JSON content string to Object) : {}", e.toString());
			LOGGER.debug(jsonStr);
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * json 문자열을 명시된 타입으로 변환한다.
	 * 
	 * @param jsonStr
	 *        json문자열
	 * @param type
	 * @return 변환된 Map 객체
	 */
	public static <T> T fromJson(String jsonStr, Type type) {
		LOGGER.debug("fromJson");
		try {
			JavaType javaType = TYPE_FACTORY.constructType(type);
			return OBJECT_MAPPER.readValue(jsonStr, javaType);
		} catch (IOException e) {
			LOGGER.error("Exception occurred deserializing JSON (JSON content string to Type) : {}", e.toString());
			LOGGER.debug(jsonStr);
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * json 문자열을 명시된 타입으로 변환한다.
	 * 
	 * @param jsonBytes
	 *        json byte[] 데이터
	 * @param type
	 * @return 변환된 Map 객체
	 */
	public static <T> T fromJson(byte[] jsonBytes, Type type) {
		LOGGER.debug("fromJson");
		try {
			JavaType javaType = TYPE_FACTORY.constructType(type);
			return OBJECT_MAPPER.readValue(jsonBytes, javaType);
		} catch (IOException e) {
			LOGGER.error("Exception occurred deserializing JSON (JSON content bytes to Type) : {}", e.toString());
			LOGGER.debug(new String(jsonBytes, Charset.defaultCharset()));
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * byte[] json 데이터를 Object 로 변환한다. <br/>
	 * 리턴 값은, Map, List, String, Integer, Long, String, Double.. 객체가 될 수 있다.
	 * 
	 * @param jsonBytes
	 *        json byte[] 데이터
	 * @return 변환된 객체 
	 */
	public static Object fromJsonToAnyObject(byte[] jsonBytes) {
		LOGGER.debug("fromJsonToAnyObject");
		// BIOT-6779
		String str = new String(jsonBytes, Charset.defaultCharset()).trim();
		
		if (str.startsWith("{")) {
			return fromJson(jsonBytes, TYPE_REF_MAP);	
		} else if (str.startsWith("[")) {
			return fromJson(jsonBytes, TYPE_REF_LIST);
		} else if (str.startsWith("\"")) {
			return fromJson(jsonBytes, TYPE_REF_STR);
		} else if ("true".equals(str)) {
			return Boolean.TRUE;
		} else if ("false".equals(str)) {
			return Boolean.FALSE;
		} else if (str.contains(".")) {
			return fromJson(jsonBytes, TYPE_REF_DOUBLE);
		} else {
			return fromJson(jsonBytes, TYPE_REF_LONG);
		}
	}

	/**
	 * json 문자열을 지정한 타입으로 변환한다.
	 * 
	 * @param json
	 *        json String or byte[]
	 * @param typeRef
	 * @return 변환된 Map 객체
	 */
	private static <T> T fromJson(Object json, TypeReference<T> typeRef) {
		try {
			if (json instanceof String) {
				return OBJECT_MAPPER.readValue((String) json, typeRef);
			} else if (json instanceof byte[]) {
				return OBJECT_MAPPER.readValue((byte[]) json, typeRef);
			} else {
				throw new IllegalArgumentException("Unsupported Type or null");
			}
		} catch (IOException e) {
			LOGGER.error("Exception occurred deserializing JSON : {}", e.toString());
			LOGGER.debug(json.toString());
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * json 문자열을 java.util.Map으로 변환한다.
	 * 
	 * @param jsonStr
	 *        json문자열
	 * @return 변환된 Map 객체
	 */
	public static Map<String, Object> fromJsonToMap(String jsonStr) {
		LOGGER.trace("fromJsonToMap - {}", jsonStr);
		return fromJson(jsonStr, TYPE_REF_MAP);
	}
	
	/**
	 * json 문자열을 java.util.Map으로 변환한다.
	 * 
	 * @param jsonStr
	 *        json문자열
	 * @return 변환된 Map 객체
	 */
	public static Map<String, String> fromJsonToStrMap(String jsonStr) {
		LOGGER.debug("fromJsonToStrMap");
		return fromJson(jsonStr, TYPE_REF_MAP_STR);
	}

	/**
	 * byte[] json 데이터를 java.util.Map으로 변환한다.
	 * 
	 * @param jsonBytes
	 *        json byte[] 데이터
	 * @return 변환된 Map 객체
	 */
	public static Map<String, Object> fromJsonToMap(byte[] jsonBytes) {
		LOGGER.debug("fromJsonToMap");
		return fromJson(jsonBytes, TYPE_REF_MAP);
	}
	
	/**
	 * byte[] json 데이터를 java.util.Map으로 변환한다.
	 * 
	 * @param jsonBytes
	 *        json byte[] 데이터
	 * @return 변환된 Map 객체
	 */
	public static Map<String, String> fromJsonToStrMap(byte[] jsonBytes) {
		LOGGER.debug("fromJsonToStrMap");
		return fromJson(jsonBytes, TYPE_REF_MAP_STR);
	}


	/**
	 * byte[] json 데이터를 java.util.List으로 변환한다.
	 * 
	 * @param jsonBytes
	 *        json byte[] 데이터
	 * @return 변환된 List 객체
	 */
	public static List<Object> fromJsonToList(byte[] jsonBytes) {
		LOGGER.debug("fromJsonToList");
		try {
			return OBJECT_MAPPER.readValue(jsonBytes, TYPE_REF_LIST);
		} catch (IOException e) {
			LOGGER.error("Exception occurred deserializing JSON (JSON content bytes to List) : {}", e.toString());
			LOGGER.debug(new String(jsonBytes, Charset.defaultCharset()));
			throw new IllegalArgumentException(e);
		}
	}

	public static Map<String, Object> fromJsonToMap(Object obj) {
		if (obj instanceof byte[]) {
			byte[] bytes = (byte[]) obj;
			return fromJsonToMap(bytes);
		} else if (obj instanceof String) {
			String json = (String) obj;
			return fromJsonToMap(json);
		}
		throw new IllegalArgumentException("Unsupported Type or null");
	}

}
