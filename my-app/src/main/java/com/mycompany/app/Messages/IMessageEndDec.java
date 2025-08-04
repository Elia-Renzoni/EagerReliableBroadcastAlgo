package com.mycompany.app.Messages;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IMessageEndDec {
	byte[] encodeMessage(AckMessage m) throws JsonProcessingException;
	Message decodeMessage(byte[] m) throws Exception;
}
