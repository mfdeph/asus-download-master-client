package com.insolence.admclient.entity;

public class DownloadFileInfo {

	String id;
	String name;
	String size;
	
	boolean selected;
	
	public DownloadFileInfo(String id, String name, String size) {
		super();
		this.id = id;
		this.name = name;
		this.size = size;
		this.selected = true;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSize() {
		return size;
	}
}