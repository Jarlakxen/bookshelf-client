package com.bookshelf.client.connector;

import java.util.Map;

public abstract class RESTConnector {
	
	private String endpoint;

	public RESTConnector(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public abstract Map<String, String> get();
	
	public String getEndpoint() {
		return endpoint;
	}
}
