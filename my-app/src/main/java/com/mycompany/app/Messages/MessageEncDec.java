package com.mycompany.app.Messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MessageEncDec implements IMessageEndDec {
	private ObjectMapper mapper;	

	public MessageEncDec() { this.mapper = new ObjectMapper(); }

	@Override
	public byte[] encodeMessage(Message m) throws JsonProcessingException {
		return this.mapper.writeValueAsBytes(m);
	}

	@Override
	public Message decodeMessage(byte[] m) throws Exception {
		return this.mapper.readValue(m, Message.class);
	}
}
