package com.bookshelf.client.connector;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class JerseyConnector
    extends RESTConnector {

    private WebResource webResource;

    public JerseyConnector(String endpoint) {
        super(endpoint);

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
        clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, READ_TIMEOUT);
        clientConfig.getClasses().add(JacksonJsonProvider.class);

        this.webResource = Client.create(clientConfig).resource(endpoint);
    }

    @Override
    public Map<String, String> get() {

        ClientResponse response = this.webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.getEntity(new GenericType<Map<String, String>>() {});
    }
}
