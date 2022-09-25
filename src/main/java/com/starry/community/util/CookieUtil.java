package com.starry.community.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Starry
 * @create 2022-09-06-12:30 AM
 * @Describe
 */
public class CookieUtil {

    /**
     * 从request对象中找到key为name的Cookie，如果不存在则返回null
     *
     * @param request
     * @param name
     * @return key为name的cookie对象
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        if (request == null || StringUtils.isBlank(name)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
