package com.qinfengsa.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务端
 * @author: qinfengsa
 * @date: 2020/1/8 14:03
 */
@Slf4j
public class NioServer {

    /**
     * 端口号
     */
    private int port;

    /**
     * 多路复用器,
     */
    private Selector selector;


    NioServer(int port) {

        try {
            this.port = port;
            log.debug("服务端启动-端口号：{}",port);
            // 打开 ServerSocketChannel,用于监听客户端的连接,它是所有客户端连接的父管道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();

            // 绑定监听端口,设置连接为非阻塞模式
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            // 创建 Reactor线程,创建多路复用器
            this.selector = Selector.open();
            // 将ServerSocketChannel注册到 Reactor线程的多路复用器 Selector上,监听ACCEPT事件
            serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {

        try {
            while (true) {
                int num = selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();

                Iterator<SelectionKey> iterator = keySet.iterator();
                // 遍历keySet
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    // 客户端请求连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        // 获取客户端连接
                        SocketChannel clientChannel = serverChannel.accept();
                        // 设置为非阻塞
                        clientChannel.configureBlocking(false);
                        // 把客户端连接注册到 多路复用器 Selector上,监听READ事件
                        clientChannel.register(this.selector, SelectionKey.OP_READ);
                    }
                    // 读事件
                    else if (key.isReadable()) {
                        doRead(key);
                    }
                    // 写事件
                    else if (key.isWritable()) {
                        doWrite(key);
                    }

                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取客户端的 信息
     * @param key
     */
    private void doRead(SelectionKey key) throws IOException, InterruptedException {

        // 触发读取事件,获取客户端连接
        SocketChannel clientChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 读取
        int len = clientChannel.read(buffer);
        if (len > 0) {
            buffer.flip();
            String msg = new String(buffer.array(),0,len);
            log.debug("服务端收到信息：{}",msg);

            Thread.sleep(1000);
            // 设置为非阻塞
            clientChannel.configureBlocking(false);
            // 把客户端连接注册到 多路复用器 Selector上,监听WRITE事件
            clientChannel.register(this.selector,SelectionKey.OP_WRITE);
        }


    }

    /**
     * 向客户端写入信息
     * @param key
     */
    private void doWrite(SelectionKey key) throws IOException {
        // 触发写入事件,获取客户端连接
        SocketChannel clientChannel = (SocketChannel)key.channel();
        String msg = "已收到请求";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        // 写入
        clientChannel.write(buffer);
        clientChannel.close();
    }

    public static void main(String[] args) {
        new NioServer(8080).start();
    }




}
