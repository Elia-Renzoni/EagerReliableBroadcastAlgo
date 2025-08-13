package com.mycompany.app.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AckMessage {
	private String ack;

	@JsonCreator
	public AckMessage(@JsonProperty("ack") final String ackContent) {
		this.ack = ackContent;
	}

	public String getAckMessageContent() {
		return this.ack;
	}
}
