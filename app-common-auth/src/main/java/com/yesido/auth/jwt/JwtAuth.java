package com.yesido.auth.jwt;

import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yesido.auth.AbstractApiAuth;
import com.yesido.auth.AuthPlayload;

/**
 * jwt校验
 * 
 * @author yesido
 * @date 2019年8月14日 下午6:17:56
 */
public class JwtAuth extends AbstractApiAuth {

    protected Long expirestime() {
        return System.currentTimeMillis() / 1000 + SESSION_EXPIRE;
    }

    protected Algorithm getAlgorithm() {
        return Algorithm.HMAC256(ALGORITHM_SECRET + ".jwt");
    }

    @Override
    public String createToken(String uid) {
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        long ets = expirestime();
        String sid = sessionid(uid, ets);

        Algorithm algorithm = getAlgorithm();
        String token = JWT.create().withHeader(header)
                .withClaim("uid", uid).withClaim("sid", sid).withClaim("ets", ets)
                // .withExpiresAt(new Date(ets * 1000))
                .sign(algorithm);
        return token;
    }

    public AuthPlayload parseToken(String token) {
        try {
            AuthPlayload playload = new AuthPlayload();
            Algorithm algorithm = getAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            playload.setEts(jwt.getClaim("ets").asLong());
            playload.setSid(jwt.getClaim("sid").asString());
            playload.setUid(jwt.getClaim("uid").asString());
            return playload;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean checkValid(AuthPlayload playload) {
        if (playload == null) {
            return false;
        }
        long ets = playload.getEts();
        // TODO 过期验证
        String sessionid = playload.getSid();
        if (sessionid != null) {
            String sid = sessionid(playload.getUid(), ets);
            return sid.equals(sessionid);
        }
        return false;
    }

}
