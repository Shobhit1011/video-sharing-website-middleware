package com.dell.flat.hyd;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.dell.flat.hyd.model.Comments;
import com.dell.flat.hyd.model.JSONBuilder;
import com.dell.flat.hyd.model.Notification;
import com.dell.flat.hyd.model.User;
import com.dell.flat.hyd.model.VideoDao;

@Controller
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class WebSocketController {
	private SimpMessagingTemplate template;
	
	@Autowired(required = true)
	VideoDao d;
	
	@Autowired(required = true)
	JSONBuilder builder;
	
	@Autowired
	WebSocketController(SimpMessagingTemplate template){
		this.template = template;
	}
	
	@MessageMapping("/send/message/{videoId}/{userId}")
	public void send(@Payload String message, @DestinationVariable int videoId, @DestinationVariable int userId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
			User user = (User)d.getUserById(userId);
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");  
			String strDate = formatter.format(date);
			Comments comment = new Comments();
			comment.setUserName(user.getFirstName() + " " + user.getLastName());
			comment.setVideoId(videoId);
			comment.setTime(strDate);
			comment.setComment(message);
			d.save(comment);
			this.template.convertAndSend("/topic/chat", builder.objectToJSON(comment));
	}
	
	@MessageMapping("/notifications/{uploader:.+}")
	public void sendNotifications(@Payload String message, @DestinationVariable String uploader) {
		System.out.println("Inside notifications " + uploader);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss"); 
		Date date = new Date();
		String strDate = formatter.format(date);
		Notification notification = new Notification();
		notification.setDate(strDate);
		notification.setMessage(message);
		String dest = "/topic/" + uploader;
		System.out.println(dest);
		System.out.println(builder.objectToJSON(notification));
		this.template.convertAndSend(dest, builder.objectToJSON(notification));
	}
}