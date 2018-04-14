package com.express4j;

import com.express4j.exception.FileRequiredException;
import com.express4j.exception.TraversalAttackPreventionException;
import com.express4j.service.HttpRequest;
import com.express4j.service.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class RequestHandler implements HttpHandler {
	private Express4J app;
	
	public RequestHandler(Express4J app) {
		this.app = app;
	}
	
	public void handle(HttpExchange exchange) throws IOException {
		HttpRequest req = new HttpRequest(this.app, exchange);
		HttpResponse res = new HttpResponse(this.app, exchange, req);
		
		boolean found = false;
		for(String path : this.app.listeners.get(req.getMethod()).keySet()) {
			String parsed_path = Express4J.parsePath(path);
			if(req.getPath().equals(parsed_path) || req.getPath().matches(parsed_path)) {
				try {
					if(!path.equals(parsed_path))
						req.params = toParams(path, req.getPath());
					this.app.listeners.get(req.getMethod()).get(path).handle(req, res);
					found = true;
				} catch(FileNotFoundException | FileRequiredException e) {
					this.sendErrorDocument(404, req, res);
					e.printStackTrace();
				} catch(TraversalAttackPreventionException e) {
					this.sendErrorDocument(403, req, res);
					e.printStackTrace();
				} catch(Exception e) {
					this.sendErrorDocument(500, req, res);
					e.printStackTrace();
				}
				if(res.isClosed())
					return;
			}
		}
		if(!found)
			this.sendErrorDocument(404, req, res);
	}
	
	public void sendErrorDocument(int status, HttpRequest req, HttpResponse res) {
		try {
			InputStream in;
			File file = this.app.error_documents.getOrDefault(status, null);
			if(file == null) in = getClass().getResourceAsStream("/default_error_pages/" + status + ".html");
			else in = new FileInputStream(file);
			
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				while((line = br.readLine()) != null)
					sb.append(line);
			} catch(IOException ex) {
				ex.printStackTrace();
			} finally {
				if(br != null)
					try {
						br.close();
						in.close();
					} catch(IOException ex) {
						ex.printStackTrace();
					}
			}
			
			String body = sb.toString();
				body = body.replaceAll("\\{REQ.METHOD\\}", req.getMethod().toString());
				body = body.replaceAll("\\{REQ.PATH\\}", req.getPath());
				body = body.replaceAll("\\{REQ.HOST\\}", req.getHostname());
				body = body.replaceAll("\\{REQ.IP\\}", req.getIP());
			res.send(body);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<String, String> toParams(String raw, String path) { return this.toParams(raw, path, new HashMap<String, String>()); }
	private HashMap<String, String> toParams(String raw, String path, HashMap<String, String> map) {
		String[] raw_parts = raw.split("/");
		String[] path_parts = path.split("/");
		for(int i = 0; i < raw_parts.length; i++) {
			String raw_part = raw_parts[i];
			String path_part = path_parts[i];
			if(!raw_part.isEmpty())
				if(raw_part.startsWith(":"))
					map.put(raw_part.replaceFirst(":", ""), path_part);
		}
		return map;
	}
}
