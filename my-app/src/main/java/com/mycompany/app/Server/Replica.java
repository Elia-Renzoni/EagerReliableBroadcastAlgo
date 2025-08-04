package com.mycompany.app.Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread;
import java.net.Socket;
import java.util.Optional;

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
				this.messageFilter(msg);
			
			} catch (Exception e) { System.out.println(e.getMessage()); }
		};
	}

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
	
	}

	private void writeBucket(final String key, final byte[] value) {
	
	}

	private byte[] retreiveValue(final String key) {
	
		return null;
	}

	private void removeBucket(final String key) {
	
	}
}


