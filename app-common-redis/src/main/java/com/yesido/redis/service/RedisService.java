package com.yesido.redis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * redis 服务
 * 
 * @author yesido
 * @date 2018年9月23日
 */
@Component
public class RedisService {
    private TimeUnit defaultUnit = TimeUnit.SECONDS;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, defaultUnit);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 过期时间，单位秒
     * 
     * @param key
     * @return -1=没有过期时间，-2=不存在key
     */
    public long ttl(String key) {
        Long ttl = redisTemplate.getExpire(key);
        if (ttl == null) {
            if (exist(key)) {
                return -1L;
            }
            return -2L;
        }
        return ttl;
    }

    /** -------------String------------- **/
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String result = get(key);
        if (result != null) {
            return JSONObject.parseObject(result, clazz);
        }
        return null;
    }

    public void set(String key, Object value) {
        Assert.notNull(value, "value can not be null");
        redisTemplate.opsForValue().set(key, toString(value));
    }

    private String toString(Object value) {
        String _value = null;
        if (value instanceof String) {
            _value = value.toString();
        } else {
            _value = JSONObject.toJSONString(value);
        }
        return _value;
    }

    // springboot 2.0
    public boolean setNxEx(String key, Object value, long timeout) {
        Assert.notNull(value, "value can not be null");
        return redisTemplate.opsForValue().setIfAbsent(key, toString(value), timeout, defaultUnit);
    }

    public boolean setNxEx(String key, Object value, long timeout, TimeUnit unit) {
        Assert.notNull(value, "value can not be null");
        return redisTemplate.opsForValue().setIfAbsent(key, toString(value), timeout, unit);
    }

    public void set(String key, Object value, long timeout) {
        Assert.notNull(value, "value can not be null");
        redisTemplate.opsForValue().set(key, toString(value), timeout, defaultUnit);
    }

    /** -------------String------------- **/


    /** -------------List------------- **/
    public List<String> lgetAll(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public long lsize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public String lget(String key, int index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public void lset(String key, int index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    public long lremove(String key, String value) {
        return redisTemplate.opsForList().remove(key, 0, value);
    }

    public long lremove(String key, long count, String value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    public long lleftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public long lrightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public String lleftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public String lrightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /** -------------List------------- **/

    /** -------------Hash------------- **/
    public String hget(String key, String hashKey) {
        Object result = redisTemplate.opsForHash().get(key, hashKey);
        return result != null ? result.toString() : null;
    }

    public <T> T hget(String key, String hashKey, Class<T> clazz) {
        String result = hget(key, hashKey);
        if (result != null) {
            return JSONObject.parseObject(result, clazz);
        }
        return null;
    }

    public boolean hexist(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public void hset(String key, String hashKey, Object value) {
        Assert.notNull(value, "value can not be null");
        redisTemplate.opsForHash().put(key, hashKey, toString(value));
    }

    public void hdel(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public void hdel(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    public Map<String, String> hgetall(String key) {
        return redisTemplate.execute((RedisCallback<Map<String, String>>) action -> {
            Map<byte[], byte[]> result = action.hGetAll(key.getBytes());
            if (CollectionUtils.isEmpty(result)) {
                return new HashMap<>(0);
            }

            Map<String, String> ans = new HashMap<>(result.size());
            for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                ans.put(new String(entry.getKey()), new String(entry.getValue()));
            }
            return ans;
        });
    }

    public <T> Map<String, T> hgetall(String key, Class<T> clazz) {
        return redisTemplate.execute((RedisCallback<Map<String, T>>) action -> {
            Map<byte[], byte[]> result = action.hGetAll(key.getBytes());
            if (CollectionUtils.isEmpty(result)) {
                return new HashMap<>(0);
            }

            Map<String, T> ans = new HashMap<>(result.size());
            for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                T ss = JSONObject.parseObject(new String(entry.getValue()), clazz);
                ans.put(new String(entry.getKey()), ss);
            }
            return ans;
        });
    }

    /** -------------Hash------------- **/

    /**
     * 发布消息
     */
    public void publish(String channel, Object message) {
        Assert.notNull(message, "message can not be null");
        redisTemplate.convertAndSend(channel, toString(message));
    }
}
