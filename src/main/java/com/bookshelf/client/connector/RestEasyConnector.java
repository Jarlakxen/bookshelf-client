package com.bookshelf.client.connector;

import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;

public class RestEasyConnector
    extends RESTConnector {

    private ClientRequest clientRequest;

    public RestEasyConnector(String endpoint) {
        super(endpoint);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);

        ClientExecutor executor = new ApacheHttpClient4Executor(httpClient);

        this.clientRequest = new ClientRequest(endpoint, executor);
        this.clientRequest.accept("application/*+json");
    }

    @Override
    public Map<String, String> get() {
        ClientResponse<?> response;
        try {
            response = this.clientRequest.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.getEntity(new GenericType<Map<String, String>>() {});
    }

}
