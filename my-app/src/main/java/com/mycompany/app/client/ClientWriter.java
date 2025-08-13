package com.mycompany.app.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientWriter {
        // the client will contact the seed node to send
	// the write message.
	public static final String SEED_ADDR = "127.0.0.1";
	public static final int SEED_PORT = 5050;

	public static void main(String ...args) {
		ObjectMapper objm = new ObjectMapper();
		objm.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		SetMessage sm = new SetMessage("/set", "/foo", "myvalue".getBytes());
		try {
			var valueToSend = objm.writeValueAsBytes(sm);
			SocketSpreader sp = new SocketSpreader(ClientWriter.SEED_ADDR, ClientWriter.SEED_PORT);
			sp.sendMessage(valueToSend);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}

class SetMessage {
	private String endpoint;
	private String key;
	private byte[] value;

	public SetMessage(final String endpoint, final String key, final byte[] value) {
		this.endpoint = endpoint;
		this.key = key;
		this.value = value;
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
}

class SocketSpreader {
	private Socket conn;
	private String seedAddr;
	private int seedListenPort;
	private InputStream connIn;
	private OutputStream connOut;

	SocketSpreader(final String host, final int port) throws Exception {
		this.seedAddr = host;
		this.seedListenPort = port;
		this.conn = new Socket(this.seedAddr, this.seedListenPort);
	}

	void sendMessage(final byte[] msg) throws Exception {
		this.connIn = this.conn.getInputStream();
		this.connOut = this.conn.getOutputStream();

		this.connOut.write(msg);
		System.out.println("Sent Data to the Seed Node");
		
		byte[] buffer = new byte[2024];
		int status = 0;
		while (true) {
			status = this.connIn.read(buffer);
			if (status == -1) 
				break;
		}

		System.out.println("Ack Message from Seed Node: " + buffer.toString());
	}
}

