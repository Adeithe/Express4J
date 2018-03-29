package com.express4j.service;

import com.express4j.Express4J;
import com.express4j.service.obj.ContentType;
import com.express4j.service.obj.Cookie;
import com.express4j.service.obj.Method;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class HttpRequest {
	private Express4J app;
	private HttpExchange exchange;
	
	public HashMap<String, String> params;
	
	public HttpRequest(Express4J app, HttpExchange exchange) {
		this.app = app;
		this.exchange = exchange;
		
		this.params = new HashMap<String, String>();
	}
	
	public HashMap<String, Cookie> cookies() {
		HashMap<String, Cookie> cookies = new HashMap<String, Cookie>();
		for(String header : this.get("cookie")) {
			String[] ckies = header.split("; ");
			for(String ckie : ckies) {
				String[] c = ckie.split("=");
				cookies.put(c[0], new Cookie(c[0], c[1]));
			}
		}
		return cookies;
	}
	
	/**
	 * Returns the body that was sent to the server with the request
	 *
	 * @return
	 */
	public String getBody() { return this.getBody(null); }
	
	/**
	 * Returns the body that was sent to the server with the request
	 *
	 * @param encoding
	 * @return
	 */
	public String getBody(String encoding) {
		try {
			return IOUtils.toString(this.exchange.getRequestBody(), encoding);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a value from the Method enum
	 *
	 * @return
	 */
	public Method getMethod() {
		return Method.valueOf(this.exchange.getRequestMethod());
	}
	
	/**
	 * Returns the request protocol
	 *
	 * @return
	 */
	public String getProtocol() {
		return this.exchange.getProtocol();
	}
	
	/**
	 * Returns the IP of the client
	 *
	 * @return
	 */
	public String getIP() {
		return this.exchange.getRemoteAddress().getHostString();
	}
	
	/**
	 * Returns the hostname requested by the client
	 *
	 * @return
	 */
	public String getHostname() {
		return this.get("host").get(0).split(":")[0];
	}
	
	/**
	 * Returns the path after removing all GET parameters
	 *
	 * @return
	 */
	public String getPath() {
		return this.exchange.getRequestURI().toString().split("\\?")[0];
	}
	
	/**
	 * Returns the unmodified path, including all GET parameters
	 *
	 * @return
	 */
	public String getOriginalURL() {
		return this.exchange.getRequestURI().toString();
	}
	
	/**
	 * Returns a HashMap of the parameters assigned by the URL path
	 * Empty if none were assigned.
	 *
	 * @return
	 */
	public HashMap<String, String> getParams() {
		return this.params;
	}
	
	/**
	 * Returns the value of the provided parameter key
	 *
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		return this.getParams().get(key);
	}
	
	/**
	 * Returns a HashMap of the GET parameters from <code>HttpRequest.getOriginalURL()</code>
	 *
	 * @return
	 */
	public HashMap<String, String> getQuery() {
		HashMap<String, String> params = new HashMap<String, String>();
		String[] split = this.getOriginalURL().split("\\?");
		if(split.length > 1)
			for(String o : split[1].split("&")) {
				String[] s = o.split("=");
				if(s.length > 1)
					if(o.contains("=")) params.put(s[0], s[1]);
					else params.put(s[0], null);
			}
		return params;
	}
	
	/**
	 * Checks if the specified content types are acceptable, based on the request's Accept HTTP header field. (Case-Insensitive)
	 *
	 * @param type
	 * @return
	 */
	public boolean accepts(ContentType type) {
		for(String header : this.get("Accept")) {
			String[] types = header.split(",");
			for(String s : types) {
				s = s.split(";")[0];
				if(type.toString().equalsIgnoreCase(s))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the specified content types are acceptable, based on the request's Accept HTTP header field. (Case-Insensitive)
	 *
	 * @param type
	 * @return
	 */
	public boolean accepts(ContentType... type) {
		for(ContentType t : type)
			if(!this.accepts(t))
				return false;
		return true;
	}
	
	/**
	 * Checks if the specified encodings are acceptable, based on the request's Accept-Encoding HTTP header field. (Case-Insensitive)
	 *
	 * @param encoding
	 * @return
	 */
	public boolean acceptsEncoding(String encoding) {
		for(String s : this.get("Accept-Encoding")) {
			String[] split = s.split(", ");
			for(String o : split)
				if(o.toLowerCase().equals(encoding.toLowerCase())) return true;
		}
		return false;
	}
	
	/**
	 * Checks if the specified encodings are acceptable, based on the request's Accept-Encoding HTTP header field. (Case-Insensitive)
	 *
	 * @param encoding
	 * @return
	 */
	public boolean acceptsEncoding(String... encoding) {
		for(String e : encoding)
			if(!this.acceptsEncoding(e))
				return false;
		return true;
	}
	
	/**
	 * Checks if the specified languages are acceptable, based on the request's Accept-Language HTTP header field. (Case-Insensitive)
	 *
	 * @param lang
	 * @return
	 */
	public boolean acceptsLanguage(String lang) {
		for(String s : this.get("Accept-Language")) {
			String[] split = s.split(",");
			for(String o : split)
				if(o.toLowerCase().equals(lang.toLowerCase())) return true;
		}
		return false;
	}
	
	/**
	 * Checks if the specified languages are acceptable, based on the request's Accept-Language HTTP header field. (Case-Insensitive)
	 *
	 * @param lang
	 * @return
	 */
	public boolean acceptsLanguage(String... lang) {
		for(String l : lang)
			if(!this.acceptsLanguage(l))
				return false;
		return true;
	}
	
	/**
	 * Returns the specified HTTP request header field (Case-Insensitive)
	 *
	 * @param header
	 * @return
	 */
	public List<String> get(String header) {
		List<String> headers = new ArrayList<String>();
		for(String key : this.getHeaders().keySet())
			if(key.toLowerCase().equals(header.toLowerCase()))
				for(String h : this.getHeaders().get(key))
					headers.add(h);
		return headers;
	}
	
	/**
	 * Returns a HashMap of all headers sent to the server
	 *
	 * @return
	 */
	public HashMap<String, List<String>> getHeaders() {
		HashMap<String, List<String>> headers = new HashMap<String, List<String>>();
		for(Entry<String, List<String>> header : this.exchange.getRequestHeaders().entrySet())
			headers.put(header.getKey(), header.getValue());
		return headers;
	}
}
