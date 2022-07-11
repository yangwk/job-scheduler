package com.github.yangwk.jobscheduler.core.impl.registry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private final String hostIp;
    private final int port;
    
    public Client(String hostIp, int port) {
        this.hostIp = hostIp;
        this.port = port;
    }
    
    public void send(byte[] b) {
        try(Socket socket = new Socket();){
            socket.setReuseAddress(true);
            socket.setKeepAlive(false);
            socket.setSoTimeout(2000);
            socket.connect(new InetSocketAddress(InetAddress.getByName(hostIp), port), 1500);
            
            try(OutputStream output = socket.getOutputStream();) {
                output.write(b);
                output.flush();
            }
        } catch (IOException e) {
            // ignore
        }
    }
    
}
