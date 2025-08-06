package com.mycompany.app.Cluster;

/**
 * Dialer
 *
 */
public class Joiner {
	private String host;
	private int listenPort;
	
	public Joiner(final String host, final int listenPort) {
		this.host = host;
		this.listenPort = listenPort;
	}

	public void DialSeed() {
	
	}
}
