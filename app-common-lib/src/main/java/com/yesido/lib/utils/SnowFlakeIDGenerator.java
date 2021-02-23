package com.yesido.lib.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SnowFlake 64位id生成器 </br>
 * 首位符号位|41位时间戳 |10位进程号 |12位序列
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public class SnowFlakeIDGenerator {

    /**
     * 初始时间戳:2018-01-01 00:00:00<br>
     * 一经定义不可修改
     */
    private final static long beginTs = 1483200000000L;
    private final static long processIdBits = 10; // 进程位数
    private final static int sequenceBits = 12; // sequence位数
    private final static int sequenceMax = (1 << sequenceBits) - 1; // sequence最大值
    private final static AtomicInteger seq = new AtomicInteger(0); // 递增序号
    private long processId; // 进程id
    private long lastTs = 1514736000000L;

    public SnowFlakeIDGenerator(long processId) {
        if (processId < 1 || processId > ((1 << processIdBits) - 1)) {
            throw new RuntimeException("进程ID超出范围，最小：1，最大：" + ((1 << processIdBits) - 1));
        }
        this.processId = processId;
    }

    public String nextId() {
        return String.valueOf(nextLongId());
    }

    public synchronized Long nextLongId() {
        long ts = nextTs();

        int sequence = seq.getAndIncrement();
        if (sequence >= sequenceMax) {
            seq.set(0);
            ts = nextTs(lastTs);
        }
        lastTs = ts;
        long diffTs = ts - beginTs;
        return (diffTs << (processIdBits + sequenceBits)) | (processId << sequenceBits) | sequence;
    }

    /**
     * 获取当前时间戳
     * 
     * @return
     */
    private long currentTs() {
        return System.currentTimeMillis();
    }

    /**
     * 下一个时间戳
     * 
     * @param lastTs
     * @return
     */
    private long nextTs() {
        long ts = currentTs();
        while (ts < lastTs) {
            sleep(1);
            ts = currentTs();
        }
        return ts;
    }

    /**
     * 下一个时间戳(晚于最后时间)
     * 
     * @author yesido
     * @date 2020年12月16日 上午11:42:14
     * @param lastTs
     * @return
     */
    private long nextTs(long lastTs) {
        long ts = currentTs();
        while (ts <= lastTs) {
            sleep(1);
            ts = currentTs();
        }
        return ts;
    }

    /**
     * 休眠
     * 
     * @author yesido
     * @date 2020年12月16日 上午11:43:59
     * @param m 毫秒
     */
    private static void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
        }
    }
}
