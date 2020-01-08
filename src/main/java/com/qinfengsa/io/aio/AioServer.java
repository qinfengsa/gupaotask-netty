package com.qinfengsa.io.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO 服务端
 * @author: qinfengsa
 * @date: 2020/1/8 18:37
 */
@Slf4j
public class AioServer {

    // 端口号
    private int port;

    private AsynchronousServerSocketChannel serverChannel;

    static ExecutorService pool = Executors.newFixedThreadPool(5);
    public AioServer(int port) {
        this.port = port;
    }


    public void start() {
        try {
            AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(pool, 1);
            serverChannel = AsynchronousServerSocketChannel.open(threadGroup);
            serverChannel.bind(new InetSocketAddress(port));
            log.debug("服务已启动,监听端口:{}" ,port);
            // 注册回调
            serverChannel.accept(this,new AioServerHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        new AioServer(8080).start();
    }
}
