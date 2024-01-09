package com.anshul.atomichabits.security;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseConfiguration {
	
	@Bean
	FirebaseApp firebaseApp() throws IOException {
		// TODO: use better and secure way
		InputStream refreshToken = getClass().getClassLoader().getResourceAsStream("firebaseServiceAccountKey.json");
		
		FirebaseOptions options = FirebaseOptions.builder()
				  .setCredentials(GoogleCredentials.fromStream(refreshToken))
				  .build();
		
		if(FirebaseApp.getApps().isEmpty()) { //<--- check with this line
            return FirebaseApp.initializeApp(options);
        }

	    return FirebaseApp.getInstance();
	}

}
