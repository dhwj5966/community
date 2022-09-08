package com.starry.community;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Starry
 * @create 2022-09-07-9:58 AM
 * @Describe
 */
public class demo {
    public static void main(String[] args) {
        Set<Integer> set = new HashSet<Integer>() {{
            add(3);
            add(4);
            add(5);
        }};
        System.out.println(set);
        for (int number : set) {
            set.remove(number);
            System.out.println(set);
        }
    }
}
