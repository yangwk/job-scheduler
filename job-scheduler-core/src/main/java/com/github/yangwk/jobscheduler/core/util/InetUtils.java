package com.github.yangwk.jobscheduler.core.util;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.lang3.tuple.Pair;

public class InetUtils {

    public static Pair<String, String> findFirstNonLoopbackHostInfo() {
        InetAddress address = findFirstNonLoopbackAddress();
        if (address != null) {
            return Pair.of(address.getHostName(), address.getHostAddress());
        }
        return Pair.of("localhost", "127.0.0.1");
    }

    private static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces(); nics
                    .hasMoreElements();) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    if (result != null) {
                        continue;
                    }

                    for (Enumeration<InetAddress> addrs = ifc
                            .getInetAddresses(); addrs.hasMoreElements();) {
                        InetAddress address = addrs.nextElement();
                        if (address instanceof Inet4Address
                                && !address.isLoopbackAddress()) {
                            result = address;
                        }
                    }
                }
            }
        }
        catch (IOException ex) {
            // ignore
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            // ignore
        }

        return null;
    }

    
}
