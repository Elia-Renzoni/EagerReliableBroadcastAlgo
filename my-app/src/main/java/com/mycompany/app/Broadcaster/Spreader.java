package com.mycompany.app.Broadcaster;

import com.mycompany.app.Cluster.IProcessGroup;
import com.mycompany.app.Cluster.ProcessGroup;
import com.mycompany.app.Messages.Message;
import com.mycompany.app.Cluster.ProcessEntity;
import com.mycompany.app.Messages.IMessageEndDec;
import com.mycompany.app.Messages.MessageEncDec;

import java.util.List;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Spreader implements ISpreader {
	private IProcessGroup cluster;
	private IMessageEndDec<Message> messageTranslator;

	public Spreader() {
		this.cluster = ProcessGroup.createProcessGroupInstance();	
		this.messageTranslator = new MessageEncDec<>();
	}	

	private class PeerConnection {
		private Socket peerConn;
		private DataOutputStream peerConnWriterObj;
		private DataInputStream peerConnReadObj;

		public void createPeerConnection(final String host, final int port) throws IOException {	
			System.out.println("PORCA MADONNA: " + host + " port: " + port);
			this.peerConn = new Socket(host, port);
			this.peerConn.setKeepAlive(true);
			this.peerConn.setSendBufferSize(2500);
			this.peerConn.setReceiveBufferSize(2500);
			this.peerConn.setSoTimeout(10000);
			this.peerConnReadObj = new DataInputStream(this.peerConn.getInputStream());
			this.peerConnWriterObj = new DataOutputStream(this.peerConn.getOutputStream());
		}


		public void sendBytes(final byte[] messageToSpread) throws IOException {
			this.peerConnWriterObj.write(messageToSpread);
			this.peerConnWriterObj.flush();
			this.peerConn.shutdownInput();
		}

		public byte[] readBytes() throws IOException {
			return this.peerConnReadObj.readAllBytes();	
		}

		public void closePeerConnection() throws IOException {
			if (this.peerConn != null && !this.peerConn.isClosed()) {
        		this.peerConn.close();
    		}
		}

	}

	@Override
	public void eagerBroadcast(final Message msg) {
		final List<ProcessEntity> clusterList = this.cluster.getProcessGroup();	
		if (clusterList.isEmpty()) {
			System.out.println("Porco DIO");
			return;
		}

		clusterList.forEach(peer -> {
			PeerConnection pc = new PeerConnection();
			
			try {
				pc.createPeerConnection(peer.getProcessHost(), peer.getProcessListenPort());
				var bytesMessage = this.messageTranslator.encodeMessage(msg);
				pc.sendBytes(bytesMessage);

				var bufferReader = pc.readBytes();

				// the result is ignored 
				this.messageTranslator.decodeMessage(bufferReader);
			} catch (Exception ioex) {
				System.err.println(ioex.getMessage());
			} finally {
				try {
					pc.closePeerConnection();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
	}
}

