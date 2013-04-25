package com.bookshelf.client.spring.properties;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BookshelfPropertyPlaceholderConfigurerTest{

	private BookshelfPropertyPlaceholderConfigurer bookshelfPropertyPlaceholderConfigurer;
	
	@Before
	public void setUp(){
		
		bookshelfPropertyPlaceholderConfigurer = new BookshelfPropertyPlaceholderConfigurer("localhost:8080", "P1", "M1", "RC");

	}
	
	@Test
    public void testGetPropertiesFromServer(){
    	
		Map<String, String> values = bookshelfPropertyPlaceholderConfigurer.getPropertiesFromServer();
		
		Assert.assertNotNull(values);
		Assert.assertEquals(1, values.size());
		
    }
}

