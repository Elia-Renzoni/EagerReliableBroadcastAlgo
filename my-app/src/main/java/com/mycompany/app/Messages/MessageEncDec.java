package com.mycompany.app.Messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MessageEncDec<E> implements IMessageEndDec<E> {
	private ObjectMapper mapper;	

	public MessageEncDec() { 
		this.mapper = new ObjectMapper(); 
		this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); 
	}

	@Override
	public byte[] encodeMessage(E m) throws JsonProcessingException {
		Message mtype;
		AckMessage acktype;

		if (m instanceof AckMessage) {
			acktype = (AckMessage) m;
			return this.mapper.writeValueAsBytes(acktype);
		}
		mtype = (Message) m;
		return this.mapper.writeValueAsBytes(mtype);
	}

	@Override
	public Message decodeMessage(byte[] m) throws Exception {
		return this.mapper.readValue(m, Message.class);
	}
}
