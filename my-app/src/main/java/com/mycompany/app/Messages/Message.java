
package com.mycompany.app.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	private String endpoint;
	private String key;
	private byte[] value;
	private String netAddr;

	@JsonCreator
	public Message(@JsonProperty("endpoint") final String endpoint, 
		       @JsonProperty("key") final String key, 
		       @JsonProperty("value") final byte[] value, 
		       @JsonProperty("addr") final String addr) {
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

	public byte[] getValue() {
		return this.value;
	}

	public String getNetAddr() {
		return this.netAddr;
	}
}
