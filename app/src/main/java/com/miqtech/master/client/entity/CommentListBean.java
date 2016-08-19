package com.miqtech.master.client.entity;

import java.util.List;
/**
 * 网吧评论列表
 * @author Administrator
 *
 */
public class CommentListBean {
	List<CommentInfo> list;
	int isLast;
	int total;
	int currentPage;
	public List<CommentInfo> getList() {
		return list;
	}
	public void setList(List<CommentInfo> list) {
		this.list = list;
	}
	public int getIsLast() {
		return isLast;
	}
	public void setIsLast(int isLast) {
		this.isLast = isLast;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
}
