package com.udacity.storm.restexpress;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

public class InputController<BodyType> {
	final BlockingQueue<Emission> _queue;
	final RouteToStreamDefinition<BodyType> _rtsDef;
	final Class<BodyType> _clazz;
	
	public InputController(BlockingQueue<Emission> queue, RouteToStreamDefinition<BodyType> rtsDef, Class<BodyType> clazz) {
		_queue = queue;
		_rtsDef = rtsDef;
		_clazz = clazz;
	}
	
	public void handle(Request request, Response response) {
		BodyType input = request.getBodyAs(_clazz);
		
		List<Emission> emissions = _rtsDef.handle(input);
		
		_queue.addAll(emissions);
		response.setResponseCreated();
	}
}
