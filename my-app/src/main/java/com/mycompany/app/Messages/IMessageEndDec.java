package com.mycompany.app.Messages;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IMessageEndDec<E> {
	byte[] encodeMessage(E m) throws JsonProcessingException;
	Message decodeMessage(byte[] m) throws Exception;
}
