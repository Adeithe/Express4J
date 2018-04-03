package com.express4j.service.obj;

public enum Charset {
	UTF_8("UTF-8"),
	NONE(null);
	
	private String charset;
	
	Charset(String charset) {
		this.charset = charset;
	}
	
	public String toString() {
		return this.charset;
	}
}
