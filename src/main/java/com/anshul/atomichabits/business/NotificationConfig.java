package com.anshul.atomichabits.business;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.anshul.atomichabits.dto.TaskForNotifications;
import com.anshul.atomichabits.jpa.TaskRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class NotificationConfig {

	private TaskRepository taskRepository;

	@Scheduled(cron = "0 1,31 * * * *")
	public void scheduleFixedRateTask() {
		List<TaskForNotifications> tasks = taskRepository.getNotificationTasks(Instant.now(),
				Instant.now().plusSeconds(30 * 60));

		log.info("notification for {} tasks", tasks.size());
		for (TaskForNotifications task : tasks) {
			List<String> tokens = getFireBaseTokens(task.getEmail());
			for (String token : tokens) {
				sendNotification(task, token);
			}
		}
	}

	private List<String> getFireBaseTokens(String email) {
		try {
			// get user record
			UserRecord userRecord;
			userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
		
			// See the UserRecord reference doc for the contents of userRecord.
			log.debug("Successfully fetched user data: " + userRecord.getUid());

			// get tokens from firestore
			Firestore db = FirestoreClient.getFirestore();

			ApiFuture<QuerySnapshot> future = db.collection("users/" + userRecord.getUid() + "/devices").get();
			// future.get() blocks on response
			List<QueryDocumentSnapshot> documents;
			documents = future.get().getDocuments();
			
			List<String> tokens = new ArrayList<>();
			for (QueryDocumentSnapshot document: documents) {
				Map<String, Object> obj =  document.getData();
				log.debug("device: {}", obj);
				boolean enabled = (boolean)obj.get("enabled");
				if (enabled) {
					String token = (String)obj.get("token");
					tokens.add(token);
				}
			}
			return tokens;
			
		} catch (FirebaseAuthException e) {
			// TODO Auto-generated catch block
			log.info("couldn't retrieve user record from firebase auth");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.info("retrieval device details from firebase firestore is interrupted");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			log.info("couldn't retrieve device details from firebase firestore");
			e.printStackTrace();
		}
		
		return List.of();
	}

	private void sendNotification(TaskForNotifications task, String token) {
		Message message = Message.builder()
				.putData("title", String.valueOf(task.getDueDate().toEpochMilli()))
				.putData("body", task.getDescription())
				.setToken(token)
				.build();

		// Send a message to the device corresponding to the provided registration token.
		try {
			String response;
			response = FirebaseMessaging.getInstance().send(message);
			log.debug("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("an error occurs while handing the message off to FCM for delivery.");
		}
	}
}

record UserDevice(String token, boolean enabled) {}
