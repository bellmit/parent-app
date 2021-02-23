package com.yesido.auth;

/**
 * 校验接口
 * 
 * @author yesido
 * @date 2019年8月14日 下午6:18:09
 */
public interface ApiAuth {

    /**
     * 创建token
     * 
     * @param uid
     * @return
     */
    String createToken(String uid);

    /**
     * 从token提取uid
     * 
     * @param token
     * @return
     */
    String getUid(String token);

    /**
     * 从token提取AuthPlayload
     * 
     * @param token
     * @return
     */
    AuthPlayload getPlayload(String token);

    /**
     * 验证token合法性、是否过期
     * 
     * @param token
     * @return
     */
    boolean verifyToken(String token);
}
