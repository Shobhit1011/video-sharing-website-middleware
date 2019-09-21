package com.dell.flat.hyd.model;

import java.util.List;

public class Structure {
 private User user;
 private List<Video> videos;
public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public List<Video> getVideos() {
	return videos;
}
public void setVideos(List<Video> videos) {
	this.videos = videos;
}
}
