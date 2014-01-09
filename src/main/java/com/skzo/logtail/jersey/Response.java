package com.skzo.logtail.jersey;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {

	public String text;
	public String author;
	public long time;

	public Response(String author, String text) {
		this.author = author;
		this.text = text;
		this.time = new Date().getTime();
	}

	public Response() {
	}
}