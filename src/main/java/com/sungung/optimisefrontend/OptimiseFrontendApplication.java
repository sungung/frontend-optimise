package com.sungung.optimisefrontend;


import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sungung.optimisefrontend.entity.Customer;
import com.sungung.optimisefrontend.repository.CustomerRepository;

@SpringBootApplication
public class OptimiseFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptimiseFrontendApplication.class, args);
	}
	
    @Configuration    
    public static class WebConfig extends WebMvcConfigurerAdapter{
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/assets/**")
			.addResourceLocations("classpath:/static/assets/")
			.setCacheControl(CacheControl.maxAge(5, TimeUnit.DAYS));
		}    	
    }	
	
    @Bean
    public CommandLineRunner demo(CustomerRepository repository){
    	return (args) -> {
			repository.save(new Customer("Jack", "Bauer", "jack@helloworld.com"));
			repository.save(new Customer("Chloe", "O'Brian", "chloe@helloworld.com"));
			repository.save(new Customer("Kim", "Bauer", "kim@helloworld.com"));
			repository.save(new Customer("David", "Palmer", "david@helloworld.com"));
			repository.save(new Customer("Michelle", "Dessler", "michelle@helloworld.com"));    	
    	};
    }
    
}
