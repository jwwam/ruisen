package com.ruisen.rsmanage.daemon.quartz.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class JsonUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	public static String toJsonString(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("JSON序列化失败", e);
			return null;
		}
	}

	/**
	 * JSON字符串转对象
	 */
	public static <T> T parseObject(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			log.error("JSON反序列化失败", e);
			return null;
		}
	}
	    /**
    * 将JSON字符串解析为Map
    */
	public static Map<String, Object> parseMap(String json) {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (JsonProcessingException e) {
			log.error("JSON解析失败: {}", e.getMessage());
			return null;
		}
	}
}