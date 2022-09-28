package com.starry.community.mapper;

import java.io.IOException;

/**
 * @author Starry
 * @create 2022-09-27-8:51 PM
 * @Describe
 */
public class whtest {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("D:/wkhtmltopdf/bin/wkhtmltoimage " +
                    " https://www.nowcoder.com D:/wkhtmltopdf/data/4.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
