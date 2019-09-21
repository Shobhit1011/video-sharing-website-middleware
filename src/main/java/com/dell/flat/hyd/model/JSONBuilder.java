package com.dell.flat.hyd.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONBuilder {
	private GsonBuilder builder;
	private Gson gson;
	JSONBuilder(){
		builder = new GsonBuilder().setPrettyPrinting();
		gson= builder.create();
	}
	public String objectToJSON(Object object) {
		return gson.toJson(object);
	}
	public Gson getGsonObject() {
		return gson;
	}
}
