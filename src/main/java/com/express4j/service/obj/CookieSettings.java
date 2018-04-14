package com.express4j.service.obj;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class CookieSettings {
	private Cookie cookie;
	
	@Setter private Date expiration;
	@Setter private String domain;
	@Setter private String path;
	@Setter private boolean sameSite;
	@Setter private boolean httpOnly;
	@Setter private boolean secure;
	
	public CookieSettings(Cookie cookie) {
		this.cookie = cookie;
	}
}
