package com.hypertension.runner.tasks;

import com.hypertension.runner.dto.Query;
import com.hypertension.runner.ws.WSTask;
import com.hypertension.runner.ws.WSType;

import java.io.IOException;
import java.util.Map;

/**
 * @author Avirup
 */
public class PerformanceTestTask<T> extends WSTask<T> {

	public PerformanceTestTask(String methodName, Map<String, Object> paramMap) {
		super(WSType.API_UNDER_TEST, methodName, paramMap);
	}

	public Map runTask(final Map<String, Object> paramMap) throws IOException {
		String url;
		Query query = (Query) paramMap.get(WsTaskInputType.WS_INPUT);
		String baseUrl = (String) paramMap.get(WsTaskInputType.BASE_URI);

		if(query.getQueryType() instanceof Query.QueryType.GET) {
			url = formUri(CustomerQuery.QueryType.SSID, query.getId(), baseUrl);
		} else {
			url = formUri(CustomerQuery.QueryType.FA, query.getId(), baseUrl);
		}
		return fetchResponseAsMap(url);
	}

	private Map fetchResponseAsMap(String url) throws IOException {
		ClientResponse response = Client.create().resource(url).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		String entity = response.getEntity(String.class);
		return convertToMap(entity);
	}

	private Map convertToMap(String entity) throws IOException {
		return new ObjectMapper().readValue(entity, Map.class);
	}

	private String formUri(CustomerQuery.QueryType type, String id, String baseUrl) {
		StringBuilder builder = new StringBuilder();
		builder.append(baseUrl);
		builder.append("/search/invoices/open/");
		builder.append(type.toString().toLowerCase());
		builder.append("/");
		builder.append(id);
		return builder.toString();
	}
}
