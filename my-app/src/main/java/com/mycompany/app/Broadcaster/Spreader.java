package com.mycompany.app.Broadcaster;

import com.mycompany.app.Cluster.IProcessGroup;
import com.mycompany.app.Cluster.ProcessGroup;
import com.mycompany.app.Messages.Message;
import com.mycompany.app.Cluster.Process;
import com.mycompany.app.Messages.IMessageEndDec;
import com.mycompany.app.Messages.MessageEncDec;

import java.util.List;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Spreader implements ISpreader {
	private IProcessGroup cluster;
	private IMessageEndDec messageTranslator;

	public Spreader() {
		this.cluster = ProcessGroup.createProcessGroupInstance();	
		this.messageTranslator = new MessageEncDec();
	}	

	private class PeerConnection {
		private Socket peerConn;
		private DataOutputStream peerConnWriterObj;
		private DataInputStream peerConnReadObj;

		public void createPeerConnection(final String host, final int port) throws IOException {	
			this.peerConn = new Socket(host, port);
		}


		public void sendBytes(final byte[] messageToSpread) throws IOException {
			this.peerConnWriterObj.write(messageToSpread);
		}

		public byte[] readBytes() throws IOException {
			return this.peerConnReadObj.readAllBytes();	
		}

		public void closePeerConnection() throws IOException {
			this.peerConn.close();
		}

	}

	@Override
	public void eagerBroadcast(final Message msg) {
		final List<Process> clusterList = this.cluster.getProcessGroup();	

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

