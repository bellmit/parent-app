package com.yesido.quartz.utils;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.proto.entity.quartz.ScheduleJob;

public class JobUtil {
    private static Logger logger = LoggerFactory.getLogger(JobUtil.class);

    /**
     * 通过反射调用scheduleJob中定义的方法
     * 
     * @param scheduleJob
     */
    public static void invokMethod(ScheduleJob scheduleJob) {
        boolean rs = true;
        String msg = "执行成功！";
        Object object = null;
        Class<?> clazz = null;
        // springId不为空先按springId查找bean
        /*if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
            object = SpringContextHolder.getBean(scheduleJob.getSpringId());
        }*/
        try {
            clazz = Class.forName(scheduleJob.getBeanClass());
            object = clazz.newInstance();
        } catch (Exception e) {

        }
        if (object == null) {
            rs = false;
            msg = "找不要要执行的类！！！";
        } else {
            clazz = object.getClass();
            Method method = null;
            try {
                method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
                method.invoke(object);
            } catch (NoSuchMethodException e) {
                rs = false;
                msg = "找不要要执行的方法！！！";
                logger.error(msg + ", exception: {}", e.toString());
            } catch (Exception e) {
                rs = false;
                msg = "执行任务方法出错！！！";
                logger.error(msg + ", exception: {}", e.toString());
            }
        }
        taskLog(scheduleJob, rs, msg);
    }

    /**
     * 记录任务执行日志
     * 
     * @param scheduleJob
     * @param flag
     * @param msg
     */
    private static void taskLog(ScheduleJob scheduleJob, boolean flag, String msg) {
        /*if(flag) {
        	logger.info("定时任务[{}]调用结果：{}", scheduleJob.getJobName(), msg);
        } else {
        	logger.error("定时任务[{}]调用结果：{}", scheduleJob.getJobName(), msg);
        }*/
    }

    /**
     * 获取所有定时任务业务类及其方法
     * 
     * @return
     */
    /*public static Map<String, List<String>> getClassMethods() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> list = getClassByPackagePath();
        for (String className : list) {
            try {
                List<String> methodNames = new ArrayList<String>();
                Class<?> clz = Class.forName(className);
                Method[] methods = clz.getDeclaredMethods();
                for (Method method : methods) {
                    Class<?>[] classes = method.getParameterTypes();
                    if (classes.length == 0) {
                        methodNames.add(method.getName());
                    }
                }
                if (methodNames.size() > 0) {
                    result.put(className, methodNames);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }*/

    /**
     * 获取所有定时任务业务类
     * 
     * @return
     */
    /*public static List<String> getClassByPackagePath() {
        List<String> result = new ArrayList<String>();
        TaskConfig config = SpringContextHolder.getBean(TaskConfig.class);
        String packageName = config.getExecuterJobPackage();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        String type = url.getProtocol();
        if (type.equals("file")) {
            result = getClassNameByFile(url.getPath(), true);
        }
        return result;
    }*/

    /**
     * 获取目录下的所有类名
     * 
     * @param filePath
     * @param flag 是否遍历子目录
     * @return
     */
    /*private static List<String> getClassNameByFile(String filePath,
            boolean flag) {
        List<String> result = new ArrayList<String>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (flag) {
                    result.addAll(getClassNameByFile(childFile.getPath(), flag));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(
                            childFilePath.indexOf("\\classes") + 9,
                            childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    result.add(childFilePath);
                }
            }
        }
        return result;
    }*/
}
