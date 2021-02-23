package com.yesido.lib.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ServletRequest 工具类
 * 
 * @author yesido
 * @date 2019年8月28日 下午3:31:50
 */
public class ServletRequestUtil {

    public final static String SESSION_TOKEN = "token";
    public final static String JWT_TOKEN = "jwtToken";

    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    public static HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return response;
    }

    public static String getHeader(String name) {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader(name);
    }

    public static String getCookie(String name) {
        HttpServletRequest request = getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void writeCookie(String name, String value) {
        getHttpServletResponse().addCookie(new Cookie(name, value));
    }

    public static String getToken(HttpServletRequest request) {
        return request.getHeader(SESSION_TOKEN);
    }

    public static String getToken() {
        return getHeader(SESSION_TOKEN);
    }

    public static String getJwfToken(HttpServletRequest request) {
        return request.getHeader(JWT_TOKEN);
    }

    public static String getJwtToken() {
        return getHeader(JWT_TOKEN);
    }

}
