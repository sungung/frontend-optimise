package com.sungung.optimisefrontend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaticResourcesCacheTests {
	
	@Autowired
	private MockMvc mockMvc;
		
	@Test
	public void should_return_main_page() throws Exception {
		this.mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(status().isOk());
	}
	
	@Test
	public void should_return_cached_static_resource() throws Exception {
		
		// do first request cacheable resource then get 200 status with max-age
		MvcResult result = this.mockMvc.perform(get("/assets/cached.txt"))
				.andDo(print())
				.andExpect(header().string("Cache-Control", "max-age=432000"))
				.andExpect(status().isOk())
				.andReturn();
		
		String lastModified = result.getResponse().getHeader("Last-Modified");
		
		// when attach if-modified-since header in second request then get 304 status
		this.mockMvc.perform(get("/assets/cached.txt").header("If-Modified-Since", lastModified))
			.andDo(print())
			.andExpect(status().isNotModified());		
	}
	
	@Test
	public void should_return_always_from_server() throws Exception{
		
		// do first request non cacheable resource then get 200 status without max-age
		MvcResult result = this.mockMvc.perform(get("/uncached.txt"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		// second request then get 200 status
		this.mockMvc.perform(get("/uncached.txt"))
			.andDo(print())
			.andExpect(status().isOk());				
	}
}
