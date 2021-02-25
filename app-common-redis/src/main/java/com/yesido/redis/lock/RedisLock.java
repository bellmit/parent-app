package com.yesido.redis.lock;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.redis.service.RedisService;

/**
 * 分布式锁<br>
 * redisson：https://github.com/redisson/redisson/wiki/
 * 
 * 
 * @author yesido
 * @date 2019年7月30日 下午3:14:12
 */
@Service
public class RedisLock {
    Logger logger = LoggerFactory.getLogger(getClass());
    private final static int WAIT_MS = 1000; // 默认等待时间，单位毫秒

    @Autowired
    private RedisService redisService;

    /**
     * 尝试获取锁
     * 
     * @param key 锁key
     * @param expireSeconds 锁过期时间，单位秒
     * @return
     */
    public boolean tryLock(String key, int expireSeconds) {
        String val = UUID.randomUUID().toString().replaceAll("-", "");
        return tryLock(key, val, expireSeconds, WAIT_MS);
    }

    public boolean tryLock(String key, String val, int expireSeconds) {
        return tryLock(key, val, expireSeconds, WAIT_MS);
    }

    /**
     * 尝试获取锁
     * 
     * @param key 锁key
     * @param expireSeconds 锁过期时间，单位秒
     * @param waitMilliseconds 等待时间，单位毫秒
     * @return
     */
    public boolean tryLock(String key, int expireSeconds, int waitMilliseconds) {
        String val = UUID.randomUUID().toString().replaceAll("-", "");
        return tryLock(key, val, expireSeconds, waitMilliseconds);
    }

    /**
     * 尝试获取锁
     * 
     * @param key 锁key
     * @param val 锁值，可以配合unlock(String key, String val)释放锁
     * @param expireSeconds 锁过期时间，单位秒
     * @param waitMilliseconds 等待时间，单位毫秒
     * @return
     */
    public boolean tryLock(String key, String val, int expireSeconds, int waitMilliseconds) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("[RedisLock] key can not be empty!");
        }
        if (expireSeconds <= 0 || waitMilliseconds <= 0) {
            throw new RuntimeException("[RedisLock] time param illegal!");
        }
        int timeout = waitMilliseconds;
        while (timeout >= 0) {
            boolean lock = redisService.setNxEx(key, val, expireSeconds, TimeUnit.SECONDS);
            if (lock) {
                expireRenewWoker(key, val, expireSeconds);
                return true;
            }
            int nextAttempt = this.getNextAttempt();
            timeout -= nextAttempt;
            sleep(nextAttempt);
        }
        return false;
    }

    private void expireRenewWoker(String key, String val, int expireSeconds) {
        int expireMillisecond = expireSeconds * 1000;
        int delay = (int) (expireMillisecond * 0.75);
        int period = expireMillisecond / 3; // 以(过期时间1/3)的周期做续期
        Timer timer = new Timer(true); // 守护线程
        ExpireRenewWoker woker = new ExpireRenewWoker(redisService, key, val, expireSeconds);
        logger.info("[RedisLock]配置续期线程，key->{}, val->{}, 延迟delay->{}毫秒，周期period->{}毫秒", key, val, delay, period);
        timer.schedule(woker, delay, period);
    }

    /**
     * 直接释放锁
     */
    public void unlock(String key) {
        redisService.delete(key);
    }

    /**
     * 配合value时间释放锁
     */
    public void unlock(String key, String val) {
        // 非原子操作
        String value = redisService.get(key);
        if (value != null && value.equals(val)) {
            redisService.delete(key);
        }
    }

    /**
     * 获取随机数
     */
    private int getNextAttempt() {
        Random random = new Random();
        int min = 50;
        int max = 100;
        int number = random.nextInt(max - min) + min;
        return number;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * 锁续期
     * 
     * @author yesido
     * @date 2020年4月30日 上午9:26:26
     */
    private class ExpireRenewWoker extends TimerTask {
        Logger logger = LoggerFactory.getLogger(getClass());
        private RedisService redisService;
        private String key;
        private String val;
        private int expireSeconds;

        public ExpireRenewWoker(RedisService redisService, String key, String val, int expireSeconds) {
            this.redisService = redisService;
            this.key = key;
            this.val = val;
            this.expireSeconds = expireSeconds;
        }

        @Override
        public void run() {
            try {
                String cacheVal = redisService.get(key);
                if (cacheVal == null) {
                    cancel();
                    return;
                }
                if (!cacheVal.equals(val)) {
                    cancel();
                    return;
                }
                redisService.expire(key, expireSeconds); // 续期
            } catch (Exception e) {
                logger.error("[RedisLock]锁key({})续期失败：", key, e);
            }
        }
    }
}
