package com.fxmind.thrift;

import static org.assertj.core.api.Assertions.assertThat;

import com.fxmind.config.FxMindProperties;
import com.fxmind.global.FXMindMQL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Exercises the real Thrift stack (socket, binary protocol, processor) on an ephemeral port. */
class ThriftRoundTripTest {

    private ThriftServerLifecycle server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void clientCanCallServerOverTheWire() throws TException {
        var handler = new FXMindMQL.Iface() {
            @Override
            public List<String> ProcessStringData(Map<String, String> params, List<String> input) {
                return List.of(params.get("func"), String.valueOf(input.size()));
            }

            @Override
            public List<Double> ProcessDoubleData(Map<String, String> params, List<String> input) {
                return List.of(1.5, -125.0);
            }

            @Override
            public long IsServerActive(Map<String, String> params) {
                return 1;
            }

            @Override
            public void PostStatusMessage(Map<String, String> params) {}
        };
        var properties = new FxMindProperties(
                null, new FxMindProperties.Thrift(true, "127.0.0.1", 0, Duration.ofSeconds(5)), null, null, null);
        server = new ThriftServerLifecycle(handler, properties);
        server.start();
        assertThat(server.isRunning()).isTrue();

        try (var transport = new TSocket("127.0.0.1", server.getPort(), 5_000)) {
            transport.open();
            var client = new FXMindMQL.Client(new TBinaryProtocol(transport));

            assertThat(client.IsServerActive(Map.of())).isEqualTo(1L);
            assertThat(client.ProcessStringData(Map.of("func", "Echo"), List.of("a", "b")))
                    .containsExactly("Echo", "2");
            assertThat(client.ProcessDoubleData(Map.of(), List.of())).containsExactly(1.5, -125.0);
        }

        server.stop();
        assertThat(server.isRunning()).isFalse();
    }
}
