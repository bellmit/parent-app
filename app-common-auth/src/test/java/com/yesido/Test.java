package com.yesido;

import com.yesido.auth.ApiAuth;
import com.yesido.auth.jwt.JwtAuth;

public class Test {
    public static void main(String[] args) {
        ApiAuth auth = new JwtAuth();
        String token = auth.createToken("wufen_0");
        System.out.println(token);

        String uid = auth.getUid(token);
        System.out.println(uid);

        boolean b = auth.verifyToken(token);
        System.out.println(b);
    }
}
