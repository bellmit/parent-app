package com.yesido.zookeeper.model;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 服务器信息
 * 
 * @author yesido
 * @date 2019年8月16日 上午11:46:12
 */
public class ServerData {

    private String sid; // 会话id

    private String name; // 名称

    private Integer balance; // 负载均衡计数

    private Integer balance_weight = 1; // 负载均衡权重

    private String host; // 域名或IP

    private Integer port; // udp端口

    private Integer websocket_port; // websocket端口

    private Map<String, Object> attachment; // 附加信息


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getWebsocket_port() {
        return websocket_port;
    }

    public void setWebsocket_port(Integer websocket_port) {
        this.websocket_port = websocket_port;
    }

    public ServerData addAttachment(String key, Object value) {
        if (attachment == null) {
            attachment = new HashMap<String, Object>();
        }
        attachment.put(key, value);
        return this;
    }

    public Integer getBalance_weight() {
        return balance_weight;
    }

    public void setBalance_weight(Integer balance_weight) {
        this.balance_weight = balance_weight;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public ServerData copy() {
        return JSONObject.parseObject(JSONObject.toJSONString(this), ServerData.class);
    }
}
