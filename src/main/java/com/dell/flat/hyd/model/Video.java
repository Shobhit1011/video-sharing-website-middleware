package com.dell.flat.hyd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "video")
public class Video {
	@Id
	private int id;
	
	@Column
	private String name_in_folder;
	
	@Column
	private String size;
	
	@Column
	private int user_id;
	
	@Column
	private String description;
	
	@Column
	private String manifest_path;
	
	@Column
	private String videoName;
	
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public String getManifest_path() {
		return manifest_path;
	}
	public void setManifest_path(String manifest_path) {
		this.manifest_path = manifest_path;
	}
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
