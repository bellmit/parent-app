package com.yesido.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author yesido
 * @date 2019年8月14日 下午1:58:35
 */
public class MD5 {

    private final static String[] hexDigits0 = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static MessageDigest md5 = null;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public static String encoded(String data) {
        byte[] bs = md5.digest(data.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            int n = bs[i];
            if (n < 0) {
                n = 256 + n;
            }
            int d1 = n / 16;
            int d2 = n % 16;
            sb.append(hexDigits0[d1] + hexDigits0[d2]);
        }
        return sb.toString();
    }
}
