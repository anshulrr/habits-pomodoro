package com.anshul.atomichabits.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.security.test.context.support.WithMockUser;

import com.anshul.atomichabits.business.UserService;
import com.anshul.atomichabits.model.User;

@WebMvcTest(UserResource.class)
//@ContextConfiguration
public class UserResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	@WithMockUser(authorities= {"admin"})
	void retrieveAllUsers_withAdminAuthority() throws Exception {

		when(userService.retriveAllUsers()).thenReturn(
				Arrays.asList(
						new User("Prateek", "prateek@xyz.com"),
						new User("Pulkit", "pulkit@xyz.com")
						));
		
		RequestBuilder request = MockMvcRequestBuilders.get("/users")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(request)
				.andExpect(status().is(200))
				.andExpect(content().json("""
						[
							{"username": "Prateek", "email": "prateek@xyz.com"},
							{"username": "Pulkit", "email": "pulkit@xyz.com"}
						]
					  	"""))
				.andReturn();
		
		JSONAssert.assertEquals("[{username:Prateek}, {username:Pulkit}]", result.getResponse().getContentAsString(), false);
	}
	
	@Test
	@WithMockUser(authorities= {"user"})
	void retrieveAllUsers_withNonAdminAuthority() throws Exception {

		RequestBuilder request = MockMvcRequestBuilders.get("/users")
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(request)
				.andExpect(status().is(403))
				.andReturn();
	}
}
