package top.dreamer.core.utils;

import top.dreamer.core.exception.HRpcBusinessException;

import java.net.*;
import java.util.Enumeration;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 21:54
 * @description: 网络相关的工具
 */
public class NetUtils {
    /**
     * 获取本机在局域网内的IP地址
     * @return 本机局域网IP地址，或者null如果获取失败
     */
    public static String getLocalIPAddress() {
        try {
            // 枚举所有网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // 忽略掉回环接口和未激活的接口
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // 枚举网络接口中的所有 IP 地址
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // 只获取IPv4地址，且忽略回环地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new HRpcBusinessException();
        }
        return null;
    }

    /**
     * 通过String解析为InetAddress
     * @param address "127.0.0.1:2181"
     * @return InetAddress
     */
    public static InetSocketAddress parseInetAddress(String address) {
        try {
            String[] parts = address.split(":");
            if (parts.length == 2) {
                String ipAddress = parts[0];  // IP地址部分
                int port = Integer.parseInt(parts[1]);  // 端口号部分
                return new InetSocketAddress(ipAddress, port);
            } else {
                throw new HRpcBusinessException("IP 地址 " + address + "不合法");
            }
        } catch (NumberFormatException e) {
            throw new HRpcBusinessException("IP 地址 " + address + "不合法");
        }
    }

    public static void main(String[] args) {
        System.out.println(
                NetUtils.parseInetAddress("192.168.1.1:2181").getHostName()
        );
    }
}
