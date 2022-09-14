package com.starry.community.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Starry
 * @create 2022-09-13-9:17 AM
 * @Describe
 */
public class Client {
    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("localhost",8080));
            String message = "GET /community/index HTTP/1.1\n" +
                    "Host: localhost:8080\n" +
                    "Connection: keep-alive\n" +
                    "Cache-Control: max-age=0\n" +
                    "sec-ch-ua: \"Microsoft Edge\";v=\"105\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"105\"\n" +
                    "sec-ch-ua-mobile: ?0\n" +
                    "sec-ch-ua-platform: \"Windows\"\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.33\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                    "Sec-Fetch-Site: none\n" +
                    "Sec-Fetch-Mode: navigate\n" +
                    "Sec-Fetch-User: ?1\n" +
                    "Sec-Fetch-Dest: document\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6\n" +
                    "Cookie: ticket=2b16ef9ec82643b19dd3b92ce08a0243; Idea-d455399b=ea6d69c9-048e-4b1a-9635-557ee3293e36";
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            outputStream.write(message.getBytes());
            int l = 0;
            byte[] buffer = new byte[1024];
            while ((l = inputStream.read(buffer)) != -1) {
                System.out.println(new String(buffer,0, l));
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
