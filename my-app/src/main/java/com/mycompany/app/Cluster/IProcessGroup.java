
package com.mycompany.app.Cluster;

import java.util.List;

public interface IProcessGroup {
	void addCorrectProcess(final Process p);
	boolean deleteCorrectProcess(final Process p); 
	List<Process> getProcessGroup(); 
}

