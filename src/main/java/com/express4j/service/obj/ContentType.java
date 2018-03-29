package com.express4j.service.obj;

public enum ContentType {
	ANY("*/*"),
	UNSTRUCTURED_DATA("application/octet-stream"),
	XHTML("application/xhtml+xml"),
	XML("application/xml"),
	JSON("application/json"),
	WEBP("image/webp"),
	GIF("image/gif"),
	JPEG("image/jpeg"),
	APNG("image/apng"),
	PNG("image/png"),
	HTML("text/html"),
	JAVASCRIPT("text/javascript"),
	CSS("stylesheet/css"),
	UNSTRUCTURED_TEXT("text/plain"),
	UNKNOWN(null);
	
	private String type;
	
	ContentType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return this.type;
	}
}
