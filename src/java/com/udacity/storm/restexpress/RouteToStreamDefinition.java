package com.udacity.storm.restexpress;

import java.io.Serializable;
import java.util.List;

import backtype.storm.topology.OutputFieldsDeclarer;

public class RouteToStreamDefinition<BodyType> implements Serializable {
	private static final long serialVersionUID = 3231142313439090235L;

	private final String urlPattern;
	private final Class<BodyType> bodyType;
	private final RouteToStreamDefinitionHandler<BodyType> handler;
	private final String[] methods;
	
	public RouteToStreamDefinition(String urlPattern, Class<BodyType> bodyType, RouteToStreamDefinitionHandler<BodyType> handler, String... methods) {
		if((methods == null || methods.length == 0))
			throw new IllegalArgumentException("At least one HttpMethod should be specified when instantiating RouteToStreamDefinition");
		
		this.urlPattern = urlPattern;
		this.bodyType = bodyType;
		this.handler = handler;
		this.methods = methods;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}
	
	public List<Emission> handle(BodyType body) {
		return handler.handle(body);
	}
	
	public void declareFields(OutputFieldsDeclarer declarer) {
		handler.declareFields(declarer);
	}
	
	public String[] getMethods() {
		return methods;
	}
	
	public Class<BodyType> getBodyType() {
		return bodyType;
	}
}