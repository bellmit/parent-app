package com.yesido.zookeeper;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yesido.zookeeper.balance.BalanceService;
import com.yesido.zookeeper.model.ServerData;
import com.yesido.zookeeper.service.ZkService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@ActiveProfiles("test")
public class TestBalance {

    protected final static ConcurrentLinkedQueue<ServerData> serverQueue = new ConcurrentLinkedQueue<ServerData>();
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private ZkService zkService;

    @Test
    public void test() throws Exception {
        for (int i = 1; i <= 5; i++) {
            ServerData server = new ServerData();
            server.setHost(i + "");
            server.setBalance_weight(i);
            balanceService.register("/services_balance", server);
        }
        balanceService.start();

        for (int i = 0; i < 50; i++) {
            new Thread(new BalanceTask(balanceService, "thread-" + i)).start();
        }

        // deleteNode();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
        }
    }

    public void deleteNode() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        ServerData server = new ServerData();
        server.setHost("2");
        String nodePath = "/services_balance/nodes-" + server.getHost() + "-";
        zkService.deleteNode(nodePath);
    }
}


class BalanceTask implements Runnable {

    private BalanceService balanceService;
    private String name;

    public BalanceTask(BalanceService balanceService, String name) {
        this.balanceService = balanceService;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            ServerData server = balanceService.nextServer();
            System.out.println(name + "--获取next节点：" + JSONObject.toJSONString(server));
            try {
                Thread.sleep(400 + new Random().nextInt(100));
            } catch (InterruptedException e) {
            }
        }
    }

}

