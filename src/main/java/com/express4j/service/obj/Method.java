package com.express4j.service.obj;

public enum Method {
	ALL("ALL"),
	CHECKOUT("CHECKOUT"),
	COPY("COPY"),
	DELETE("DELETE"),
	GET("GET"),
	HEAD("HEAD"),
	LOCK("LOCK"),
	MERGE("MERGE"),
	MKACTIVITY("MKACTIVITY"),
	MKCOL("MKCOL"),
	MOVE("MOVE"),
	M_SEARCH("M-SEARCH"),
	NOTIFY("NOTIFY"),
	OPTIONS("OPTIONS"),
	PATCH("PATCH"),
	POST("POST"),
	PURGE("PURGE"),
	PUT("PUT"),
	REPORT("REPORT"),
	SEARCH("SEARCH"),
	SUBSCRIBE("SUBSCRIBE"),
	TRACE("TRACE"),
	UNLOCK("UNLOCK"),
	UNSUBSCRIBE("UNSUBSCRIBE");
	
	private String method;
	
	Method(String method) {
		this.method = method;
	}
	
	public String toString() {
		return this.method;
	}
}
