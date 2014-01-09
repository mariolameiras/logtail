package com.skzo.logtail.file;

import java.util.UUID;

public class FileBean {
	private String name;
	private String path;
	private long updateTime = 0;
	private String text;
	private UUID id;
	private String type = "textFileUpdate"; // Maps to jQuery.WebSockets event
											// type

	FileBean() {

	}

	public FileBean(String name, String path) {
		this.name = name;
		this.path = path;
		id = UUID.randomUUID();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsolutePath() {
		return path;
	}

	public void setAbsolutePath(String absolutePath) {
		this.path = absolutePath;
	}

	public long getLastUpdated() {
		return updateTime;
	}

	public void setLastUpdated(long lastUpdated) {
		this.updateTime = lastUpdated;
	}

	public String getId() {
		return id.toString();
	}

	public String getText() {
		return text;
	}

	public void setText(String updatedText) {
		this.text = updatedText;
	}
}
