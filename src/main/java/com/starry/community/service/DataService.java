package com.starry.community.service;

import com.starry.community.bean.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-26-2:36 PM
 * @Describe 数据统计Service
 */
public interface DataService {

    /**
     * 将指定ip存入Redis，key为 uv:20220926 样式。
     * @param ip
     * @return
     */
    void setUV(String ip);

    /**
     * 统计从start到end的UV分布,最后还有sum。
     * 比如2022:09:24 - 2022:09:26,则返回map:
     * {2022:09:24 : 209, 2022:09:25 : 109, 2022:09:26 : 123, sum : 441}
     * @param start
     * @param end
     * @return
     */
    Map<String, Long> getUV(LocalDate start, LocalDate end);

    /**
     * 将指定用户的用户id，存入Redis,数据类型为BitMap，key为 dau:20220926 样式。
     * @param user
     */
    void setDAU(User user);

    /**
     * 统计从start到end的DAU分布,最后还有sum。
     * 比如2022:09:24 - 2022:09:26,则返回map:
     * {2022:09:24 : 209, 2022:09:25 : 109, 2022:09:26 : 123, sum : 441}
     * @param start
     * @param end
     * @return
     */
    Map<String, Long> getDAU(LocalDate start, LocalDate end);

}
