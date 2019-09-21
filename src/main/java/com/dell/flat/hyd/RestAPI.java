package com.dell.flat.hyd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.dell.flat.hyd.model.ApplicationError;
import com.dell.flat.hyd.model.ExtractImageFromVideo;
import com.dell.flat.hyd.model.JSONBuilder;
import com.dell.flat.hyd.model.Login;
import com.dell.flat.hyd.model.Message;
import com.dell.flat.hyd.model.MultipartFileSender;
import com.dell.flat.hyd.model.Ratings;
import com.dell.flat.hyd.model.Recommendation;
import com.dell.flat.hyd.model.Session;
import com.dell.flat.hyd.model.Structure;
import com.dell.flat.hyd.model.Subscription;
import com.dell.flat.hyd.model.User;
import com.dell.flat.hyd.model.Video;
import com.dell.flat.hyd.model.VideoDao;
import com.dell.flat.hyd.model.VideoDetails;
import com.google.gson.Gson;

@CrossOrigin(origins={"http://localhost:4200","http://192.168.1.78:4200"},allowCredentials = "true")
@Controller
public class RestAPI {
	
	File dir;
	
	@Autowired(required = true)
	ExtractImageFromVideo videoToImageExtractor;
	
	@Autowired(required = true)
	VideoDao d;
	
	@Autowired(required = true)
	Recommendation reccommendation;
	
	@Autowired(required = true)
	JSONBuilder builder;
	HttpSession session;

	private FileInputStream fileInputStream;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/auth", produces = "application/json")
	@ResponseBody
	public ResponseEntity auth(HttpServletRequest req) {
		session = req.getSession(false);
		if (session == null) {
			Session ses = new Session();
			ses.setSession("false");
			return new ResponseEntity(ses,HttpStatus.ACCEPTED);
		} else {
			User user = (User) session.getAttribute("user");
			return new ResponseEntity(user,HttpStatus.ACCEPTED);
		}
	}

