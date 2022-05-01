package com.cheapbuy.ordersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import com.thoughtworks.xstream.XStream;

@SpringBootApplication
@EnableDiscoveryClient
public class OrdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersServiceApplication.class, args);
	}
	
    /**
     Hack to solve the ForbiddentClassCastException occuring becuase of serilaizaion problem.
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
