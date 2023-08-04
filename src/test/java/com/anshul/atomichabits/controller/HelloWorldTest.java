package com.anshul.atomichabits.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(HelloWorld.class)
class HelloWorldTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getHelloWorld_basic() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(request)
				.andExpect(status().is(200))
				.andExpect(content().string("Hello"))
				.andReturn();

		assertEquals("Hello", result.getResponse().getContentAsString());
	}

}
