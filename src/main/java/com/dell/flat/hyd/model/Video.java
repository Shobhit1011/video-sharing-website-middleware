package com.dell.flat.hyd.model;

public class Video {
	//final String status = "SUCCESS";
	private int id;
	private String name_in_folder;
	private String size;
	private int user_id;
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName_in_folder() {
		return name_in_folder;
	}
	public void setName_in_folder(String name_in_folder) {
		this.name_in_folder = name_in_folder;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
