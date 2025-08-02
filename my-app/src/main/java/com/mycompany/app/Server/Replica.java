package com.mycompany.app.Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread;
import java.net.Socket;

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
			InputStream connIn = conn.getInputStream();
			byte[] buffer = new byte[1024];
			
			while (true) {
				int status = connIn.read(buffer);
				if (status == -1)
					break;
			}

			Message msg  = this.encoder.decodeMessage(buffer);
			this.messageFilter(msg);
		};
	}

	private void messageFilter(final Message messageToFilter) {
		byte[] bucket = switch (messageToFilter.getEndpoint()) {
			case Endpoints.ADD_NODE.getEndpointValue() -> {
				this.addNodeToCluster(messageToFilter.getNetAddr());
				this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				yeld null;
			}
			case Endpoints.WRITE_KV.getEndpointValue() -> {
			}
			case Endpoints.GET_KV.getEndpointValue() -> {}
			case Endpoints.DELETE_KV.getEndpointValue() -> {}
		}
	}

	private void addNodeToCluster(final String addr) {
	
	}

	private void writeBcuket(final String key, final byte[] value) {
	
	}

	private byte[] retreiveValue(final String key) {
	
	}

	private void removeBucket(final String key) {
	
	}
}


enum Endpoints {
	ADD_NODE("/add"),
	WRITE_KV("/set"),
	GET_KV("/get"),
	DELETE_KV("/remove");

	private String endpointValue;

	private Endpoints(final String e) {
		this.endpointValue = e;
	}

	public String getEndpointValue() {
		return this.endpointValue;
	}	
}
