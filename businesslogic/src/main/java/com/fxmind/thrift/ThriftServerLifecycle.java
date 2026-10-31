package com.fxmind.thrift;

import com.fxmind.config.FxMindProperties;
import com.fxmind.global.FXMindMQL;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Runs the MetaTrader-facing Thrift server as part of the Spring lifecycle: it starts after the
 * context is fully initialised and is stopped gracefully on shutdown. Each client connection is
 * served on its own virtual thread.
 */
@Component
@ConditionalOnBooleanProperty(name = "fxmind.thrift.enabled", matchIfMissing = true)
public class ThriftServerLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(ThriftServerLifecycle.class);

    private final FXMindMQL.Iface handler;
    private final FxMindProperties.Thrift settings;

    private volatile TServer server;
    private volatile TServerSocket serverSocket;
    private volatile ExecutorService workers;
    private volatile Thread acceptor;

    public ThriftServerLifecycle(FXMindMQL.Iface handler, FxMindProperties properties) {
        this.handler = handler;
        this.settings = properties.thrift();
    }

    @Override
    public synchronized void start() {
        if (isRunning()) {
            return;
        }
        try {
            serverSocket = new TServerSocket(new TServerSocket.ServerSocketTransportArgs()
                    .bindAddr(new InetSocketAddress(settings.host(), settings.port()))
                    .clientTimeout((int) settings.clientTimeout().toMillis()));
        } catch (TTransportException e) {
            throw new IllegalStateException(
                    "Cannot bind Thrift server to %s:%d".formatted(settings.host(), settings.port()), e);
        }
        workers = Executors.newVirtualThreadPerTaskExecutor();
        server = new TThreadPoolServer(new TThreadPoolServer.Args(serverSocket)
                .processor(new FXMindMQL.Processor<>(handler))
                .executorService(workers));
        acceptor = Thread.ofPlatform().name("thrift-acceptor").daemon().start(server::serve);
        log.info("FXMind MQL Thrift server listening on {}:{}", settings.host(), getPort());
    }

    @Override
    public synchronized void stop() {
        if (server == null) {
            return;
        }
        server.stop();
        workers.close();
        log.info("FXMind MQL Thrift server stopped");
        server = null;
        serverSocket = null;
        workers = null;
        acceptor = null;
    }

    @Override
    public boolean isRunning() {
        return server != null && acceptor != null && acceptor.isAlive();
    }

    /** The bound port; differs from the configured one when {@code fxmind.thrift.port=0}. */
    public int getPort() {
        var socket = serverSocket;
        return socket == null ? -1 : socket.getServerSocket().getLocalPort();
    }
}
