package com.miqtech.master.client.entity;

import java.io.Serializable;

public class UserGame implements Serializable {
	String game_server;
	String game_nickname;
	String game_name;
	String game_level;
	int id;
	String game_pic;
	int game_id;

	public String getGame_server() {
		return game_server;
	}

	public void setGame_server(String game_server) {
		this.game_server = game_server;
	}

	public String getGame_nickname() {
		return game_nickname;
	}

	public void setGame_nickname(String game_nickname) {
		this.game_nickname = game_nickname;
	}

	public String getGame_name() {
		return game_name;
	}

	public void setGame_name(String game_name) {
		this.game_name = game_name;
	}

	public String getGame_level() {
		return game_level;
	}

	public void setGame_level(String game_level) {
		this.game_level = game_level;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGame_pic() {
		return game_pic;
	}

	public void setGame_pic(String game_pic) {
		this.game_pic = game_pic;
	}

	public int getGame_id() {
		return game_id;
	}

	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}
}
