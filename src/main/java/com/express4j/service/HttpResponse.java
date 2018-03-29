package com.express4j.service;

import com.express4j.Express4J;
import com.express4j.exception.FileRequiredException;
import com.express4j.exception.TraversalAttackPreventionException;
import com.express4j.json.Json;
import com.express4j.service.obj.ContentType;
import com.express4j.service.obj.Cookie;
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
	private boolean closed = false;
	
	public HttpResponse(Express4J app, HttpExchange exchange, HttpRequest req) {
		this.app = app;
		this.exchange = exchange;
		this.req = req;
		
		this.status = 200;
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
	 * Sets the Content-Type HTTP header to the MIME type provided.
	 *
	 * @param type
	 * @return
	 */
	public HttpResponse type(ContentType type) {
		this.set("Content-Type", type.toString());
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
	
	public Cookie cookie(String name) { return this.req.cookies().getOrDefault(name, new Cookie(null, null)); }
	
	public HttpResponse cookie(String name, String value) { return this.append("Set-Cookie", name+"="+value); }
	
	public HttpResponse cookie(String name, String value, int maxAge) { return this.cookie(name, value, maxAge, null, null, false, false, false); }
	public HttpResponse cookie(String name, String value, int maxAge, String domain) { return this.cookie(name, value, maxAge, domain, null, false, false, false); }
	public HttpResponse cookie(String name, String value, int maxAge, String domain, String path) { return this.cookie(name, value, maxAge, domain, path, false, false, false); }
	public HttpResponse cookie(String name, String value, int maxAge, String domain, String path, boolean sameSite) { return this.cookie(name, value, maxAge, domain, path, sameSite, false, false); }
	public HttpResponse cookie(String name, String value, int maxAge, String domain, String path, boolean sameSite, boolean httpOnly) { return this.cookie(name, value, maxAge, domain, path, sameSite, httpOnly, false); }
	public HttpResponse cookie(String name, String value, int maxAge, String domain, String path, boolean sameSite, boolean httpOnly, boolean secure) { return this.cookie(name, value, Long.valueOf(maxAge), domain, path, sameSite, httpOnly, secure); }
	
	public HttpResponse cookie(String name, String value, long maxAge) { return this.cookie(name, value, maxAge, null, null, false, false, false); }
	public HttpResponse cookie(String name, String value, long maxAge, String domain) { return this.cookie(name, value, maxAge, domain, null, false, false, false); }
	public HttpResponse cookie(String name, String value, long maxAge, String domain, String path) { return this.cookie(name, value, maxAge, domain, path, false, false, false); }
	public HttpResponse cookie(String name, String value, long maxAge, String domain, String path, boolean sameSite) { return this.cookie(name, value, maxAge, domain, path, sameSite, false, false); }
	public HttpResponse cookie(String name, String value, long maxAge, String domain, String path, boolean sameSite, boolean httpOnly) { return this.cookie(name, value, maxAge, domain, path, sameSite, httpOnly, false); }
	public HttpResponse cookie(String name, String value, long maxAge, String domain, String path, boolean sameSite, boolean httpOnly, boolean secure) {
		Date expires = new Date();
		expires.setTime(expires.getTime() + maxAge);
		return this.cookie(name, value, expires, domain, path, httpOnly, secure);
	}
	
	public HttpResponse cookie(String name, String value, Date expires) { return this.cookie(name, value, expires, null, null, false, false, false); }
	public HttpResponse cookie(String name, String value, Date expires, String domain) { return this.cookie(name, value, expires, domain, null, false, false, false); }
	public HttpResponse cookie(String name, String value, Date expires, String domain, String path) { return this.cookie(name, value, expires, domain, path, false, false, false); }
	public HttpResponse cookie(String name, String value, Date expires, String domain, String path, boolean sameSite) { return this.cookie(name, value, expires, domain, path, sameSite, false, false); }
	public HttpResponse cookie(String name, String value, Date expires, String domain, String path, boolean sameSite, boolean httpOnly) { return this.cookie(name, value, expires, domain, path, sameSite, httpOnly, false); }
	public HttpResponse cookie(String name, String value, Date expires, String domain, String path, boolean sameSite, boolean httpOnly, boolean secure) {
		String tags = "";
		if(domain != null && !domain.isEmpty()) tags += " Domain="+ domain +";";
		if(path !=null && !path.isEmpty()) tags += " Path="+ path +";"; else tags += "Path=/;";
		if(sameSite) tags += " SameSite;";
		if(httpOnly) tags += " HttpOnly;";
		if(secure) tags += " Secure;";
		return this.append("Set-Cookie", name+"="+value+"; Expires="+ DateUtils.toGMTString(expires)+"; "+ tags);
	}
	
	public HttpResponse clearCookie(String name) {
		return this.cookie(name, "", new Date(1));
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
	
	public void sendJson(Json json) throws IOException {
		this.type(ContentType.JSON).send(json.toString());
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
