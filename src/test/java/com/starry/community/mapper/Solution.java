package com.starry.community.mapper;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.TreeMap;

/**
 * @author Starry
 * @create 2022-09-01-9:31 PM
 * @Describe
 */
class Solution {
    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 3);
    public static void main(String[] args) throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("sensitive-words.txt");
        File file = classPathResource.getFile();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (true)
        System.out.println(bufferedReader.readLine());


    }

}
