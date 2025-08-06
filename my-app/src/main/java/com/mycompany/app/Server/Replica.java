package com.mycompany.app.Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;

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
	private static final Logger logger = Logger.getLogger(Replica.class.getName());

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

	public String getReplicaHost() {
		return this.host;
	}

	public int getReplicaPort() {
		return this.listenPort;
	}

	@Override
	public void startListener() throws IOException {
		this.server = new ServerSocket();
		this.server.bind(this.replicaIpAddr);

		Replica.logger.info("Server Listening!");

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

	private Optional<byte[]> messageFilter(final Message messageToFilter) {
		String messageEndpoint = messageToFilter.getEndpoint();
		byte[] buff = switch (messageEndpoint) {
			case "/add" -> {
				var erb = this.addNodeToCluster(messageToFilter.getNetAddr());
				if (erb) {
					Replica.logger.info("Broadcasting Cluster Node");
					this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				}
				yield null;
			}
			case "/set" -> {
				var erb = this.storageManager.isBucketEagerRealibleCompatible(messageToFilter.getKey());
				var res = this.writeBucket(messageToFilter.getKey(), messageToFilter.getValue());
				if (erb) {
					Replica.logger.info("Broadcasting New Bucket");
					this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				}
				yield res;
			}
			case "/delete" -> {
				var erb = this.storageManager.isBucketEagerRealibleCompatible(messageToFilter.getKey());
				var res = this.removeBucket(messageToFilter.getKey());
				if (!erb) {
					Replica.logger.info("Broadcasting Bucket Deletion");
					this.eagerBroadcastSpreader.eagerBroadcast(messageToFilter);
				}
				yield res;
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

	private boolean addNodeToCluster(final String addr) {
		final String[] splitted = addr.split(":");
		final String host = splitted[0];
		final int port = Integer.parseInt(splitted[1]);

		ProcessEntity p = new ProcessEntity(new InetSocketAddress(host, port));
		this.clusterManager.addCorrectProcess(p);

		Replica.logger.info("Add New Node the Cluster");

		if (this.clusterManager.isEagerBroadcastCompatible(p))
			return true;
		return false;
	}

	private byte[] writeBucket(final String key, final byte[] value) {
		try {
			this.storageManager.setKV(key, value);
		} catch (Exception e) { 
			System.out.println(e.getMessage()); 
			return e.getMessage().getBytes();
		}

		Replica.logger.info("Add New Entry to the Cache");

		return null;
	}

	private byte[] retreiveValue(final String key) {
		byte[] value = null;
		try {
			value = this.storageManager.getValueFromKey(key);
		} catch (Exception e) {
			System.out.println(e.getMessage());	
			return e.getMessage().getBytes();
		}
		return value;
	}

	private byte[] removeBucket(final String key) {
		try {
			this.storageManager.removeValueFromKey(key);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return ex.getMessage().getBytes();
		}
		Replica.logger.info("Delete Bucket from Cache");

		return null;
	}
}


