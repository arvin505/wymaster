package com.miqtech.master.client.entity;

public class WarGame {
	int item_id;
	String item_name;
	String item_icon;
	int server_required;

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_icon() {
		return item_icon;
	}

	public void setItem_icon(String item_icon) {
		this.item_icon = item_icon;
	}

	public int getServer_required() {
		return server_required;
	}

	public void setServer_required(int server_required) {
		this.server_required = server_required;
	}

}
