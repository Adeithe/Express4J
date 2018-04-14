package com.express4j.service.obj;

import lombok.Getter;

@Getter
public class Cookie {
	private String name;
	private String value;
	
	public Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public boolean hasName() {
		if(this.name != null && !this.name.isEmpty())
			return true;
		return false;
	}
	
	public boolean hasValue() {
		if(this.value != null && !this.value.isEmpty())
			return true;
		return false;
	}
	
	public boolean isEmpty() {
		if(this.hasName() && this.hasValue())
			return false;
		return true;
	}
	
	public boolean exists() { return !this.isEmpty(); }
}
