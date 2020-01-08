package com.qinfengsa.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 伪异步IO 使用线程池
 * @author: qinfengsa
 * @date: 2020/1/8 13:54
 */
@Slf4j
public class NewBioServer {

    static ExecutorService pool = Executors.newFixedThreadPool(5);
    /**
     * 端口号
     */
    private int port;

    private ServerSocket server;

    public NewBioServer(int port) {
        this.port = port;
        try {
            this.server = new ServerSocket(this.port);
            log.debug("服务端启动-端口号：{}",port);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }


    public void start() {
        try {
            while (true) {
                // 监听连接, 获取到客户端
                Socket client = server.accept();
                pool.execute(new SocketHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SocketHandler implements Runnable {
        Socket client;

        SocketHandler(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            // 获取到客户端的输入流
            try (InputStream is = client.getInputStream()){

                byte[] bytes = new byte[1024];
                // 获取长度
                int len = is.read(bytes);
                if (len > 0) {
                    String msg = new String(bytes,0,len);
                    log.debug("收到客户端请求信息: {}",msg);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) {
        new NewBioServer(8080).start();
    }

}
