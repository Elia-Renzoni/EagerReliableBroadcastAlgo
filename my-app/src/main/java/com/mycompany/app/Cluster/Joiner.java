package com.mycompany.app.Cluster;

import com.mycompany.app.Messages.*;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * Dialer
 *
 */
public class Joiner {
	private String host; // seed node host
	private int listenPort; // seed node listen port
	private String nodeHost;
	private int nodeListenPort;
	private Socket sock;
	private DataInputStream in;
	private DataOutputStream out;
	private IMessageEndDec<Message> encoder;
	private Message msgToEncode;
	
	public Joiner(final String host, final int listenPort, final String myHost,
		      final int myListenPort) {
		this.host = host;
		this.listenPort = listenPort;
		this.nodeHost = myHost;
		this.nodeListenPort = myListenPort;
		this.encoder = new MessageEncDec<>();
	}

	public void DialSeed() {
		try {
			this.sock = new Socket(this.host, this.listenPort);
			this.setConnFlags();
			this.in = new DataInputStream(this.sock.getInputStream());
			this.out = new DataOutputStream(this.sock.getOutputStream());

			var conv = String.valueOf(this.nodeListenPort);
			var joined = "" + this.nodeHost + ":" + conv;

			this.msgToEncode = new Message("/add", null, null, joined);

			var msgToSend = this.encoder.encodeMessage(this.msgToEncode);

			this.out.write(msgToSend);
			this.out.flush();
			
			this.sock.shutdownOutput();

			byte[] buffer = new byte[1024];
			this.in.read(buffer);

			// ignore the ack value returned by the seed node
			System.out.println(buffer.toString());

		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		} finally {
			try {
				this.sock.close();
			} catch (Exception e) { return; }
		}
	}

	private void setConnFlags() throws IOException {
		this.sock.setSoTimeout(10000);
		this.sock.setKeepAlive(true);
	}
}
