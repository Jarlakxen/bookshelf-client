package com.bookshelf.client.spring.properties;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import com.bookshelf.client.connector.JerseyConnector;
import com.bookshelf.client.connector.RESTConnector;


public class BookshelfPropertyPlaceholderConfigurer
    implements BeanFactoryPostProcessor, BeanFactoryAware, BeanNameAware, PriorityOrdered {

	public static final Class<? extends RESTConnector> DEFAULT_REST_CONNECTOR = JerseyConnector.class;
	public static final String BOOKSHELF_URL = "http://%s/bookshelf/query/%s/%s/%s";

	public static final String HOST_SYSTEM_PROPERTY_KEY = "bookshelf.client.host";
	public static final String ENVIROMENT_SYSTEM_PROPERTY_KEY = "bookshelf.client.env";
	
    protected BeanFactory beanFactory;
    protected String beanName;
    
    protected String fileEncoding = null; // "utf-8";
    protected String nullValue = "NULL";
    private boolean ignoreUnresolvablePlaceholders = true;
    private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;

    private RESTConnector connector;
    
    private String serverUrl;
   
    public BookshelfPropertyPlaceholderConfigurer(String projectName, String moduleName) {
    	this(DEFAULT_REST_CONNECTOR, projectName, moduleName);
    }
    
    public BookshelfPropertyPlaceholderConfigurer(Class<? extends RESTConnector> restConnector, String projectName, String moduleName) {
    	this(restConnector, System.getProperty(HOST_SYSTEM_PROPERTY_KEY), projectName, moduleName, System.getProperty(ENVIROMENT_SYSTEM_PROPERTY_KEY));
    }
    
    public BookshelfPropertyPlaceholderConfigurer(String bookshelfDomain, String projectName, String moduleName, String enviroment) {
    	this(DEFAULT_REST_CONNECTOR, bookshelfDomain, projectName, moduleName, enviroment);
    }
    
   	public BookshelfPropertyPlaceholderConfigurer(Class<? extends RESTConnector> restConnector, String bookshelfDomain, String projectName, String moduleName, String enviroment) {
    	
   		if(bookshelfDomain==null){
   			throw new RuntimeException("The domain of the server is null!");
   		}

   		if(enviroment==null){
   			throw new RuntimeException("The enviroment is null!");
   		}
   		
   		serverUrl = String.format(BOOKSHELF_URL, bookshelfDomain, projectName, moduleName, enviroment);
   		
   		try {
			connector = restConnector.getConstructor(String.class).newInstance(serverUrl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    public Map<String, String> getPropertiesFromServer(){
		return connector.get();
    }
    
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PropertyPlaceholderConfigurer pphc = new PropertyPlaceholderConfigurer();

        // Configure the configurer (!)
        pphc.setBeanFactory(beanFactory);
        pphc.setBeanName("##BookshelfPropertyPlaceholderConfigurer##@" + this.beanName);
        pphc.setIgnoreResourceNotFound(true);
        pphc.setIgnoreUnresolvablePlaceholders(this.ignoreUnresolvablePlaceholders);
        pphc.setFileEncoding(this.fileEncoding);
        pphc.setNullValue(this.nullValue);
        pphc.setSystemPropertiesMode(this.systemPropertiesMode);

        Properties dummyProps = new Properties();
        
        for(Entry<String, String> entry : getPropertiesFromServer().entrySet()){
        	dummyProps.setProperty(entry.getKey(), entry.getValue());
        }
        
        pphc.setProperties(dummyProps);
        
        // process...
        pphc.postProcessBeanFactory(beanFactory);
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
		return fileEncoding;
	}
    
    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
    
    public boolean isIgnoreUnresolvablePlaceholders() {
		return ignoreUnresolvablePlaceholders;
	}
    
    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }
    
    public String getNullValue() {
		return nullValue;
	}

    public void setSystemPropertiesMode(int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }
    
    public int getSystemPropertiesMode() {
		return systemPropertiesMode;
	}
    
    public String getServerUrl() {
		return serverUrl;
	}
}

