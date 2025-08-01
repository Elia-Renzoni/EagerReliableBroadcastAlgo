package com.mycompany.app.Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

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
	public void startListener() {
	
	}
}
