package com.skzo.logtail.jersey;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	private String author = "";
	private String message = "";

	public Message() {
	}

	public Message(String author, String message) {
		this.setAuthor(author);
		this.setMessage(message);
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}