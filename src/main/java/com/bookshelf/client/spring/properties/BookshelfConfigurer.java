package com.bookshelf.client.spring.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import com.bookshelf.client.connector.JerseyConnector;
import com.bookshelf.client.connector.RESTConnector;

public abstract class BookshelfConfigurer {

    public static final Class<? extends RESTConnector> DEFAULT_REST_CONNECTOR = JerseyConnector.class;
    public static final String BOOKSHELF_URL = "http://%s/bookshelf/query/%s/%s/%s";

    public static final String HOST_SYSTEM_PROPERTY_KEY = "bookshelf.client.host";
    public static final String ENVIROMENT_SYSTEM_PROPERTY_KEY = "bookshelf.client.env";

    protected boolean continueWithConnectionErrors = false;

    protected boolean backupMode = true;
    protected String backupPath = System.getProperty("java.io.tmpdir");

    protected RESTConnector connector;

    protected String bookshelfDomain;
    protected String projectName;
    protected String moduleName;
    protected String enviroment;
    protected String serverUrl;

    public BookshelfConfigurer(Class<? extends RESTConnector> restConnector, String bookshelfDomain, String projectName,
        String moduleName, String enviroment) {

        this.bookshelfDomain = bookshelfDomain;
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.enviroment = enviroment;
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

            if (this.backupMode && properties != null && !properties.isEmpty()) {
                this.storeBackUp(properties);
            }

            this.getLogger().info("Properties From Server:\n" + properties);

        } catch (Exception e) {
            if (!this.continueWithConnectionErrors) {
                throw new RuntimeException("Cannot get values from " + this.getServerUrl(), e);
            } else {
                this.getLogger().warn("Cannot get values from with " + this.getServerUrl(), e);
            }

            if (this.backupMode) {
                properties = this.loadBackUp();
            }
        }

        return properties;
    }

    private void storeBackUp(Map<String, String> properties) {

        Properties backupProperties = new Properties();
        backupProperties.putAll(properties);

        File backupFile = new File(this.backupPath + "bookshelf-client_backup.properties");

        if (backupFile.exists()) {
            backupFile.delete();
        }

        try {
            FileWriter backupFileWriter = new FileWriter(backupFile);

            backupProperties.store(backupFileWriter, DateUtil.formatDate(new Date()));

            backupFileWriter.close();
        } catch (IOException e) {
            this.getLogger().error(e);
        }
    }

    private Map<String, String> loadBackUp() {

        File backupFile = new File(this.backupPath + "bookshelf-client_backup.properties");

        if (!backupFile.exists()) {
            return Collections.emptyMap();
        }

        try {
            FileInputStream backupFileInputStream = new FileInputStream(backupFile);
            Properties backupProperties = new Properties();
            backupProperties.load(backupFileInputStream);
            backupFileInputStream.close();

            Map<String, String> properties = new HashMap<String, String>();
            for (Entry<Object, Object> entry : backupProperties.entrySet()) {
                properties.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return properties;

        } catch (Exception e) {
            this.getLogger().error(e);
        }

        return Collections.emptyMap();
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public String getBookshelfDomain() {
        return this.bookshelfDomain;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getEnviroment() {
        return this.enviroment;
    }

    public boolean isContinueWithConnectionErrors() {
        return this.continueWithConnectionErrors;
    }

    public void setContinueWithConnectionErrors(boolean continueWithConnectionErrors) {
        this.continueWithConnectionErrors = continueWithConnectionErrors;
    }

    public boolean isBackupMode() {
        return this.backupMode;
    }

    public void setBackupMode(boolean backupMode) {
        this.backupMode = backupMode;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public abstract Logger getLogger();
}
