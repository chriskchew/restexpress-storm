package com.udacity.storm.restexpress;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.utils.Utils;

public class RestExpressSpout extends BaseRichSpout {
	private static final long serialVersionUID = -1150677524672544330L;
	
	SpoutOutputCollector _collector;
	private final LinkedBlockingQueue<Emission> _queue = new LinkedBlockingQueue<Emission>();
	private final List<RouteToStreamDefinition<?>> rtsDefs;

	private RestExpressSpoutConfig environment;
	
	public RestExpressSpout(RestExpressSpoutConfig environment, List<RouteToStreamDefinition<?>> rtsDefs) {
		super();
		this.environment = environment;
		this.rtsDefs = rtsDefs;
	}
	
	@Override
	public void open(@SuppressWarnings({ "rawtypes" }) Map conf, TopologyContext context, SpoutOutputCollector collector) {
		_collector = collector;
		
		RestExpressThread thread = new RestExpressThread(
				environment.getWorkerCount(),
				environment.getExecutorThreadCount(),
				environment.getPort(),
				_queue,
				rtsDefs
		);
		thread.run();
	}

	@Override
	public void nextTuple() {
		Emission ret = _queue.poll();
        if(ret==null) {
            Utils.sleep(environment.getSpoutPollingDelayMillis());
        } else {
            _collector.emit(ret.getStreamId(), ret.getMessage());
        }
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for(RouteToStreamDefinition<?> rtsd: rtsDefs) {
			rtsd.declareFields(declarer);
		}
	}
}
