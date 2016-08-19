package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

public class Game implements Serializable{
	int id;
	int favor_count;
	String des;
	String intro;
	String version;
	String name;
	String icon;
	String cover;
	int download_count;
	float android_file_size;
	String url_android;
	List<Image> imgs;
	private int has_favor ;
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDownload_count() {
		return download_count;
	}

	public void setDownload_count(int download_count) {
		this.download_count = download_count;
	}

	public float getAndroid_file_size() {
		return android_file_size;
	}

	public void setAndroid_file_size(float android_file_size) {
		this.android_file_size = android_file_size;
	}

	public String getUrl_android() {
		return url_android;
	}

	public void setUrl_android(String url_android) {
		this.url_android = url_android;
	}

	public List<Image> getImgs() {
		return imgs;
	}

	public void setImgs(List<Image> imgs) {
		this.imgs = imgs;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getFavor_count() {
		return favor_count;
	}

	public void setFavor_count(int favor_count) {
		this.favor_count = favor_count;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHas_favor() {
		return has_favor;
	}

	public void setHas_favor(int has_favor) {
		this.has_favor = has_favor;
	}

}
