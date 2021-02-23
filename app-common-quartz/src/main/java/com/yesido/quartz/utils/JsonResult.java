package com.yesido.quartz.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

@Component
public class JsonResult {

    private static Logger log = LoggerFactory.getLogger(JsonResult.class);

    public void success(HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("status", HttpStatus.SUCCESS.value());
        result.put("msg", "ok");
        writeToString(result, response);
    }

    public void data(Object data, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("status", HttpStatus.SUCCESS.value());
        result.put("data", data);
        writeToString(result, response);
    }

    public void fail(HttpServletResponse response, Object msg) {
        JSONObject result = new JSONObject();
        result.put("status", HttpStatus.FAIL.value());
        result.put("error", msg);
        writeToString(result, response);
    }

    public void fail(HttpStatus httpStatus, Object msg, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("status", httpStatus.value());
        result.put("error", msg);
        writeToString(result, response);
    }

    public void validator(String validator, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("status", HttpStatus.VALIDATOR_ERROR.value());
        result.put("error", validator);
        writeToString(result, response);
    }

    public void writeToString(JSONObject result, HttpServletResponse response) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            String str = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty);
            System.out.println(str);
            gzip.write(str.getBytes("utf-8"));
            gzip.finish();
            response.setHeader("Content-Encoding", "GZIP");
            response.setContentType("application/json; charset=UTF-8");
            response.getOutputStream().write(bos.toByteArray());
        } catch (Exception e) {
            log.error("JsonResult compress error", e);
        }
    }

}
