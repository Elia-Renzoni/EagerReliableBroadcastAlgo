package com.mycompany.app.Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread;
import java.net.Socket;
import java.util.Optional;
import java.net.SocketAddress;

import com.mycompany.app.Broadcaster.*;
import com.mycompany.app.Cluster.*;
import com.mycompany.app.Storage.*;
import com.mycompany.app.Messages.*;

public class Replica implements IReplica {
	private final String host;
	private final int listenPort;
	private InetSocketAddress replicaIpAddr;
	private ISpreader eagerBroadcastSpreader;
	private IProcessGroup clusterManager;
	private ICache storageManager;
	private IMessageEndDec encoder;

	private ServerSocket server;

	public Replica(final String host, final int port) {
		this.host = host;
		this.listenPort = port;
		this.replicaIpAddr = new InetSocketAddress(host, port);
		this.eagerBroadcastSpreader = new Spreader();
		this.clusterManager = new ProcessGroup();
		this.storageManager = new Cache();
		this.encoder = new MessageEncDec();
	}

	@Override
	public void startListener() throws IOException {
		this.server = new ServerSocket();
		this.server.bind(this.replicaIpAddr);

		while (!this.isSocketClose()) {
			Socket conn = this.server.accept();
			Thread t = new Thread(this.applicationThreadImpl(conn));
			t.start();
		}
	}

	private boolean isSocketClose() {
		return this.server.isClosed();
	}


	private Runnable applicationThreadImpl(Socket conn) {
		return () -> {
			try { 
				InputStream connIn = conn.getInputStream();
				byte[] buffer = new byte[1024];
			
				while (true) {
					int status = connIn.read(buffer);
					if (status == -1)
						break;
				}

				Message msg  = this.encoder.decodeMessage(buffer);
				Optional<byte[]> result = this.messageFilter(msg);
				
				AckMessage am;
				byte[] encodingResult;
				if (result.isPresent()) {
					am = new AckMessage(new String(result.get()));
					encodingResult = this.encoder.encodeMessage(am);
				} else {
					am = new AckMessage("1");
					encodingResult = this.encoder.encodeMessage(am);
				
				}

				OutputStream connOut = conn.getOutputStream();
				connOut.write(encodingResult);

				conn.close();
			
			} catch (Exception e) { System.out.println(e.getMessage()); }
		};
	}

	// TODO -> adjust return value
	private Optional<byte[]> messageFilter(final Message messageToFilter) {
		String messageEndpoint = messageToFilter.getEndpoint();
		byte[] buff = switch (messageEndpoint) {
			case "/add" -> {
				this.addNodeToCluster(messageToFilter.getNetAddr());
				this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				yield null;
			}
			case "/set" -> {
				this.writeBucket(messageToFilter.getKey(), messageToFilter.getValue());
				this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				yield null;
			}
			case "/delete" -> {
				this.removeBucket(messageToFilter.getKey());
				this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				yield null;
			}
			case "/get" -> {
				var valueInBucket = this.retreiveValue(messageToFilter.getKey());
				yield valueInBucket;
			}
			default -> {
				yield null;
			}
		};
		return Optional.of(buff);
	}

	private void addNodeToCluster(final String addr) {
		final String[] splitted = addr.split(":");
		final String host = splitted[0];
		final int port = Integer.parseInt(splitted[1]);

		ProcessEntity p = new ProcessEntity(new InetSocketAddress(host, port));
		this.clusterManager.addCorrectProcess(p);
	}

	private void writeBucket(final String key, final byte[] value) {
		try {
			this.storageManager.setKV(key, value);
		} catch (Exception e) { 
			System.out.println(e.getMessage()); 
			return;
		}
	}

	private byte[] retreiveValue(final String key) {
		byte[] value = null;
		try {
			value = this.storageManager.getValueFromKey(key);
		} catch (Exception e) {
			System.out.println(e.getMessage());	
			return null;
		}
		return value;
	}

	private void removeBucket(final String key) {
		try {
			this.storageManager.removeValueFromKey(key);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return;
		}
	}
}


