package com.qinfengsa.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * NIO 客户端
 * @author: qinfengsa
 * @date: 2020/1/8 14:03
 */
@Slf4j
public class NioClient {

    /**
     * 服务端地址
     */
    private final InetSocketAddress serverAdrress = new InetSocketAddress("localhost", 8080);

    private Selector selector;

    private SocketChannel clientChannel;

    public NioClient() {
    }

    private void initClient() throws IOException {
        this.selector = Selector.open();
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.connect(serverAdrress);
        clientChannel.register(selector, SelectionKey.OP_CONNECT);
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isConnectable()){
                    doConnect(key);
                }else if (key.isReadable()){

                    doRead(key);
                }
                iterator.remove();
            }
        }
    }
    public void doConnect(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        if (clientChannel.isConnectionPending()){
            clientChannel.finishConnect();
        }
        clientChannel.configureBlocking(false);

        String name =  UUID.randomUUID().toString();
        log.debug("发送数据：{}",name);
        ByteBuffer buffer = ByteBuffer.wrap(name.getBytes());
        clientChannel.write(buffer);
        clientChannel.register(key.selector(),SelectionKey.OP_READ);
    }

    public void doRead(SelectionKey key) throws IOException {

        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int len = clientChannel.read(buffer);
        if (len > 0) {
            buffer.flip();
            String msg = new String(buffer.array(),0,len);
            log.debug("收取服务端信息：{}",msg);
            key.interestOps(SelectionKey.OP_READ);
        }
    }


    public static void main(String[] args) throws IOException {
        new NioClient().initClient();
    }

}
