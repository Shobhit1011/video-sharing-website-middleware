package com.dell.flat.hyd.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class SendNotifications {
	 @Bean
	 public RestTemplate getRestTemplate() {
	     return new RestTemplate();
	 }
	 
	 @Autowired
	 public RestTemplate restTemplate;
	 
	 @Autowired(required = true)
	 VideoDao d;
	
	 public void sendMessage(String message, String channel){
		 	SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss"); 
			Date date = new Date();
			String strDate = formatter.format(date);
			int id = d.getLastNotificationId();
		 	UserNotification userNotification = new UserNotification();
		 	userNotification.setId(id + 1);
		    userNotification.setChannel(channel);
		    userNotification.setMessage(message);
		    userNotification.setTime(strDate);
		    userNotification.setStatus("unread");
		    d.save(userNotification);
		 	HttpHeaders headers = new HttpHeaders();
		    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		    Message notification = new Message();
		    notification.setMessage(message);
		    HttpEntity<Message> entity = new HttpEntity<Message>(notification,headers);
			restTemplate.exchange("http://localhost:8002/send/message/" + channel, HttpMethod.POST, entity, String.class).getBody();
	}
}
