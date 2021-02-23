package com.yesido.zookeeper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yesido.zookeeper.service.ZkService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@ActiveProfiles("test")
public class TestService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ZkService zkService;

    @Test
    public void test() {
        // zkService.createEphemeralNode("/test/app1", "1");
        zkService.watchPath("/test", (client, event) -> {
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
        });
        zkService.createPersistentNode("/test/app", "1111111");
        System.out.println(zkService.getNodeData("/test/app"));
        zkService.setNodeData("/test/app", "222222222222222");
        System.out.println(zkService.getNodeData("/test/app"));

        for (int i = 0; i < 10; i++) {
            zkService.createPersistentWidthSeqNode("/test/app", "1");
        }

        System.out.println("删除/test：" + zkService.deleteNodeIfNoChild("/test"));
        System.out.println("删除/test/app:" + zkService.deleteNodeIfNoChild("/test/app"));
        System.out.println("强删除/test：" + zkService.deleteNode("/test"));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
