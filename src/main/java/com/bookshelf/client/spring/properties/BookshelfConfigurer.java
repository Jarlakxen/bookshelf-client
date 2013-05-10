package com.bookshelf.client.spring.properties;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bookshelf.client.connector.JerseyConnector;
import com.bookshelf.client.connector.RESTConnector;

public abstract class BookshelfConfigurer {

    public static final Class<? extends RESTConnector> DEFAULT_REST_CONNECTOR = JerseyConnector.class;
    public static final String BOOKSHELF_URL = "http://%s/bookshelf/query/%s/%s/%s";

    public static final String HOST_SYSTEM_PROPERTY_KEY = "bookshelf.client.host";
    public static final String ENVIROMENT_SYSTEM_PROPERTY_KEY = "bookshelf.client.env";

    protected boolean continueWithConnectionErrors = false;

    protected RESTConnector connector;

    protected String serverUrl;

    public BookshelfConfigurer(Class<? extends RESTConnector> restConnector, String bookshelfDomain, String projectName,
        String moduleName, String enviroment) {

        this.serverUrl = String.format(BOOKSHELF_URL, bookshelfDomain, projectName, moduleName, enviroment);

        try {

            this.getLogger().debug("Creating RESTConnector [" + restConnector.getSimpleName() + "]  for " + this.serverUrl);

            this.connector = restConnector.getConstructor(String.class).newInstance(this.serverUrl);
        } catch (Exception e) {

            this.getLogger().warn("Cannot establish connection with " + this.serverUrl, e);

            if (!this.continueWithConnectionErrors) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, String> getPropertiesFromServer() {

        if (this.connector == null) {
            return Collections.emptyMap();
        }

        Map<String, String> properties = Collections.emptyMap();

        try {

            properties = this.connector.get();

            this.getLogger().info("Properties From Server:\n" + properties);

        } catch (Exception e) {
            if (!this.continueWithConnectionErrors) {
                throw new RuntimeException("Cannot get values from " + this.getServerUrl(), e);
            } else {
                this.getLogger().warn("Cannot get values from with " + this.getServerUrl(), e);
            }
        }

        return properties;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public boolean isContinueWithConnectionErrors() {
        return this.continueWithConnectionErrors;
    }

    public void setContinueWithConnectionErrors(boolean continueWithConnectionErrors) {
        this.continueWithConnectionErrors = continueWithConnectionErrors;
    }

    public abstract Logger getLogger();
}
