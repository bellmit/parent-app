package com.yesido.zookeeper.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * zookeeper 服务
 * 
 * @author yesido
 * @date 2019年8月9日 下午4:40:00
 */
@Service
public class ZkService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CuratorFramework zkClient;

    /**
     * 创建持久化节点
     * 
     * @param path 节点路径
     * @param data 节点数据
     */
    public boolean createPersistentNode(String path, String data) {
        return createNode(path, data, CreateMode.PERSISTENT);
    }

    /**
     * 创建持久化节点
     * 
     * @author yesido
     * @date 2020年12月24日 上午10:26:10
     * @param path
     * @return
     */
    public boolean createPersistentNode(String path) {
        return createNode(path, CreateMode.PERSISTENT);
    }

    /**
     * 创建持久化顺序节点
     * 
     * @param path 节点路径
     * @param data 节点数据
     */
    public boolean createPersistentWidthSeqNode(String path, String data) {
        return createNode(path, data, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    /**
     * 创建临时节点
     * 
     * @param path 节点路径
     * @param data 节点数据
     */
    public boolean createEphemeralNode(String path, String data) {
        return createNode(path, data, CreateMode.EPHEMERAL);
    }

    /**
     * 创建临时顺序节点
     * 
     * @param path 节点路径
     * @param data 节点数据
     */
    public boolean createEphemeralWidthSeqNode(String path, String data) {
        return createNode(path, data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * 创建节点
     * 
     * @param path 节点路径
     * @param data 节点数据
     * @param mode 节点类型 <br>
     * 1、PERSISTENT 持久化目录节点，存储的数据不会丢失<br>
     * 2、PERSISTENT_SEQUENTIAL顺序自动编号的持久化目录节点，存储的数据不会丢失<br>
     * 3、EPHEMERAL临时目录节点，一旦创建这个节点的客户端与服务器端口也就是session超时，这种节点会被自动删除<br>
     * 4、EPHEMERAL_SEQUENTIAL临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名。
     */
    public boolean createNode(String path, String data, CreateMode mode) {
        try {
            zkClient.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data.getBytes());
            return true;
        } catch (Exception e) {
            logger.error("创建zk节点异常，路径：{}", path, e);
        }
        return false;
    }

    /**
     * 创建节点
     * 
     * @param path 节点路径
     * @param mode 节点类型
     */
    public boolean createNode(String path, CreateMode mode) {
        try {
            zkClient.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path);
            return true;
        } catch (Exception e) {
            logger.error("创建zk节点异常，路径：{}", path, e);
        }
        return false;
    }

    /**
     * 删除节点，如果存在子节点的删除失败
     * 
     * @param path 节点路径
     * @return
     */
    public boolean deleteNodeIfNoChild(String path) {
        return deleteNode(path, false);
    }

    /**
     * 删除节点，并删除子节点
     * 
     * @param path 节点路径
     */
    public boolean deleteNode(String path) {
        return deleteNode(path, true);
    }

    /**
     * 删除节点
     * 
     * @param path 节点路径
     * @param deleteChild 是否删除子节点
     */
    public boolean deleteNode(String path, boolean deleteChild) {
        try {
            if (deleteChild) {
                zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            } else {
                zkClient.delete().guaranteed().forPath(path);
            }
            return true;
        } catch (Exception e) {
            logger.error("删除zk节点异常，路径：{}", path, e);
            return false;
        }
    }

    private String objectToString(Object data) {
        if (data instanceof String) {
            return String.valueOf(data);
        }
        return JSONObject.toJSONString(data);
    }

    /**
     * 设置节点数据
     * 
     * @param path 节点路径
     * @param data 节点数据
     */
    public void setNodeData(String path, Object data) {
        try {
            zkClient.setData().forPath(path, objectToString(data).getBytes());
        } catch (Exception e) {
            logger.error("设置zk节点数据异常，路径：{}", path, e);
        }
    }

    /**
     * 获取节点数据
     * 
     * @param path 节点路径
     * @return String
     */
    public String getNodeData(String path) {
        try {
            byte[] bs = zkClient.getData().forPath(path);
            return new String(bs);
        } catch (NoNodeException e) {
            logger.error("获取zk节点数据，节点不存在，路径：{}， exception：{}", path, e.toString());
        } catch (Exception e) {
            logger.error("获取zk节点数据异常，路径：{}", path, e);
        }
        return null;
    }

    /**
     * 获取节点数据
     * 
     * @param <T>
     * @param path 节点路径
     * @param clazz 返回对象类型
     * @return 返回T
     */
    public <T> T getNodeData(String path, Class<T> clazz) {
        String data = getNodeData(path);
        if (data != null) {
            return JSONObject.parseObject(data, clazz);
        }
        return null;
    }

    /**
     * 先同步再获取数据
     * 
     * @param path 节点路径
     * @return String
     */
    public String syncAndGetNodeData(String path) {
        zkClient.sync();
        return getNodeData(path);
    }

    /**
     * 判断节点是否存在
     * 
     * @param path 节点路径
     * @return boolean
     */
    public boolean isExistNode(String path) {
        try {
            zkClient.sync();
            return null != zkClient.checkExists().forPath(path);
        } catch (Exception e) {
            logger.error("判断是否存在zk节点异常，路径：{}", path, e);
            return false;
        }
    }

    /**
     * 获取子节点
     * 
     * @param path 节点路径
     * @return List<String>
     */
    public List<String> getChildren(String path) {
        List<String> list = new ArrayList<String>();
        try {
            list = zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            logger.error("获取zk子节点异常，路径：{}", path, e);
        }
        return list;
    }

    /**
     * 监控整棵树的节点变化
     * 
     * @param path 节点路径
     * @param listener 监听器
     */
    public void watchPath(String path, TreeCacheListener listener) {
        TreeCache cache = new TreeCache(zkClient, path);
        cache.getListenable().addListener(listener);
        try {
            cache.start();
        } catch (Exception e) {
            cache.close();
            logger.error("监听zk节点异常，路径：{}", path, e);
        }
        /*TreeCacheListener cacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case INITIALIZED:
                        logger.info("初始化事件：{}", event.getType());
                        break;
                    case NODE_ADDED:
                        logger.info("新增节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    case NODE_UPDATED:
                        logger.info("修改节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    case NODE_REMOVED:
                        logger.info("删除节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    default:
                        logger.info("未处理事件：{}", event.getType(), JSONObject.toJSONString(event));
                        break;
                }
                
            }
        };*/
    }

    /**
     * 取消监听
     * 
     * @param path 节点路径
     * @param listener 监听器
     */
    public void unWatchPath(String path, TreeCacheListener listener) {
        TreeCache cache = new TreeCache(zkClient, path);
        cache.getListenable().removeListener(listener);
        try {
            cache.close();
        } catch (Exception e) {
            logger.error("取消监听zk节点异常，路径：{}", path, e);
        }
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }
}
