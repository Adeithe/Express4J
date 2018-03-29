package com.express4j.events;

import com.express4j.service.HttpRequest;
import com.express4j.service.HttpResponse;

public interface Request {
	/**
	 * Called by the Express4J service when a path listener is fired
	 * The handler will stop firing events when you call <code>HttpResponse.send()</code> or <code>HttpResponse.end()</code>
	 *
	 * @param req
	 * @param res
	 */
	void handle(HttpRequest req, HttpResponse res) throws Exception;
}
