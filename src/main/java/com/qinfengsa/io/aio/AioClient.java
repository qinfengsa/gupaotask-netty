package com.qinfengsa.io.aio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.UUID;

/**
 * AIO 客户端
 * @author: qinfengsa
 * @date: 2020/1/8 18:37
 */
@Slf4j
public class AioClient {


    private final AsynchronousSocketChannel client;

    // 服务地址
    private final InetSocketAddress serverAdrress = new InetSocketAddress("localhost", 8080);


    public AioClient() throws Exception{
        client = AsynchronousSocketChannel.open();
    }
    public void connect()throws Exception{
        client.connect(serverAdrress,null,new CompletionHandler<Void,Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                try {
                    client.write(ByteBuffer.wrap("这是一条测试数据".getBytes())).get();
                    log.debug("已发送至服务器");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });
        final ByteBuffer bb = ByteBuffer.allocate(1024);
        client.read(bb, null, new CompletionHandler<Integer,Object>(){
            @Override
            public void completed(Integer result, Object attachment) {
                log.debug("IO 操作完成:{}" , result);
                log.debug("获取反馈结果:{}" , new String(bb.array(),0,result));
            }
            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
    public static void main(String args[])throws Exception{
        new AioClient().connect();
    }
}
