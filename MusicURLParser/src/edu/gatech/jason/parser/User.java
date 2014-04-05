package edu.gatech.jason.parser;

public class User {
	private String uid;
	private String loaction;
	private String gender;
	
	public User(String uid, String loaction, String gender) {
		super();
		this.uid = uid;
		this.loaction = loaction;
		this.gender = gender;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLoaction() {
		return loaction;
	}
	public void setLoaction(String loaction) {
		this.loaction = loaction;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
}
