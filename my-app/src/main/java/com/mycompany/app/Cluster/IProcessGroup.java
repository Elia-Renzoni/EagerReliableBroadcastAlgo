
package com.mycompany.app.Cluster;

import java.util.ArrayList;

public interface IProcessGroup {
	void addCorrectProcess(final Process p);
	boolean deleteCorrectProcess(final Process p);
	ArrayList<Process> getProcessGroup();
}

