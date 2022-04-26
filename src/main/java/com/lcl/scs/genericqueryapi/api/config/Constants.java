package com.lcl.scs.genericqueryapi.api.config;

import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
	
	static {
		try {
			initSystemProperties();
		} catch (Exception e) {
			LoggingUtilities.generateErrorLog("Error while initialising datasource", e);
		}
	}

	// Locale and Timezone Constants
	public static final String DEFAULT_TIMEZONE = "Canada/Eastern";
	public static final String NA = "N/A";

	static {
		TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
	}
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String DEFAULT_COUNTRY = "CA";
	public static final Locale DEFAULT_LOCALE = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
	public static final String DEFAULT_INPUT_DATE_PATTERN = "yyyy-MM-dd";
	public static final DateTimeFormatter DEFAULT_INPUT_DATE_FORMAT = DateTimeFormatter
			.ofPattern(DEFAULT_INPUT_DATE_PATTERN, DEFAULT_LOCALE);
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN,
			DEFAULT_LOCALE);
	public static final String DATE_AND_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	public static final DateTimeFormatter DATE_AND_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_AND_TIME_PATTERN);

	// System Level Constants
	public static final String LINE_SEPARATOR = System.lineSeparator();

	public enum FilterType {
		CONTAINS("contains"), STARTS_WITH("startsWith"), EQUALS("equals"), CUSTOM("custom");

		private final String filterType;

		FilterType(String filterType) {
			this.filterType = filterType;
		}

		public String getFilterType() {
			return filterType;
		}

	}

	public enum Condition {
		AND("and"), OR("or");

		private final String condition;

		Condition(String condition) {
			this.condition = condition;
		}

		public String getCondition() {
			return condition;
		}
	}

	private static final HashMap<Integer, String> HTTP_STATUS_CODES = new HashMap<>();

	static {
		HTTP_STATUS_CODES.put(400, "Bad Request");
		HTTP_STATUS_CODES.put(401, "Unauthorized");
		HTTP_STATUS_CODES.put(404, "Resource Not Found");
		HTTP_STATUS_CODES.put(500, "Internal Server Error");
	}

	public static String getHttpStatusDesc(Integer statusCode) {
		return HTTP_STATUS_CODES.get(statusCode);
	}

	private static void initSystemProperties() {
		System.setProperty("oracle.net.CONNECT_TIMEOUT", "300000");
		System.setProperty("oracle.jdbc.ReadTimeout", "900000");
	}

	// Helper function to convert SQL Type from int to string
	// Add more as needed
	public static String typeConvert(int type) {
		
		switch(type) {
			case -7:
				return "Bit";
			case -2:
				return "Binary";
			case 0:
				return "Null";
			case 1:
				return "Char";
			case 2:
				return "Numeric";
			case 3:
				return "Decimal";
			case 4:
				return "Integer";
			case 5:
				return "SmallInt";
			case 6:
				return "Float";
			case 7:
				return "Real";
			case 8:
				return "Double";
			case 12:
				return "Varchar";
			case 16:
				return "Boolean";
			case 70:
				return "Datalink";
			case 91:
				return "Date";
			case 92: 
				return "Time";
			case 93: 
				return "Timestamp";
			case 2000:
				return "JavaObject";
			case 2002:
				return "Struct";
			case 2003:
				return "Array";
			case 2004:
				return "Blob";
			case 2005:
				return "Clob";
			case 2014:
				return "Timestamp with time zone";
			default:
				return "Type not in Conversion Table: "+Integer.toString(type);
		}
	}

	public static String errorToResponse(String info, String error) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();

			ObjectNode main = mapper.createObjectNode();

			main.put("request", info);
			main.put("error", error);

			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
		}
		catch(Exception ex) {
			return "error: Unable to format JSON response to display error: "+error+" for "+info;
		}
	}

}
