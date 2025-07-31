package com.mycompany.app.Messages;


public interface IMessageEndDec {
	void encodeMessage(Message m);
	Message decodeMessage(byte[] m);
}
