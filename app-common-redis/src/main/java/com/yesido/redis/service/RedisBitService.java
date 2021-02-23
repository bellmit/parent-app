package com.yesido.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RedisBitService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 设置位图值
     * 
     * @param key redis key
     * @param offset 偏移位置，从0开始
     * @param value true=设置1，false=设置0
     */
    public void setBit(String key, long offset, boolean value) {
        Assert.notNull(key, "key can not be null");
        if (offset < 0) {
            throw new IllegalArgumentException("offset is less than 0");
        }
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.setBit(key, offset, value);
    }

    /**
     * 获取位图布尔值
     * 
     * @param key redis key
     * @param offset 偏移位置，从0开始
     * @return true=设置1，false=设置0
     */
    public boolean getBit(String key, long offset) {
        Assert.notNull(key, "key can not be null");
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Boolean b = ops.getBit(key, offset);
        return b == null ? Boolean.FALSE : b;
    }

    /**
     * 获取位图值：0或者1
     * 
     * @param key redis key
     * @param offset 偏移位置，从0开始
     * @return
     */
    public int getBitForInt(String key, long offset) {
        return getBit(key, offset) ? 1 : 0;
    }

    /**
     * 统计字符中1的位数 <br>
     * 
     * @param key
     * @return
     */
    public long bitCount(final String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection conn) {
                return conn.bitCount(key.getBytes());
            }
        });
    }

    /**
     * 统计某闭区间字符中1的位数<br>
     * 
     * <pre>
     * 注意：是某闭区间字符（一个字符8位），而不是位数
     * 第一个字符        第二个字符       第三个字符
     * 01001100     00100000     00100000
     * bitCount(key, 0 , 0) = 3 # 第一个字符1的位数
     * bitCount(key, 0 , 1) = 4 # 第一个字符到第二个字符1的位数
     * bitCount(key, 0 , 2) = 5 # 第一个字符到第三个字符1的位数
     * bitCount(key, 1 , 1) = 1 # 第二个字符到第二字符1的位数
     * bitCount(key, 1 , 2) = 2 # 第二个字符到第三字符1的位数
     * </pre>
     * 
     * @param key
     * @param start 字符的起始位置，从0开始
     * @param end 字符的结束位置
     * @return
     */
    public long bitCount(final String key, final long start, final long end) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.bitCount(key.getBytes(), start, end);
            }
        });
    }

    /**
     * 获取位图01字符串（不足8的倍数位数的后面补0）
     * 
     * @param key
     * @return
     */
    public String getBitString(final String key) {
        byte[] bytes = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) {
                return connection.get(key.getBytes());
            }
        });
        return toBinaryString(bytes);
    }

    private String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 将byte数组装成01字符串，每个byte 8位
     * 
     * @param bytes byte数组
     * @return 返回01字符串
     */
    private String toBinaryString(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(byteToBit(b));
        }
        return sb.toString();
    }

}
