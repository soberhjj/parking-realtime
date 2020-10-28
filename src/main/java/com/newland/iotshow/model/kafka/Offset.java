package com.newland.iotshow.model.kafka;

import java.io.Serializable;

public class Offset implements Serializable{

	private static final long serialVersionUID = 1L;

	private String topic;
	
	private int partition;
	
	private long offset;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
	
}
