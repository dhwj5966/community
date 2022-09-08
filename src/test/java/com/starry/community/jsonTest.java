package com.starry.community;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-07-3:57 PM
 * @Describe
 */
public class jsonTest {
    @Test
    public void test() {
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("msg","what are you fucking saying?");
        json.put("name","wzh");
        System.out.println(json.toJSONString());
    }




}


