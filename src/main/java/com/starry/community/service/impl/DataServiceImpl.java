package com.starry.community.service.impl;

import com.starry.community.bean.User;
import com.starry.community.service.DataService;
import com.starry.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Starry
 * @create 2022-09-26-2:39 PM
 * @Describe
 */
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void setUV(String ip) {
        String UVKey = RedisKeyUtil.getUVKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(UVKey, ip);
    }

    @Override
    public void setDAU(User user) {
        if (user == null) {
            return;
        }
        String DAUKey = RedisKeyUtil.getDAUKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(DAUKey, user.getId(), true);
    }

    @Override
    public Map<String, Long> getDAU(LocalDate start, LocalDate end) {
        Map<String, Long> map = new TreeMap<>();
        LocalDate temp = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
        Long sum = 0l;
        while (!temp.isAfter(end)) {
            String key = RedisKeyUtil.getDAUKey(dateTimeFormatter.format(temp));
            Long size = (Long) redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.bitCount(key.getBytes());
                }
            });
            map.put(dateTimeFormatter2.format(temp), size);
            temp = temp.plusDays(1);
            sum += size;
        }
        map.put("sum", sum);
        return map;
    }

    @Override
    public Map<String, Long> getUV(LocalDate start, LocalDate end) {
        Map<String, Long> map = new TreeMap<>();
        LocalDate temp = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
        Long sum = 0l;
        while (!temp.isAfter(end)) {
            String key = RedisKeyUtil.getUVKey(dateTimeFormatter.format(temp));
            Long size = redisTemplate.opsForHyperLogLog().size(key);
            map.put(dateTimeFormatter2.format(temp), size);
            temp = temp.plusDays(1);
            sum += size;
        }
        map.put("sum", sum);
        return map;
    }
}
