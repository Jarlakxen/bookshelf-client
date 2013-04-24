package com.bookshelf.client.spring.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import com.bookshelf.client.connector.RESTConnector;


/**
 * This class is for Spring 3.1 or grater
 * 
 * @author fviale
 *
 */
public class BookshelfPropertySourcePlaceholderConfigurer
    extends BookshelfConfigurer
    implements BeanFactoryPostProcessor, EnvironmentAware, BeanFactoryAware, BeanNameAware, PriorityOrdered {

    private static final Logger LOGGER = Logger.getLogger(BookshelfPropertySourcePlaceholderConfigurer.class);

    protected BeanFactory beanFactory;
    protected String beanName;
    private Environment environment;

    protected String fileEncoding = null; // "utf-8";
    protected String nullValue = "NULL";
    private boolean ignoreUnresolvablePlaceholders = true;
    private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;

    public BookshelfPropertySourcePlaceholderConfigurer(String projectName, String moduleName) {
        this(DEFAULT_REST_CONNECTOR, projectName, moduleName);
    }

    public BookshelfPropertySourcePlaceholderConfigurer(String bookshelfDomain, String projectName, String moduleName,
        String enviroment) {
        this(DEFAULT_REST_CONNECTOR, bookshelfDomain, projectName, moduleName, System
            .getProperty(ENVIROMENT_SYSTEM_PROPERTY_KEY) != null ? System.getProperty(ENVIROMENT_SYSTEM_PROPERTY_KEY)
            : enviroment);
    }

    public BookshelfPropertySourcePlaceholderConfigurer(String bookshelfDomain, String projectName, String moduleName) {
        this(DEFAULT_REST_CONNECTOR, bookshelfDomain, projectName, moduleName, System
            .getProperty(ENVIROMENT_SYSTEM_PROPERTY_KEY));
    }

    public BookshelfPropertySourcePlaceholderConfigurer(Class<? extends RESTConnector> restConnector, String projectName,
        String moduleName) {
        this(restConnector, System.getProperty(HOST_SYSTEM_PROPERTY_KEY), projectName, moduleName, System
            .getProperty(ENVIROMENT_SYSTEM_PROPERTY_KEY));
    }

    public BookshelfPropertySourcePlaceholderConfigurer(Class<? extends RESTConnector> restConnector,
        String bookshelfDomain, String projectName, String moduleName, String enviroment) {

        super(restConnector, bookshelfDomain, projectName, moduleName, enviroment);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (this.environment instanceof ConfigurableEnvironment) {

            Map<String, Object> properties = new HashMap<String, Object>();

            for (Entry<String, String> entry : this.getPropertiesFromServer().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }

            ((ConfigurableEnvironment) this.environment).getPropertySources().addFirst(
                new MapPropertySource("localProps", properties));
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getFileEncoding() {
        return this.fileEncoding;
    }

    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public boolean isIgnoreUnresolvablePlaceholders() {
        return this.ignoreUnresolvablePlaceholders;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public String getNullValue() {
        return this.nullValue;
    }

    public void setSystemPropertiesMode(int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }

    public int getSystemPropertiesMode() {
        return this.systemPropertiesMode;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }
}
