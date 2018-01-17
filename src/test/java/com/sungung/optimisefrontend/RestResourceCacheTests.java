package com.sungung.optimisefrontend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.sungung.optimisefrontend.api.RestApi;
import com.sungung.optimisefrontend.entity.Customer;
import com.sungung.optimisefrontend.repository.CustomerRepository;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RestApi.class)
public class RestResourceCacheTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CustomerRepository customerRepository;
	
	@Test
	public void should_return_cached_resource() throws Exception{
		
		// given - entity version is 1
		Customer customer = new Customer(1L,"James","Dean","jamesd@helloworld.com",1L);
		
		given(customerRepository.findOne(1L)).willReturn(customer);
		
		// when - header etag is 1 in first request
		MvcResult result = mockMvc.perform(get("/api/customer/1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(header().string("ETag", "\"1\""))
				.andExpect(header().string("Cache-Control", "max-age=31536000"))
				.andExpect(status().isOk())
				.andDo(print()).andReturn();
		
		// then - second get request should return 304 
		mockMvc.perform(get("/api/customer/1")
						.header("If-None-Match", "\"1\"")
						.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotModified());
	}
	
	@Test
	public void should_return_udpated_resource_from_server() throws Exception {
		
		// given - entity version is 1
		Customer customer = new Customer(1L, "James", "Dean", "james@hellowld.com", 1L);
		
		given(customerRepository.findOne(1L)).willReturn(customer);

		// when - header etag is 1 in first request 
		MvcResult result = mockMvc.perform(get("/api/customer/1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(header().string("ETag", "\"1\""))
				.andExpect(header().string("Cache-Control", "max-age=31536000"))
				.andExpect(status().isOk())
				.andDo(print()).andReturn();

		// when - update the version of customer entity 
		customer.setVersion(2L);

		// then - second request should return 200
		mockMvc.perform(get("/api/customer/1")
				.header("If-None-Match", "\"1\"")
				.accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andDo(print());

		
	}
}
