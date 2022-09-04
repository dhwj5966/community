package com.starry.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author Starry
 * @create 2022-09-03-8:29 PM
 * @Describe
 */
public class CommunityUtil {
    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 使用MD5算法对密码进行加密
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
}
