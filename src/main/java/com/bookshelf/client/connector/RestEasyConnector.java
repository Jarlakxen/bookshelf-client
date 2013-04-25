package com.bookshelf.client.connector;

import java.util.Map;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;

public class RestEasyConnector extends RESTConnector{

	private ClientRequest clientRequest;
	
	public RestEasyConnector(String endpoint) {
		super(endpoint);
		
		clientRequest = new ClientRequestFactory().createRequest(endpoint);
		clientRequest.accept("application/*+json");
	}

	@Override
	public Map<String, String> get() {
		ClientResponse<?> response;		
		try {
			response = clientRequest.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		return response.getEntity( new GenericType<Map<String, String>>() {});
	}
	
}
