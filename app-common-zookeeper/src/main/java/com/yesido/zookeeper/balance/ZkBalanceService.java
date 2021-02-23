package com.yesido.zookeeper.balance;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.yesido.zookeeper.model.ServerData;
import com.yesido.zookeeper.service.ZkService;

/**
 * 负载均衡
 * 
 * @author yesido
 * @date 2019年8月20日 下午6:42:16
 */
@Service
public class ZkBalanceService extends AbstractBalanceService {
    private final static ConcurrentLinkedQueue<ServerData> serverQueue = new ConcurrentLinkedQueue<ServerData>();
    private static ScheduledExecutorService exector = Executors.newScheduledThreadPool(1);

    @Autowired
    private ZkService zkService;

    @Value("${zk.balance.max_queue_size:100}")
    private int queue_max;

    // 是否是提供服务均衡服务的提供者(注册方不允许start和nextServer)
    @Value("${zk.balance.server_provider:false}")
    private boolean isServerProvider;

    // 负载均衡root节点
    @Value("${zk.balance.root_path:/services_balances}")
    private String rootPath;

    @Override
    public void startBalance() {
        if (!isServerProvider) {
            throw new RuntimeException("[负载均衡]非服务提供者");
        }
        try {
            if (!zkService.isExistNode(rootPath)) {
                zkService.createPersistentNode(rootPath, "");
            }
        } catch (Exception e) {
            // ignore
        }
        exector.scheduleWithFixedDelay(() -> {
            nextBalance();
        }, 1, 1, TimeUnit.SECONDS);

        TreeCacheListener listener = (client, event) -> {
            // 监听节点
            switch (event.getType()) {
                case INITIALIZED:
                case NODE_UPDATED:
                    break;
                case NODE_ADDED:
                    logger.info("[负载均衡]发现服务节点注册：{}", new String(event.getData().getData()));
                    // serverQueue.clear();
                    break;
                case NODE_REMOVED:
                    logger.info("[负载均衡]服务节点移除：{}", new String(event.getData().getData()));
                    String data = new String(event.getData().getData());
                    ServerData removeServer = JSONObject.parseObject(data, ServerData.class);
                    removeServer(removeServer);
                    break;
                default:
                    logger.info("[负载均衡]]未处理事件：{}", event.getType(), JSONObject.toJSONString(event));
                    break;
            }
        };
        zkService.watchPath(rootPath, listener);
        logger.info("[负载均衡]服务初始化，队列上限：{}", queue_max);
    }

    // 移除节点
    private void removeServer(ServerData removeServer) {
        String host = removeServer.getHost();
        Iterator<ServerData> it = serverQueue.iterator();
        while (it.hasNext()) {
            ServerData server = it.next();
            if (host.equals(server.getHost())) {
                it.remove();
            }
        }
    }

    // 往队列预添加节点数据
    private synchronized void nextBalance() {
        if (serverQueue.size() >= queue_max) {
            return;
        }
        List<String> children = zkService.getChildren(rootPath);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (String path : children) {
            String nodePath = rootPath + "/" + path;
            ServerData server = zkService.getNodeData(nodePath, ServerData.class);
            if (server != null) {
                for (int i = 1; i <= server.getBalance_weight(); i++) {
                    ServerData _server = server.copy();
                    _server.setBalance(_server.getBalance() + i);
                    serverQueue.offer(_server);
                }
                server.setBalance(server.getBalance() + server.getBalance_weight());
                zkService.setNodeData(nodePath, server);
            }
        }
    }


    @Override
    public void register(String rootPath, ServerData server) {
        Assert.notNull(server.getHost(), "server host is enpty!");
        String nodePath = rootPath + "/nodes-" + server.getHost() + "-";
        server.setBalance(0);
        String data = JSONObject.toJSONString(server);
        zkService.createEphemeralNode(nodePath, data); // 临时节点
        logger.info("[负载均衡]注册节点：{}==>{}", nodePath, data);
    }

    @Override
    public void unRegister(String rootPath, ServerData server) {
        Assert.notNull(server.getHost(), "server host is enpty!");
        String nodePath = rootPath + "/nodes-" + server.getHost() + "-";
        String data = JSONObject.toJSONString(server);
        zkService.deleteNode(nodePath);
        logger.info("[负载均衡]取消注册节点：{}==>{}", nodePath, data);
    }

    @Override
    public ServerData nextServer(int cnt) {
        if (!isServerProvider) {
            throw new RuntimeException("[负载均衡]非服务提供者");
        }
        if (cnt > 10) {
            return null;
        }
        ServerData server = serverQueue.poll();
        if (server == null) {
            nextBalance();
            return nextServer(cnt + 1);
        }
        return server;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

}
