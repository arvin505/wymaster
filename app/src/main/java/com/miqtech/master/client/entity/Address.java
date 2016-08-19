package com.miqtech.master.client.entity;

import java.util.List;

public class Address {
	String begin_time;
	String over_time;
	int hasApply;
	String areas;
	String round;
	Boolean inApply;
	int inApplyNew;
	List<InternetBarInfo> netbars;

	
	public Boolean getInApply() {
		return inApply;
	}

	public void setInApply(Boolean inApply) {
		this.inApply = inApply;
	}

	public int getInApplyNew() {
		return inApplyNew;
	}

	public void setInApplyNew(int inApplyNew) {
		this.inApplyNew = inApplyNew;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getOver_time() {
		return over_time;
	}

	public void setOver_time(String over_time) {
		this.over_time = over_time;
	}

	public int getHasApply() {
		return hasApply;
	}

	public void setHasApply(int hasApply) {
		this.hasApply = hasApply;
	}

	

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}


	public List<InternetBarInfo> getNetbars() {
		return netbars;
	}

	public void setNetbars(List<InternetBarInfo> netbars) {
		this.netbars = netbars;
	}

}