	@RequestMapping("/getCar")
	@ResponseBody
	public String getCar() {
		return "Hello_World";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/postVideo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity postCar(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
			@RequestParam("description") String description, HttpServletResponse res) {
		try {
			System.out.println(name);
			User user = (User) session.getAttribute("user");
			byte bytes[] = file.getBytes();
			dir = new File("E:/tmpFiles");
			String fileName = name;
			if (!dir.exists())
				dir.mkdir();
			File serverFile = new File(dir.getAbsoluteFile() + File.separator + fileName);
			System.out.println();
			if(!serverFile.exists()) {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				System.out.println("Path of the Uploaded File is " + serverFile.getAbsolutePath());
				videoToImageExtractor.extractImage(fileName);
				Video video = new Video();
				video.setName_in_folder(fileName);
				video.setSize(String.valueOf(file.getSize()));
				video.setId(d.getVideoCount()+1);
				video.setUser_id(user.getId());
				video.setDescription(description);
				d.save(video);
				return new ResponseEntity(video,HttpStatus.ACCEPTED);
			}
			else {
				ApplicationError error = new ApplicationError();
				error.setCode(400);
				error.setMessage("File with this name already exists ");
				return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			System.out.println(e);
			ApplicationError error = new ApplicationError();
			
			error.setCode(400);
			error.setMessage("Bad Request");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			ex.printStackTrace();
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please to continue Uploading");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity register(@RequestParam String email, @RequestParam String FirstName, @RequestParam String LastName,
			@RequestParam long PhoneNo, @RequestParam String Password) {
		User a = new User();
		a.setId(d.getUserCount()+1);
		a.setEmail(email);
		a.setFirstName(FirstName);
		a.setLastName(LastName);
		a.setPhoneNo(PhoneNo);
		a.setPassword(Password);
		d.save(a);
		return new ResponseEntity(a,HttpStatus.ACCEPTED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity login(HttpServletRequest req, @RequestBody String json) {
		Gson gson = builder.getGsonObject();
		Login credentials = gson.fromJson(json, Login.class);
		
		ApplicationError error = new ApplicationError();
		
		if(credentials.getEmail() == null || credentials.getEmail().trim().equals("")) {
			error.setCode(428);
			error.setStatusCode("PRE_CONDITION_REQUIRED");
			error.setMessage("Email cannot be empty or blank");
			return new ResponseEntity(error, HttpStatus.PRECONDITION_REQUIRED);
		}
		
		else if(credentials.getPassword() == null || credentials.getPassword().trim().equals("")) {
			error.setCode(428);
			error.setStatusCode("PRE_CONDITION_REQUIRED");
			error.setMessage("Password cannot be empty or blank");
			return new ResponseEntity(error, HttpStatus.PRECONDITION_REQUIRED);
		}
		
		User user = d.get(credentials.getEmail());
		
		if(user == null) {
			error.setCode(404);
			error.setMessage("User not found");
			error.setStatusCode("USER_NOT_FOUND");
			return new ResponseEntity(error, HttpStatus.NOT_FOUND);
		}
		else if (user.getEmail().equals(credentials.getEmail()) && user.getPassword().equals(credentials.getPassword())) {
			session = req.getSession();
			session.setAttribute("user", user);
			return new ResponseEntity(user,HttpStatus.ACCEPTED);
		}
		else {
			error.setCode(400);
			error.setStatusCode("INVALID_CREDENTIALS");
			error.setMessage("Invalid credentials");
			return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/logout", produces = "application/json")
	@ResponseBody
	public ResponseEntity logout(HttpServletRequest req, HttpServletResponse res) {
		session = req.getSession(false);
		if (session != null) {
			session.invalidate();
			Message message = new Message();
			message.setMessage("Logout Successfully");
			return new ResponseEntity(message,HttpStatus.ACCEPTED);
		} else {
			ApplicationError error = new ApplicationError();
			error.setMessage("Please Login to Logout");
			error.setCode(400);
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "getAll", produces = "application/json")
	@ResponseBody
	public ResponseEntity records() {
		List<Structure> l = new ArrayList<Structure>();
		List<User> list = d.getAll();
		Iterator<User> itr = list.iterator();
		while (itr.hasNext()) {
			User user = (User) itr.next();
			Structure struct = new Structure();
			struct.setUser(user);
			List<Video> videos = d.getAllVideosByUserId(user.getId());
			struct.setVideos(videos);
			l.add(struct);
		}
		return new ResponseEntity(l,HttpStatus.ACCEPTED);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity subscribe(@RequestParam int video_id, HttpServletRequest req, HttpServletResponse res) {
		session = req.getSession(false);
		if (session == null) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please before subscribing");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		} else {
			try {
				Video video = d.getVideoById(video_id);
				Subscription subscription = new Subscription();
				User user = (User) session.getAttribute("user");
				subscription.setSerial_no(d.count() + 1);
				subscription.setUser_id(user.getId());
				subscription.setSubscription_id(video.getUser_id());
				d.save(subscription);
				return new ResponseEntity(subscription,HttpStatus.ACCEPTED);
			} catch (NullPointerException ex) {
				session.invalidate();
				ApplicationError error = new ApplicationError();
				error.setCode(400);
				error.setMessage("First Login Please before subscribing");
				return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/getSubscriptions", produces = "application/json")
	@ResponseBody
	public ResponseEntity subcriptions(@RequestParam int userId, HttpServletRequest req, HttpServletResponse res) {
		session = req.getSession(false);
		if (session == null) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please before getting subscriptions");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}

		User user_querying = (User) session.getAttribute("user");
		if (user_querying == null) {
			session.invalidate();
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please before getting subscriptions");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
		List<Subscription> list = d.subscriptionsByUserId(userId);
		Iterator<Subscription> itr = list.iterator();
		TreeSet<String> tr = new TreeSet<String>();
		while (itr.hasNext()) {
			Subscription subscription = itr.next();
			User user = d.getUserById(subscription.getSubscription_id());
			tr.add(user.getFirstName() + " " + user.getLastName());
		}
		return new ResponseEntity(tr,HttpStatus.BAD_REQUEST);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/search", produces = "application/json")
	@ResponseBody
	public ResponseEntity searchByName(@RequestParam String name, HttpServletResponse res) throws IOException {
		if (name.equals("")) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("Name cannot be blank");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
		List<Video> list = d.search(name);
		if (list.isEmpty()) {
			ApplicationError error = new ApplicationError();
			error.setCode(404);
			error.setMessage("No Result Found");
			return new ResponseEntity(error,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(list,HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value="/videoStreaming")
	@ResponseBody
	public void getVideo(HttpServletRequest req,HttpServletResponse response,@RequestParam String filename) throws Exception{
		dir = new File("E:/tmpFiles");
        String value = dir+File.separator+filename;
        Path path = Paths.get(value);
        System.out.println(path);
		MultipartFileSender.fromPath(path)
        .with(req)
        .with(response)
        .serveResource();
	}
	
	@RequestMapping(value="/videoById",produces="application/json")
	@ResponseBody
	public String videoById(@RequestParam int id) {
		VideoDetails details = new VideoDetails();
		Video video = d.getVideoById(id);
		User user = d.getUserById(video.getUser_id());
		details.setUploader(user.getFirstName() + " " + user.getLastName());
		details.setVideo(video);
		return builder.objectToJSON(details);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="recommend",produces="application/json")
	public ResponseEntity recc() throws IOException{
		try {
		User user = (User)session.getAttribute("user");
		List<RecommendedItem> list = reccommendation.recommend(3,user.getId());
		return new ResponseEntity(list,HttpStatus.OK);
		}
		catch(TasteException ex) {
			return new ResponseEntity(ex.getMessage(),HttpStatus.BAD_REQUEST);
		}
		catch(NullPointerException ex) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please to get Recommendations");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="ratings",produces="application/json")
	public ResponseEntity like(@RequestParam int videoId,
			@RequestParam int ratings) {
		if(session == null) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("First Login Please to give ratings");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
		else {
			try {
				User user = (User)session.getAttribute("user");
				int value = d.getRatings(user.getId(), videoId);
				System.out.println(value);
				if(value == 0) {
					Ratings rating = new Ratings();
					rating.setSerialNo(d.getRatingsCount()+1);
					rating.setUserId(user.getId());
					rating.setVideoId(videoId);
					rating.setRatings(ratings);
					d.save(rating);
					return new ResponseEntity(rating,HttpStatus.OK);
				}
				else {
					Ratings rating = d.getRatingsById(value);
					rating.setRatings(ratings);
					d.updateRatingsById(rating);
					return new ResponseEntity(rating,HttpStatus.OK);
				}
				
			}
			catch(Exception ex) {
				ex.printStackTrace();
				ApplicationError error = new ApplicationError();
				error.setCode(400);
				error.setMessage("First Login Please to give ratings");
				return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	@RequestMapping(value="getAllVideos",produces="application/json")
	@ResponseBody
	public String getAllVideos() {
		List<Video> videosList = d.getAllVideos();
		return builder.objectToJSON(videosList);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity handleMissingParams(MissingServletRequestParameterException ex) {
	    String name = ex.getParameterName();
	    System.out.println(name + " parameter is missing");
	    // Actual exception handling
	    ApplicationError error = new ApplicationError();
	    error.setCode(428);
	    error.setStatusCode("PRE_CONDITION_REQUIRED");
	    error.setMessage(name+ " parameter is missing");
	    return new ResponseEntity(error, HttpStatus.PRECONDITION_REQUIRED);
	}
	
	@RequestMapping(value = "/image", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@RequestParam String name) throws IOException {
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_PNG);
	        
			File file = new File("E:/imageFiles/"+name);
			byte[] bytesArray = new byte[(int) file.length()];
			fileInputStream = new FileInputStream(file);
			
	        fileInputStream.read(bytesArray);
	        return new ResponseEntity<byte[]>(bytesArray, headers, HttpStatus.OK);
		}
		catch(FileNotFoundException ex) {
			byte[] array = {};
			return new ResponseEntity<byte[]>(array, HttpStatus.NOT_FOUND) ;
		}
		catch(Exception ex) {
			byte[] array = {};
			ex.printStackTrace();
			return new ResponseEntity<byte[]>(array, HttpStatus.INTERNAL_SERVER_ERROR) ;
		}
	}
	
	@RequestMapping(value = "/users/{userId}", produces="application/json")
	@ResponseBody()
	public ResponseEntity<String> getUser(@PathVariable int userId){
		User user = d.getUserById(userId);
		if(user == null) {
			ApplicationError error = new ApplicationError();
			error.setCode(404);
			error.setMessage("User not found");
			return new ResponseEntity<String>(builder.objectToJSON(error),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>(builder.objectToJSON(user),HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/user/rating/{videoId}", produces="application/json")
	@ResponseBody()
	public ResponseEntity getRatingsByUser(@PathVariable int videoId) {
		try {
			User user = (User)session.getAttribute("user");
			int userId = user.getId();
			List<Ratings> list = d.getRatingsByUser(userId, videoId);
			if(list.size() == 0) {
				Message message = new Message();
				message.setMessage("NOT_RATED_YET");
				return new ResponseEntity(message,HttpStatus.OK);
			}else {
				Ratings ratings = list.get(0);
				return new ResponseEntity<Ratings>(ratings, HttpStatus.OK);
			}
		}
		catch(NullPointerException ex) {
			ApplicationError error = new ApplicationError();
			error.setCode(400);
			error.setMessage("User not logged in");
			return new ResponseEntity(error,HttpStatus.BAD_REQUEST);
		}
	}
}