package com.miqtech.master.client.entity;

import java.util.List;

public class GameInfo {
	List<Game> weeklyHot;
	List<Game> hot;
	List<Game> newest;
	
	public List<Game> getWeeklyHot() {
		return weeklyHot;
	}
	public void setWeeklyHot(List<Game> weeklyHot) {
		this.weeklyHot = weeklyHot;
	}
	public List<Game> getHot() {
		return hot;
	}
	public void setHot(List<Game> hot) {
		this.hot = hot;
	}
	public List<Game> getNewest() {
		return newest;
	}
	public void setNewest(List<Game> newest) {
		this.newest = newest;
	}
	
	
}
