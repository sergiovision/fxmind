package com.fxmind.service.impl;

import com.fxmind.entity.Settings;
import com.fxmind.global.FXMindMQL;
import com.fxmind.service.AdminService;
import com.fxmind.service.GlobalClient;
import com.fxmind.utils.StringCleanUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalClientImpl implements GlobalClient {

    private static final Logger log = LoggerFactory.getLogger(GlobalClientImpl.class);

    public FXMindMQL.Client client;

    protected TTransport transport;
    protected TProtocol protocol;

    protected String host;
    protected short port;

    protected String AppServiceHost;
    protected short AppServicePort;

    @Autowired
    protected AdminService adminService;

    public GlobalClientImpl() {
        host = "";
        port = 0;
        AppServiceHost = "";
        AppServicePort = 0;
    }

    protected boolean ResolveEndpoint() {
        int resolved = 0;
        if (host.length() == 0) {
            host = adminService.getProperty(Settings.THRIFT_SERVER_HOST);
        }
        if (!StringCleanUtils.stringIsNullOrEmpty(host))
            resolved++;
        if (port <= 0 ) {
            String strPort = adminService.getProperty(Settings.THRIFT_JAVA_SERVER_PORT);
            port = Short.parseShort(strPort);
        }
        if (port > 0)
            resolved++;
        return resolved == 2;
    }

    protected boolean ResolveAppServiceEndpoint() {
        int resolved = 0;
        if (AppServiceHost.length() == 0) {
            AppServiceHost = adminService.getProperty(Settings.APPNET_SERVER_HOST);
        }
        if (!StringCleanUtils.stringIsNullOrEmpty(AppServiceHost))
            resolved++;
        if (AppServicePort <= 0 ) {
            String strPort = adminService.getProperty(Settings.APPNET_SERVER_PORT);
            AppServicePort = Short.parseShort(strPort);
        }
        if (AppServicePort > 0)
            resolved++;
        return resolved == 2;
    }

    @Override
    public FXMindMQL.Iface connectFXMindMQLClient() {
        try {
            if (transport != null && transport.isOpen())
                transport.close();

            if (!ResolveEndpoint())
                throw new TException("Error resolving host and port for GlobalService client!!!");

            //transport = new TFramedTransport(new TSocket(host, port));
            transport = new TSocket(host, port);
            protocol = new TBinaryProtocol(transport);

            client = new FXMindMQL.Client(protocol);

            transport.open();

            return client;
        }
        catch (TException x) {
            log.error(x.toString());
            return null;
        }
    }



    @Override
    public void closeFXMindMQLClient() {
        if (transport != null && transport.isOpen()) {
            transport.close();
            client = null;
        }
    }


}
