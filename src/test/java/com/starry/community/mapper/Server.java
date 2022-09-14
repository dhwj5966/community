package com.starry.community.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Starry
 * @create 2022-09-13-9:23 AM
 * @Describe
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket accept = serverSocket.accept();
            InputStream inputStream = accept.getInputStream();
            int l = 0;
            byte[] buffer = new byte[1024];
            while ((l = inputStream.read(buffer)) != - 1) {
                System.out.println(new String(buffer,0,l));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
