package com.yesido.lib.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.alibaba.fastjson.JSONObject;

/**
 * 业务工具类
 * 
 * @author yesido
 * @date 2019年8月27日 下午4:53:11
 */
public class BizUtil {

    /**
     * 获取本机IP
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            String localIp = ip.getHostAddress();
                            return localIp;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 替换字符串占位符
     * 
     * @param tpl 字符串
     * @param args 占位参数
     * @return
     */
    public static String key(String tpl, Object... args) {
        return convertKey(tpl, args);
    }

    /**
     * 替换字符串占位符
     * 
     * @param tpl 字符串
     * @param args 占位参数
     * @return
     */
    public static String convertKey(String tpl, Object[] args) {
        for (int i = 1; i <= args.length; ++i) {
            tpl = tpl.replace("{" + i + "}", args[(i - 1)].toString());
        }
        return tpl;
    }

    /**
     * 线程sleep
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 对象转字符串
     */
    public static String string(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return value.toString();
        } else if (value instanceof Integer || value instanceof Long) {
            return String.valueOf(value);
        } else {
            return JSONObject.toJSONString(value);
        }
    }
}
