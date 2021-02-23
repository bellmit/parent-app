package com.yesido.zookeeper.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.zookeeper.model.ServerData;

/**
 * master选举
 * 
 * @author yesido
 * @date 2019年8月16日 上午11:45:04
 */
public class MasterService {
    Logger logger = LoggerFactory.getLogger(getClass());

    // master默认节点路径
    public static final String MASTER_PATH = "/master";
    // 服务器的状态
    private volatile boolean running = false;
    // 监听master节点删除事件
    private TreeCacheListener listener;

    private ScheduledExecutorService exector = Executors.newScheduledThreadPool(1);
    private TreeCache cache;
    private CuratorFramework zkClient;
    private ServerData server;
    private String masterPath;


    public MasterService(CuratorFramework zkClient, ServerData server) {
        this(zkClient, server, MASTER_PATH);
    }

    public MasterService(CuratorFramework zkClient, ServerData server, String masterPath) {
        this.zkClient = zkClient;
        this.server = server;
        this.masterPath = masterPath;
        this.listener = (client, event) -> {
            // 监听master节点
            switch (event.getType()) {
                case INITIALIZED:
                case NODE_ADDED:
                case NODE_UPDATED:
                    break;
                case NODE_REMOVED:
                    String data = new String(event.getData().getData());
                    ServerData master = JSONObject.parseObject(data, ServerData.class);
                    if (master != null && master.getSid() == server.getSid()) {
                        // 若之前master是本机，则立即竞争master
                        takeMaster();
                    } else {
                        // 之前master不是本机，延迟一点时间竞争master(防止小故障引起的竞争可能导致的网络数据风暴)
                        exector.schedule(new Runnable() {
                            @Override
                            public void run() {
                                takeMaster();
                            }
                        }, 10, TimeUnit.MILLISECONDS);
                    }
                    break;
                default:
                    logger.info("[master选举]未处理事件：{}", event.getType(), JSONObject.toJSONString(event));
                    break;
            }
        };
    }

    /**
     * 竞争master
     */
    private void takeMaster() {
        if (!running) {
            return;
        }
        try {
            byte[] data = JSONObject.toJSONString(server).getBytes();
            zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(masterPath, data);
            logger.info("[master选举]节点竞争成功：{}", server);

            // 模拟服务器挂机，让程序可以重新选举master
            /*exector.schedule(new Runnable() {
                @Override
                public void run() {
                    releaseMaster();
                }
            }, 1000, TimeUnit.MILLISECONDS);*/

        } catch (NodeExistsException e) {
            ServerData master = getMasterData();
            if (master == null) {
                // 读取主节点时，主节点被释放，重新竞争
                takeMaster();
            }
        } catch (Exception e) {
            logger.error("[master选举]竞争master异常，路径：{}", masterPath, e);
        }
    }

    /**
     * 释放master
     */
    private void releaseMaster() {
        if (isMaster()) {
            try {
                zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(masterPath);
            } catch (NoNodeException e) {
                logger.error("[master选举]释放master节点不存在，路径：{}，错误：{}", masterPath, e.toString());
            } catch (Exception e) {
                logger.error("[master选举]释放master异常，路径：{}", masterPath, e);
            }
        }
    }

    /**
     * 开始选举master
     */
    public synchronized void start() {
        if (running) {
            throw new RuntimeException("[master选举]server has startup.....");
        }
        running = true;
        cache = new TreeCache(zkClient, masterPath);
        cache.getListenable().addListener(listener);
        try {
            cache.start();
            takeMaster();
        } catch (Exception e) {
            cache.close();
            logger.error("[master选举]监听zk节点异常，路径：{}", masterPath, e);
        }
    }

    /**
     * 停止选举master
     */
    public synchronized void stop() {
        if (!running) {
            throw new RuntimeException("[master选举]server has stopped.....");
        }
        running = false;
        try {
            // 取消订阅master节点的删除事件
            releaseMaster();
            cache.getListenable().removeListener(listener);
            cache.close();
            cache = null;
        } catch (Exception e) {
            logger.error("[master选举]取消监听zk节点异常，路径：{}", masterPath, e);
        }
    }

    /**
     * 判断本机是不是master
     */
    public boolean isMaster() {
        ServerData master = getMasterData();
        if (master == null) {
            return false;
        }
        return master.getSid() != null && master.getSid().equals(server.getSid());
    }

    /**
     * 获取master数据，抛异常的话返回null
     * 
     * @return ServerData
     */
    private ServerData getMasterData() {
        try {
            byte[] bs = zkClient.getData().forPath(masterPath);
            return JSONObject.parseObject(new String(bs), ServerData.class);
        } catch (NoNodeException e) {
            logger.error("[master选举]节点不存在，路径：{}，错误：{}", masterPath, e.toString());
        } catch (Exception e) {
            logger.error("[master选举]获取节点数据异常，路径：{}", masterPath, e);
        }
        return null;
    }
}
