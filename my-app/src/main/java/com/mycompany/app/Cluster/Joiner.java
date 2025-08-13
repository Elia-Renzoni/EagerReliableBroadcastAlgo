package com.mycompany.app.Cluster;

import com.mycompany.app.Messages.*;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Dialer
 *
 */
public class Joiner {
	private String host; // seed node host
	private int listenPort; // seed node listen port
	private Socket sock;
	private InputStream in;
	private OutputStream out;
	private IMessageEndDec encoder;
	private Message msgToEncode;
	private static final int EOF = -1;
	
	public Joiner(final String host, final int listenPort) {
		this.host = host;
		this.listenPort = listenPort;
		this.encoder = new MessageEncDec();
	}

	public void DialSeed() {
		try {
			this.sock = new Socket(this.host, this.listenPort);
			this.in = this.sock.getInputStream();
			this.out = this.sock.getOutputStream();

			this.msgToEncode = new Message("/add-node", null, null, new InetSocketAddress(this.host, this.listenPort).toString());

			var msgToSend = this.encoder.encodeMessage(this.msgToEncode);

			this.out.write(msgToSend);

			byte[] buffer = new byte[1024];
			while (true) {
				int readed = this.in.read(buffer);
				if (readed == Joiner.EOF)
				       break;	
			}

			// ignore the ack value returned by the seed node

			this.sock.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		} finally {
			try {
				this.sock.close();
			} catch (Exception e) { return; }
		}
	}
}
