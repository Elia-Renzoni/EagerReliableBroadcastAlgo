package com.mycompany.app.Messages;

public class AckMessage {
	private String ack;

	public AckMessage(final String ackContent) {
		this.ack = ackContent;
	}

	public String getAckMessageContent() {
		return this.ack;
	}
}
