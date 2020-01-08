package com.qinfengsa.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 服务端
 * @author: qinfengsa
 * @date: 2020/1/8 11:39
 */
@Slf4j
public class BioServer {
    /**
     * 端口号
     */
    private int port;

    private ServerSocket server;

    public BioServer(int port) {
        this.port = port;
        try {
            this.server = new ServerSocket(this.port);
            log.debug("服务端启动-端口号：{}",port);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

    public void start() {
        while (true) {
            // 监听连接, 获取到客户端
            try(Socket client = server.accept();
                // 获取到客户端的输入流
                InputStream is = client.getInputStream();){
                byte[] bytes = new byte[1024];
                // 获取长度
                int len = is.read(bytes);
                if (len > 0) {
                    String msg = new String(bytes,0,len);
                    log.debug("收到客户端请求信息: {}",msg);
                    Thread.sleep(1000);
                }


            } catch (IOException e) {
                log.error(e.getMessage(),e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new BioServer(8080).start();
    }

}
