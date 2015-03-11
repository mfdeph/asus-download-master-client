package com.insolence.admclient.entity;

public class TorrentSearchResultItem {

	String title;
	String size;
	String seeds;
	String href;	
	
	public TorrentSearchResultItem() {
		super();
	}
	
	public TorrentSearchResultItem(String title, String size, String seeds, String href) {
		super();
		this.title = title;
		this.size = size;
		this.seeds = seeds;
		this.href = href;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getSeeds() {
		return seeds;
	}
	
	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}
	
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
}
