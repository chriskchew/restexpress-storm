package com.udacity.storm.restexpress;

import java.io.Serializable;
import java.util.List;

import backtype.storm.topology.OutputFieldsDeclarer;

public interface RouteToStreamDefinitionHandler<BodyType> extends Serializable {
	public List<Emission> handle(BodyType body);
	public void declareFields(OutputFieldsDeclarer declarer);
}