package com.wujiang.domain;

import java.util.Date;
import java.util.List;


public class Group {

	private long id;

	private String name; // ����

	private Date createTime;

	private int state; // 0�ɼ�״̬ 1���ɼ�״̬

	private List<User> user;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

}
