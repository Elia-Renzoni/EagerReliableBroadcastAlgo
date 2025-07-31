package com.mycompany.app.Broadcaster;

import com.mycompany.app.Cluster.IProcessGroup;
import com.mycompany.app.Cluster.ProcessGroup;

public class Spreader implements ISpreader {
	private IProcessGroup cluster;

	public Spreader() {
		this.cluster = ProcessGroup.createProcessGroupInstance();	
	}	

	@Override
	public void eagerBroadcast(final Message msg) {
		final List<Process> clusterList = this.cluster.getProcessGroup();	

	}
}
