package com.yesido.zookeeper.balance;

import com.yesido.zookeeper.model.ServerData;

/**
 * 负载均衡
 * 
 * @author yesido
 * @date 2019年8月21日 上午9:35:59
 */
public interface BalanceService {

    /**
     * 注册服务节点
     */
    void register(String rootPath, ServerData server);

    /**
     * 下一个分配server：注册服务节点不要调用
     */
    ServerData nextServer();

    /**
     * 开始：注册服务节点不要调用
     */
    void start();

    /**
     * 取消服务节点注册
     */
    void unRegister(String rootPath, ServerData server);
}
