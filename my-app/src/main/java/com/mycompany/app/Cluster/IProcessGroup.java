
package com.mycompany.app.Cluster;

import java.util.List;

public interface IProcessGroup {
	void addCorrectProcess(final ProcessEntity p);
	boolean deleteCorrectProcess(final ProcessEntity p); 
	List<Process> getProcessGroup(); 
}

