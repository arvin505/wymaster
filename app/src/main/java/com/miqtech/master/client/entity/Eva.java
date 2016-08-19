package com.miqtech.master.client.entity;

import java.io.Serializable;

public class Eva implements Serializable {

	// "service":3,
	// "enviroment":3,
	// "contentEva":1,
	// "totalEva":1,
	// "equipment":4,
	// "network":5,
	// "avgScore":3.8
	//
	// eva.avgScore 平均评价分数
	// eva.enviroment 环境舒适分数
	// eva.service 萌妹数量分数
	// eva.equipment 机器配置分数
	// eva.network 网络流畅分数
	// eva.totalEva 总的评价数量
	// eva.contentEva 包含内容的评论

	private float avgScore;
	private float enviroment;
	private float service;
	private float equipment;
	private float network;
	private int totalEva;
	private String contentEva;

	public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}

	public float getEnviroment() {
		return enviroment;
	}

	public void setEnviroment(float enviroment) {
		this.enviroment = enviroment;
	}

	public float getService() {
		return service;
	}

	public void setService(float service) {
		this.service = service;
	}

	public float getEquipment() {
		return equipment;
	}

	public void setEquipment(float equipment) {
		this.equipment = equipment;
	}

	public float getNetwork() {
		return network;
	}

	public void setNetwork(float network) {
		this.network = network;
	}

	public int getTotalEva() {
		return totalEva;
	}

	public void setTotalEva(int totalEva) {
		this.totalEva = totalEva;
	}

	public String getContentEva() {
		return contentEva;
	}

	public void setContentEva(String contentEva) {
		this.contentEva = contentEva;
	}

}
