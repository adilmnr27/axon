package com.cheapbuy.ProductsService.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thoughtworks.xstream.XStream;

@Configuration
public class AxonConfig {

	
	 /**
    Hack to solve the com.thoughtworks.xstream.security.ForbiddenClassException occuring becuase of serilaizaion problem.
    Obviously not to be used in production
    */
   @Bean
   public XStream xStream() {
       XStream xStream = new XStream();
     
       xStream.allowTypesByWildcard(new String[] {
               "com.cheapbuy.**"
       });
       return xStream;
   }

}
