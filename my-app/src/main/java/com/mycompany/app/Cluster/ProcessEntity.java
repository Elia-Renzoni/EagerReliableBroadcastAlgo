package com.mycompany.app.Cluster;

import java.net.InetSocketAddress;

public class ProcessEntity {
	private InetSocketAddress processAddr;
	private String host;
	private int listenPort;

	public ProcessEntity(final InetSocketAddress addr) {
		this.processAddr =  addr;
		this.host = this.processAddr.getHostName();
		this.listenPort = this.processAddr.getPort();
	}

	public InetSocketAddress getCompleteSocketAddress() {
		return this.processAddr;
	}

	public String getProcessHost() {
		return this.host;
	}

	public int getProcessListenPort() {
		return this.listenPort;
	}
}
