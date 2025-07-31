package com.mycompany.app.Messages;

import com.fasterxml.jackson.core.databind.ObjectMapper;

public class MessageEncDec implements IMessageEndDec {
	private ObjectMapper mapper;	

	public MessageEncDec() { this.mapper = new ObjectMapper(); }

	@Override
	public void encodeMessage(Message m) {
	
	}

	@Override
	public Message decodeMessage(byte[] m) {
	
	}
}
