package com.bookshelf.client.spring.properties;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;


public class BookshelfPropertyPlaceholderConfigurer
    implements BeanFactoryPostProcessor, BeanFactoryAware, BeanNameAware, PriorityOrdered {

	public static final String BOOKSHELF_URL = "http://%s/bookshelf/query/%s/%s/%s";
	
    protected BeanFactory beanFactory;
    protected String beanName;
    
    protected String fileEncoding = null; // "utf-8";
    protected String nullValue = "NULL";
    private boolean ignoreUnresolvablePlaceholders = true;
    private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;

    private String bookshelfDomain;
    private String projectName;
    private String moduleName;
    private String enviroment;
    
    public BookshelfPropertyPlaceholderConfigurer(String bookshelfDomain, String projectName, String moduleName, String enviroment) {
    	this.bookshelfDomain = bookshelfDomain;
    	this.projectName = projectName;
    	this.moduleName = moduleName;
    	this.enviroment = enviroment;
	}
    
    private Map<String, String> getPropertiesFromServer(){
    	String serverUrl = String.format(BOOKSHELF_URL, bookshelfDomain, projectName, moduleName, enviroment);
    	
    	ClientRequest request = new ClientRequest(serverUrl);
		request.accept("application/json");
		
		ClientResponse<?> response;
		
		try {
			response = request.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		return response.getEntity( new GenericType<Map<String, String>>() {});
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
}

