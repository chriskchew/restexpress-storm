package com.udacity.storm.restexpress;

import java.io.Serializable;

public class RestExpressSpoutConfig implements Serializable {
	private static final long serialVersionUID = 473562139501912921L;

	private int workerCount;
	private int threadCount;
	private long spoutPollingDelayMillis;
	private int port;
	
	public RestExpressSpoutConfig() {}
	
	public RestExpressSpoutConfig(int workerCount, int threadCount, long spoutPollingDelayMillis, int port) {
		this.workerCount = workerCount;
		this.threadCount = threadCount;
		this.spoutPollingDelayMillis = spoutPollingDelayMillis;
		this.port = port;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public int getExecutorThreadCount() {
		return threadCount;
	}

	public long getSpoutPollingDelayMillis() {
		return spoutPollingDelayMillis;
	}

	public int getPort() {
		return port;
	}

	public void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public void setSpoutPollingDelayMillis(long spoutPollingDelayMillis) {
		this.spoutPollingDelayMillis = spoutPollingDelayMillis;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
