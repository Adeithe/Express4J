package com.express4j.service;

import com.express4j.Express4J;
import com.express4j.exception.FileRequiredException;
import com.express4j.exception.InvalidCookieException;
import com.express4j.exception.TraversalAttackPreventionException;
import com.express4j.service.obj.Charset;
import com.express4j.service.obj.ContentType;
import com.express4j.service.obj.Cookie;
import com.express4j.service.obj.CookieSettings;
import com.express4j.utils.DateUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpResponse {
	private Express4J app;
	private HttpExchange exchange;
	private HttpRequest req;
	
	private int status;
	private Charset charset;
	private boolean closed = false;
	
	public HttpResponse(Express4J app, HttpExchange exchange, HttpRequest req) {
		this.app = app;
		this.exchange = exchange;
		this.req = req;
		
		this.status = 200;
		this.charset = Charset.UTF_8;
	}
	
	/**
	 * Sets the HTTP status for the response.
	 *
	 * @param status
	 * @return
	 */
	public HttpResponse status(int status) {
		this.status = status;
		return this;
	}
	
	/**
	 * Sets the charset for the response.
	 *
	 * @param charset
	 * @return
	 */
	public HttpResponse charset(Charset charset) {
		this.charset = charset;
		return this;
	}
	
	/**
	 * Sets the Content-Type HTTP header to the MIME type provided.
	 *
	 * @param type
	 * @return
	 */
	public HttpResponse type(ContentType type) {
		return this.type(type, this.charset);
	}
	
	/**
	 * Sets the Content-Type HTTP header to the provided MIME type and charset.
	 *
	 * @param type
	 * @param charset
	 * @return
	 */
	public HttpResponse type(ContentType type, Charset charset) {
		String content_type = type.toString();
		if(charset.toString() != null) {
			this.charset = charset;
			content_type += "; charset=" + charset.toString();
		}
		this.set("Content-Type", content_type);
		return this;
	}
	
	/**
	 * Returns the HTTP response header specified by field. (Case-Insensitive)
	 *
	 * @param header
	 * @return
	 */
	public List<String> get(String header) {
		List<String> headers = new ArrayList<String>();
		for(String key : this.exchange.getResponseHeaders().keySet())
			if(key.toLowerCase().equals(header.toLowerCase()))
				for(String h : this.exchange.getResponseHeaders().get(key))
					headers.add(h);
		return headers;
	}
	
	/**
	 * Appends new values to a header without overwriting it.
	 *
	 * @param header
	 * @param value
	 * @return
	 */
	public HttpResponse append(String header, String...value) {
		List<String> prev = this.get(header);
		for(String val : value)
			prev.add(val);
		return this.set(header, prev);
	}
	
	/**
	 * Sets the response's HTTP header field to value.
	 *
	 * @param header
	 * @param value
	 * @return
	 */
	public HttpResponse set(String header, String...value) {
		List<String> val = new ArrayList<String>();
		for(String v : value)
			val.add(v);
		return this.set(header, val);
	}
	
	/**
	 * Sets the response's HTTP header field to value.
	 *
	 * @param header
	 * @param value
	 * @return
	 */
	public HttpResponse set(String header, List<String> value) {
		List<String> new_value = new ArrayList<String>();
		if(this.exchange.getResponseHeaders().containsKey(header))
			this.exchange.getResponseHeaders().remove(header);
		for(String val : value)
			new_value.add(val);
		this.exchange.getResponseHeaders().put(header, new_value);
		return this;
	}
	
	/**
	 * Creates a basic cookie
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpResponse cookie(String name, String value) throws InvalidCookieException { return this.cookie(new Cookie(name, value)); }
	
	/**
	 * Creates a basic cookie based on the provided Cookie
	 *
	 * @param cookie
	 * @return
	 */
	public HttpResponse cookie(Cookie cookie) throws InvalidCookieException { return this.cookie(new CookieSettings(cookie)); }
	
	/**
	 * Creates a cookie based on the provided CookieSettings
	 *
	 * @param settings
	 * @return
	 */
	public HttpResponse cookie(CookieSettings settings) throws InvalidCookieException {
		if(settings.getCookie() != null) {
			Cookie cookie = settings.getCookie();
			String tags = "";
			if(settings.getExpiration() != null)
				tags += "Expires=" + DateUtils.toGMTString(settings.getExpiration()) + "; ";
			if(settings.getDomain() != null && !settings.getDomain().isEmpty())
				tags += "Domain=" + settings.getDomain() + "; ";
			if(settings.getPath() != null && !settings.getPath().isEmpty())
				tags += "Path=" + settings.getPath() + "; ";
			else tags += "Path=/; ";
			if(settings.isSameSite()) tags += "SameSite; ";
			if(settings.isHttpOnly()) tags += "HttpOnly; ";
			if(settings.isSecure()) tags += "Secure; ";
			return this.append("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; " + tags);
		} else
			throw new InvalidCookieException("You must set a cookie name and value.");
	}
	
	/**
	 * Removes a cookie based on its name.
	 *
	 * @param name
	 * @return
	 */
	public HttpResponse clearCookie(String name) throws InvalidCookieException {
		CookieSettings settings = new CookieSettings(new Cookie(name, ""));
			settings.setExpiration(new Date(1));
		return this.cookie(settings);
	}
	
	/**
	 * Sets the response Location HTTP header to the specified path parameter.
	 *
	 * @param path
	 * @return
	 */
	public HttpResponse location(String path) {
		this.set("Location", path);
		return this;
	}
	
	/**
	 * Redirects to the URL derived from the specified path, with status 302.
	 *
	 * @param path
	 * @throws IOException
	 */
	public void redirect(String path) throws IOException { this.redirect(302, path); }
	/**
	 * Redirects to the URL derived from the specified path, with specified status.
	 *
	 * @param status
	 * @param path
	 * @throws IOException
	 */
	public void redirect(int status, String path) throws IOException {
		this.status(status).location(path).end();
	}
	
	/**
	 * Sends an empty HTTP response.
	 *
	 * @throws IOException
	 */
	public void send() throws IOException { this.send(""); }
	/**
	 * Sends the HTTP response.
	 *
	 * @param body
	 * @throws IOException
	 */
	public void send(String body) throws IOException {
		this.exchange.sendResponseHeaders(this.status, body.getBytes().length);
		OutputStream os = this.exchange.getResponseBody();
		os.write(body.getBytes());
		os.close();
		this.closed = true;
	}
	
	/**
	 * Transfers the file at the given path.
	 *
	 * @param filePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TraversalAttackPreventionException
	 * @throws FileRequiredException
	 */
	public void sendFile(String filePath) throws FileNotFoundException, IOException, TraversalAttackPreventionException, FileRequiredException {
		if(!filePath.startsWith(this.app.getRoot()))
			filePath = this.app.getRoot() + filePath;
		File file = new File(filePath);
		this.sendFile(file);
	}
	
	/**
	 * Transfers the given file.
	 *
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TraversalAttackPreventionException
	 * @throws FileRequiredException
	 */
	public void sendFile(File file) throws FileNotFoundException, IOException, TraversalAttackPreventionException, FileRequiredException {
		if(!file.getPath().startsWith(this.app.getRoot()))
			throw new TraversalAttackPreventionException("Files sent to the client must be within the Express4J root.");
		if(!file.isFile())
			throw new FileRequiredException("You can not send a directory to the client.");
		
		this.exchange.sendResponseHeaders(200, 0);
		OutputStream os = this.exchange.getResponseBody();
		FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while((count = fs.read(buffer)) >= 0)
			os.write(buffer, 0, count);
		fs.close();
		os.close();
		this.closed = true;
	}
	
	/**
	 * Sends a JSON Object from Google Gson.
	 *
	 * @param obj
	 * @throws IOException
	 */
	public void sendJson(Object obj) throws IOException {
		this.type(ContentType.JSON, this.charset).send(Express4J.GSON.toJson(obj));
	}
	
	/**
	 * Sets the response HTTP status code to statusCode and send its string representation as the response body.
	 *
	 * @param status
	 * @throws IOException
	 */
	public void sendStatus(int status) throws IOException {
		this.status(status).end();
	}
	
	/**
	 * Ends the response process.
	 * Use to quickly end the response without any data. If you need to respond with data, instead use methods such as <code>HttpResponse.send()</code>
	 *
	 * @throws IOException
	 */
	public void end() throws IOException {
		this.send();
	}
	
	/**
	 * Returns <code>true</code> if no response has been sent yet, <code>false</code> otherwise.
	 *
	 * @return
	 */
	public boolean isOpen() { return !this.closed; }
	/**
	 * Returns <code>true</code> if a response has been sent, <code>false</code> otherwise.
	 *
	 * @return
	 */
	public boolean isClosed() { return this.closed; }
}
