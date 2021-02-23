package com.yesido.lib.utils;

/**
 * id生成器工具类
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public class IDUtil {
    public static SnowFlakeIDGenerator generator = null;

    public static synchronized void init(long processId) {
        if (generator == null) {
            generator = new SnowFlakeIDGenerator(processId);
        }
    }

    public static String nextId() {
        if (generator == null) {
            throw new IllegalArgumentException("IDGenerator 没有实例化");
        }
        return generator.nextId();
    }

    public static Long nextLongId() {
        if (generator == null) {
            throw new IllegalArgumentException("IDGenerator 没有实例化");
        }
        return generator.nextLongId();
    }
}
