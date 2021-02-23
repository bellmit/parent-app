package com.yesido.zookeeper.balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.zookeeper.model.ServerData;

/**
 * 负载均衡抽象类
 * 
 * @author yesido
 * @date 2019年8月21日 上午9:40:01
 */
public abstract class AbstractBalanceService implements BalanceService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected volatile boolean running = false;

    @Override
    public void start() {
        if (!running) {
            synchronized (getRootPath().intern()) {
                if (!running) {
                    startBalance();
                }
                running = true;
            }
        }
    }



    @Override
    public ServerData nextServer() {
        return nextServer(1);
    }

    public abstract String getRootPath();

    /**
     * 启动
     */
    public abstract void startBalance();

    /**
     * 下一个分配server
     * 
     * @param cnt 当前递归次数
     * @return ServerData
     */
    public abstract ServerData nextServer(int cnt);

}
