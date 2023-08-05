package com.anshul.atomichabits.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(HelloWorld.class)
//@ContextConfiguration
@WithMockUser(username="1")
class HelloWorldTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void getHelloWorld_basic() throws Exception {

		RequestBuilder request = MockMvcRequestBuilders.get("/")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(request)
				.andExpect(status().is(200))
				.andExpect(content().string("Hello 1"))
				.andReturn();

		assertEquals("Hello 1", result.getResponse().getContentAsString());
	}

}
