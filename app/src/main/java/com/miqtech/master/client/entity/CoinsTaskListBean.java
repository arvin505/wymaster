package com.miqtech.master.client.entity;

import java.util.List;


public class CoinsTaskListBean {
	
	List<TaskInfo> dailyTasks;//每日任务
	
	List<TaskInfo> teachingTasks;//新手任务

	public List<TaskInfo> getDailyTasks() {
		return dailyTasks;
	}

	public void setDailyTasks(List<TaskInfo> dailyTasks) {
		this.dailyTasks = dailyTasks;
	}

	public List<TaskInfo> getTeachingTasks() {
		return teachingTasks;
	}

	public void setTeachingTasks(List<TaskInfo> teachingTasks) {
		this.teachingTasks = teachingTasks;
	}
}
