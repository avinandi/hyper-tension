package com.hypertension.runner.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Avirup
 */
public class Query extends Domain {
	private final Map<String, Object> queryMap;
	private final QueryType queryType;

	Query(final HashMap<String, Object> queryMap, final QueryType type) {
		this.queryMap = queryMap;
		this.queryType = type;
	}

	public QueryType getQueryType() {
		return this.queryType;
	}

	public Map<String, Object> getQueryMap() {
		return this.queryMap;
	}

	public static enum QueryType {
		GET("GET"), POST("POST"), PUT("PUT"), DELETE("PUT");

		String queryType;

		QueryType(final String type) {
			this.queryType = type;
		}
	}
}
