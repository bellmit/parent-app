package com.yesido.lib.utils;

import java.util.Random;

/**
 * 数字工具类
 * 
 * @author yesido
 *
 */
public class NumberUtil {
    public static String nums = "0123456789";

    /**
     * 随机生成数，区间：[0, (num - 1))
     */
    public static int random(int num) {
        Random r = new Random();
        return r.nextInt(num);
    }

    /**
     * 随机生成数，区间：[min, (max - 1))
     */
    public static int range(int min, int max) {
        if (min > max) {
            min = min ^ max;
            max = min ^ max;
            min = min ^ max;
        }
        Random r = new Random();
        return min + r.nextInt(max - min);
    }

    /**
     * 随机取数字字符串
     * 
     * @author yesido
     * @date 2020年8月26日 下午3:05:35
     * @param num 多少位数字
     * @return
     */
    public static String getRandom(int num) {
        StringBuffer sb = new StringBuffer();
        Random rondom = new Random();
        for (int i = 0; i < num; i++) {
            sb.append(nums.charAt(rondom.nextInt(nums.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(range(10, 9));
        }
    }
}
