package com.yesido.auth;

/**
 * 校验抽象类
 * 
 * @author yesido
 * @date 2019年8月14日 下午6:18:20
 */
public abstract class AbstractApiAuth implements ApiAuth {
    protected final Long SESSION_EXPIRE = 7 * 86400L;
    private final String SESSION_SALT = "salt@yesido.com";
    protected final String ALGORITHM_SECRET = "secret@yesido";

    protected String sessionid(String uid, long ets) {
        StringBuffer sb = new StringBuffer().append(uid).append(ets).append(SESSION_SALT);
        try {
            String sid = MD5.encoded(sb.toString());
            return sid;
        } catch (Exception e) {
            throw new RuntimeException("生成sessionid错误：" + sb.toString());
        }
    }


    @Override
    public AuthPlayload getPlayload(String token) {
        return parseToken(token);
    }

    @Override
    public String getUid(String token) {
        AuthPlayload playload = parseToken(token);
        if (playload != null) {
            return playload.getUid();
        }
        return null;
    }

    @Override
    public boolean verifyToken(String token) {
        AuthPlayload playload = parseToken(token);
        if (playload != null) {
            return checkValid(playload);
        }
        return false;
    }

    public abstract AuthPlayload parseToken(String token);

    public abstract boolean checkValid(AuthPlayload playload);
}
