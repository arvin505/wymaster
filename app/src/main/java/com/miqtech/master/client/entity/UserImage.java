package com.miqtech.master.client.entity;

import java.io.Serializable;

public class UserImage implements Serializable{
	String img ;
	int id;
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
