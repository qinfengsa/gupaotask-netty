package com.qinfengsa.io.aio;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * AIO 回调接口
 * @author: qinfengsa
 * @date: 2020/1/8 18:42
 */
@Slf4j
public class AioServerHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
    @Override
    public void completed(AsynchronousSocketChannel result, AioServer attachment) {
        doRead(result);
    }


    /**
     * 读取client发送的消息打印到控制台
     * @param clientChannel 服务端于客户端通信的 channel
     */
    private void doRead(AsynchronousSocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        clientChannel.read(buffer, buffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        log.debug("result:{}",result);
                        attachment.flip(); // 移动 limit位置
                        // 读取client发送的数据
                        String msg = new String(attachment.array(),0,result);
                        log.debug("服务端收到信息：{}",msg);
                        // 向client写入数据
                        doWrite(clientChannel);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        log.debug("失败：{}",exc.getMessage(),exc);
                    }
                });
    }

    private void doWrite(AsynchronousSocketChannel clientChannel) {
        // 向client发送数据
        String msg = "已收到请求";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        clientChannel.write(buffer);
    }


    @Override
    public void failed(Throwable exc, AioServer attachment) {
        log.error("IO失败：{}",exc.getMessage(),exc);
    }
}
