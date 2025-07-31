
package com.mycompany.app.Messages;


public class Message {
	private String endpoint;
	private String key;
	private Byte[] value;
	private String netAddr;

	public Message(final String endpoint, final String key, final Byte[] value, final String addr) {
		this.endpoint = endpoint;
		this.key = key;
		this.value = value;
		this.netAddr = addr;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public String getKey() {
		return this.key;
	}

	public Byte[] getValue() {
		return this.value;
	}

	public String getNetAddr() {
		return this.netAddr;
	}
}
