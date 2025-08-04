
package com.mycompany.app.Cluster;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessGroup implements IProcessGroup {
	private List<ProcessEntity> cluster;
	private Lock mutex;
	private static ProcessGroup instance = null;

	public ProcessGroup() {
		this.cluster = new ArrayList<>();
		this.mutex = new ReentrantLock();
	}

	public static ProcessGroup createProcessGroupInstance() {
		if (ProcessGroup.instance != null)
		       return ProcessGroup.instance;
		ProcessGroup.instance = new ProcessGroup();
		return ProcessGroup.instance;
	}

	@Override
	public void addCorrectProcess(final ProcessEntity p) {
		try {
			this.mutex.lock();
			this.cluster.add(p);
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			this.mutex.unlock();
		}
	}

	@Override
	public boolean deleteCorrectProcess(final ProcessEntity p) {
		boolean result = false;
		try {
			this.mutex.lock();
			result = this.cluster.remove(p);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			this.mutex.unlock();
		}

		return result;
	}


	@Override
	public List<Process> getProcessGroup() {
		List<ProcessEntity> c = null;
		try {
			this.mutex.lock();
			c = this.cluster;
		} catch (Exception e) {
			System.err.println(e.getMessage());	
		} finally {
			this.mutex.unlock();
		}
		return c;
	}
}
