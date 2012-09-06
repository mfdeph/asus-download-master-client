package com.insolence.admclient;

public class DownloadItem {

	
	private String id;
	private String name;
	private float percentage;
	private String volume;
	private String status;
	private String type;
	private String timeOnLine;
	private String upSpeed;
	private String downSpeed;
	private String seeds;
	private String addInfo;
	
	public DownloadItem(){
		
	}
	
	public DownloadItem(String id, String name, float percentage,
			String volume, String status, String type, String timeOnLine,
			String upSpeed, String downSpeed, String seeds, String addInfo) {
		super();
		this.id = id;
		this.name = name;
		this.percentage = percentage;
		this.volume = volume;
		this.status = status;
		this.type = type;
		this.timeOnLine = timeOnLine;
		this.upSpeed = upSpeed;
		this.downSpeed = downSpeed;
		this.seeds = seeds;
		this.addInfo = addInfo;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPercentage() {
		return percentage;
	}
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTimeOnLine() {
		return timeOnLine;
	}
	public void setTimeOnLine(String timeOnLine) {
		this.timeOnLine = timeOnLine;
	}
	public String getUpSpeed() {
		return upSpeed;
	}
	public void setUpSpeed(String upSpeed) {
		this.upSpeed = upSpeed;
	}
	public String getDownSpeed() {
		return downSpeed;
	}
	public void setDownSpeed(String downSpeed) {
		this.downSpeed = downSpeed;
	}
	public String getSeeds() {
		return seeds;
	}
	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}
	public String getAddInfo() {
		return addInfo;
	}
	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}
	
	
	
}
