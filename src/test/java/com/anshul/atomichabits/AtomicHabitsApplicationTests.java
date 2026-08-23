package com.anshul.atomichabits;

import org.springframework.security.oauth2.jwt.JwtDecoder;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.firebase.FirebaseApp;

@SpringBootTest
class AtomicHabitsApplicationTests {

	@MockBean
	JwtDecoder jwtDecoder;

	@MockBean
	FirebaseApp firebaseApp;

	@Test
	void contextLoads() {
	}

}
