package com.udacity.storm.restexpress;

import java.util.List;

public class Emission {
	private final String streamId;
	private final List<Object> message;
	
	public Emission(String streamId, List<Object> message) {
		this.streamId = streamId;
		this.message = message;
	}

	public String getStreamId() {
		return streamId;
	}
	
	public List<Object> getMessage() {
		return message;
	}	
}
