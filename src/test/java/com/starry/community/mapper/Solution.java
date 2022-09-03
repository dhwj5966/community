package com.starry.community.mapper;

import java.util.*;

/**
 * @author Starry
 * @create 2022-09-01-9:31 PM
 * @Describe
 */
class Solution {
    public static void main(String[] args) {

        Solution solution = new Solution();
        solution.findSubstring("barfoofoobarthefoobarman",new String[]{"bar","foo","the"});
    }
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> res = new ArrayList<Integer>();//存放结果集
        int m = words.length, n = words[0].length(), ls = s.length();
        for (int i = 0; i < ls; i++) {//对于每个字母开头的可能性，都进行遍历判断
            if (i + m * n > ls) { //越界判断
                break;
            }
            Map<String, Integer> differ = new HashMap<String, Integer>();//差异集
            //往差异集里丢s字符串的字符
            for (int j = 0; j < m; j++) {
                String word = s.substring(i + j * n, i + (j + 1) * n);
                differ.put(word, differ.getOrDefault(word, 0) + 1);
            }
            //利用words消字符
            for (String word : words) {
                differ.put(word, differ.getOrDefault(word, 0) - 1);
                if (differ.get(word) == 0) {
                    differ.remove(word);
                }
            }
            if (differ.isEmpty()) {
                res.add(i);
            }
        }
        return res;
    }
}
