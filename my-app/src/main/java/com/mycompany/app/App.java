package com.mycompany.app;

import com.mycompany.app.Server.IReplica;
import com.mycompany.app.Server.Replica;
import com.mycompany.app.Cluster.Joiner;
/**
 * Hello world!
 */
public class App {
    // hardcoded seed infos
    public static final String seedHost = "127.0.0.1";
    public static final int seedListenPort = 5050;

    public static void main(String[] args) {
	    System.out.println("Eager Reliable Broadcast Algorithm");

	    if (args.length < 2 || args.length > 2) 
		    System.exit(1);
	    String host = args[0];
	    int listenPort = Integer.parseInt(args[1]);
	    
	    if (listenPort != seedListenPort) {
	    	Joiner contactSeed = new Joiner(App.seedHost, App.seedListenPort,
				                host, listenPort);
		// join the cluster by contacting the seed node
		contactSeed.DialSeed();
	    }

	    IReplica node = new Replica(host, listenPort);
	    try {
		    node.startListener();
	    } catch (Exception e) { System.out.println(e.getMessage()); System.exit(1); }
    }
}
