package com.mycompany.app;

import com.mycompany.app.Server.IReplica;
import com.mycompany.app.Server.Replica;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
	    System.out.println("Eager Reliable Broadcast Algorithm");

	    if (args.length < 2 || args.length > 2) 
		    System.exit(1);
	    String host = args[0];
	    int listenPort = Integer.parseInt(args[1]);

	    IReplica node = new Replica(host, listenPort);
	    try {
		    node.startListener();
	    
	    } catch (Exception e) { System.out.println(e.getMessage()); System.exit(1); }

    }
}
