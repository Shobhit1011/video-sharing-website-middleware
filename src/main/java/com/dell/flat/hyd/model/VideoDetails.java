package com.dell.flat.hyd.model;

public class VideoDetails {
	Video video;
	String uploaded_by;
	public Video getVideo() {
		return video;
	}
	public void setVideo(Video video) {
		this.video = video;
	}
	public String getUploader() {
		return uploaded_by;
	}
	public void setUploader(String uploader) {
		this.uploaded_by = uploader;
	}
}
