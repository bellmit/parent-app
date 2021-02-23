package com.yesido.zookeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yesido.zookeeper.model.ServerData;
import com.yesido.zookeeper.service.MasterService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@ActiveProfiles("test")
public class TestMaster {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CuratorFramework zkClient;
    //启动的服务个数
    private static final int CLIENT_QTY = 5;

    @Test
    public void test() throws Exception {
        testMaster();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    private void testMaster() throws IOException {
        List<MasterService> workServers = new ArrayList<MasterService>();
        for (int i = 0; i < CLIENT_QTY; ++i) {
            ServerData server = new ServerData();
            server.setSid(String.valueOf(i));
            server.setName("Client-" + i);
            MasterService workServer = new MasterService(zkClient, server);
            workServers.add(workServer);
            workServer.start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
        }
        System.out.println("Client-0 stop----------");
        workServers.get(0).stop();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
        }
        System.out.println("Client-0 start----------");
        workServers.get(0).start();

        System.out.println("敲回车键退出！\n");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println("Shutting down...");

        for (MasterService workServer : workServers) {
            try {
                workServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
