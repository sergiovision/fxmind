package com.fxmind.global;

import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class FXMindMQLServer {

  private static final Logger log = Logger.getLogger(FXMindMQLServer.class);

  public static FXMindMQLHandler handler;

  public static FXMindMQL.Processor processor;

  public static TServer server;

  public static void start(short port) {
    try {

      handler = new FXMindMQLHandler();
      processor = new FXMindMQL.Processor(handler);

      simple(port, processor);

    } catch (Exception x) {
      log.error(x.toString());
    }
  }

  protected static void simple(short port, FXMindMQL.Processor processor) {
    try {
        TServerTransport serverTransport = new TServerSocket(port);

	    //server = new TSimpleServer(new Args(serverTransport).processor(processor));
        
	    // Use this for a multithreaded server
	    server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
        
	    // Non blocking server ( only java )
 	    //TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
        //server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));

        log.info("GlobalService server started listening...");
        server.serve();
    } catch (Exception e) {
        log.error(e.toString());
    }
  }
}
