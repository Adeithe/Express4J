package com.express4j;

import com.express4j.events.Request;
import com.express4j.exception.DirectoryRequiredException;
import com.express4j.service.HttpRequest;
import com.express4j.service.HttpResponse;
import com.express4j.service.obj.Method;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Express4J {
	private InetSocketAddress address;
	private HttpServer server;
	private RequestHandler handler;
	
	protected HashMap<Method, HashMap<String, Request>> listeners;
	protected HashMap<Integer, File> error_documents;
	
	@Getter private int port;
	@Getter private String root;
	
	public Express4J(String root) throws DirectoryRequiredException {
		if(!root.endsWith("/") && !root.endsWith("\\"))
			root += "/";
		root = cleanPath(root);
		File directory = new File(root);
		if(!directory.exists())
			directory.mkdirs();
		if(!directory.isDirectory())
			throw new DirectoryRequiredException("Provided root path must be a directory!");
		this.root = root;
		
		this.listeners = new HashMap<Method, HashMap<String,Request>>();
		this.error_documents = new HashMap<Integer, File>();
		
		this.handler = new RequestHandler(this);
	}
	
	/**
	 * Makes all files in a provided directory available via the web server
	 *
	 * @param path
	 * @param dir
	 * @return
	 * @throws DirectoryRequiredException
	 */
	public Express4J addStatic(String path, String dir) throws DirectoryRequiredException {
		if(!dir.startsWith(this.root))
			dir = this.root + dir;
		File directory = new File(cleanPath(dir));
		if(!directory.exists())
			directory.mkdirs();
		if(!directory.isDirectory())
			throw new DirectoryRequiredException();
		
		if(!path.endsWith("/")) path += "/";
		for(File file : this.walk(directory)) {
			String file_path = "/"+ cleanPath("\\"+ file.getPath()).replace(this.root, "").replace("\\", "/");
			this.get(file_path.replace("//", "/"), new Request() {
				@Override
				public void handle(final HttpRequest req, final HttpResponse res) throws Exception {
					res.sendFile(file.getPath());
				}
			});
		}
		return this;
	}
	
	/**
	 * Create a listener for GET requests at the given path (Also accepts Regex)
	 * Requests are handled in the order they are added
	 * You can use <code>return</code> to skip a request handler and move on to the next.
	 * </br></br>
	 * Example: "/.*" will accept ALL requests
	 *
	 * @param path
	 * @param handler
	 */
	public void get(String path, Request handler) { this.registerListener(Method.GET, path, handler); }
	
	/**
	 * Create a listener for POST requests at the given path (Also accepts Regex)
	 * Requests are handled in the order they are added
	 * You can use <code>return</code> to skip a request handler and move on to the next.
	 * </br></br>
	 * Example: "/.*" will accept ALL requests
	 *
	 * @param path
	 * @param handler
	 */
	public void post(String path, Request handler) { this.registerListener(Method.POST, path, handler); }
	
	/**
	 * Create a listener for PUT requests at the given path (Also accepts Regex)
	 * Requests are handled in the order they are added
	 * You can use <code>return</code> to skip a request handler and move on to the next.
	 * </br></br>
	 * Example: "/.*" will accept ALL requests
	 *
	 * @param path
	 * @param handler
	 */
	public void put(String path, Request handler) { this.registerListener(Method.PUT, path, handler); }
	
	/**
	 * Create a listener for DELETE requests at the given path (Also accepts Regex)
	 * Requests are handled in the order they are added
	 * You can use <code>return</code> to skip a request handler and move on to the next.
	 * </br></br>
	 * Example: "/.*" will accept ALL requests
	 *
	 * @param path
	 * @param handler
	 */
	public void delete(String path, Request handler) { this.registerListener(Method.DELETE, path, handler); }
	
	/**
	 * Create a listener for ALL requests at the given path (Also accepts Regex)
	 * Requests are handled in the order they are added
	 * You can use <code>return</code> to skip a request handler and move on to the next.
	 * </br></br>
	 * Example: "/.*" will accept ALL requests
	 *
	 * @param path
	 * @param handler
	 */
	public void all(String path, Request handler) { this.registerListener(Method.ALL, path, handler); }
	
	public void registerListener(Method method, String path, Request handler) {
		if(method == Method.ALL) {
			for(Method m : Method.values())
				if(m != Method.ALL)
					this.registerListener(m, path, handler);
		} else {
			if(!this.listeners.containsKey(method))
				this.listeners.put(method, new HashMap<String, Request>());
			if(this.listeners.get(method).containsKey(path))
				this.listeners.get(method).remove(path);
			this.listeners.get(method).put(path, handler);
		}
	}
	
	/**
	 * Starts the Express4J server on port 80
	 *
	 * @throws IOException
	 */
	public void listen() throws IOException { this.listen(80); }
	
	/**
	 * Starts the Express4J server on the provided port
	 *
	 * @param port
	 * @throws IOException
	 */
	public void listen(int port) throws IOException { this.listen(port, "0.0.0.0"); }
	
	/**
	 * Starts the Express4J server on the provided hostname and port
	 *
	 * @param port
	 * @param hostname
	 * @throws IOException
	 */
	public void listen(int port, String hostname) throws IOException { this.listen(port, hostname, 0); }
	
	/**
	 * Starts the Express4J server on the provided hostname and port with a backlog limit
	 *
	 * Backlogs are the maximum number of requests the server will queue at one time
	 * If this limit is reached, the server will refuse the request
	 *
	 * @param port
	 * @param hostname
	 * @param backlog
	 * @throws IOException
	 */
	public void listen(int port, String hostname, int backlog) throws IOException {
		this.port = port;
		this.address = new InetSocketAddress(hostname, port);
		
		this.server = HttpServer.create(this.address, backlog);
		this.server.createContext("/", this.handler);
		this.server.setExecutor(null);
		this.server.start();
	}
	
	/**
	 * Kills the server
	 */
	public void stop() { this.server.stop(0); }
	
	public static String cleanPath(String path) {
		String seperator = "\\";
		String antiSeperator = "/";
		if(File.separator.equals("/")) {
			seperator = "/";
			antiSeperator = "\\";
		}
		return path.replace(antiSeperator, seperator).replace(seperator + seperator, seperator);
	}
	
	public static String parsePath(String path) {
		if(path.contains(":")) {
			int start = path.indexOf(":");
			int end = path.substring(start).indexOf("/");
			if(end < 0)
				end = path.length();
			path = path.replace(path.substring(start, end), "[A-Za-z0-9-_.+]+");
			return Express4J.parsePath(path);
		}
		return path;
	}
	
	private List<File> walk(File dir) {
		List<File> files = new ArrayList<File>();
		for(File file : dir.listFiles()) {
			if(file.isFile())
				files.add(file);
			else if(file.isDirectory())
				for(File file2 : this.walk(file))
					files.add(file2);
		}
		return files;
	}
}
