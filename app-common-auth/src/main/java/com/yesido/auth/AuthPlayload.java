package com.yesido.auth;

/**
 * 会话载体
 * 
 * @author yesido
 * @date 2019年8月14日 下午6:16:51
 */
public class AuthPlayload {

    // 用户uid
    private String uid;
    // 会话id
    private String sid;
    // 会话过期时间戳unixtime
    private Long ets;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Long getEts() {
        return ets;
    }

    public void setEts(Long ets) {
        this.ets = ets;
    }

}
