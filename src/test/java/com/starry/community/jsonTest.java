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
        String s = "asdaskhgfdaisfgaeihdfgasid1298736428735rgasikhd早点饭2iy早上空腹喝酒吧撒旦解放和v";
        char[] chars = s.toCharArray();
        long start = System.currentTimeMillis();
        for (long l = 0; l < 1_0000_0000l; l++) {
            for (int i = 0; i < s.length(); i++) {
                char c = chars[i];
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }




}


