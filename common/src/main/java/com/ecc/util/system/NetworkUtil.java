package com.ecc.util.system;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public final class NetworkUtil {
    public static String getLocalAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) interfaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            try {
                throw unknownHostException;
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
