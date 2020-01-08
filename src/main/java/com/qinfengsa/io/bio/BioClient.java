package com.qinfengsa.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO 客户端
 * @author: qinfengsa
 * @date: 2020/1/8 11:39
 */
@Slf4j
public class BioClient {



    static ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            pool.execute(new SendInfo(i));
        }

        pool.shutdown();

    }

    static class SendInfo implements Runnable {
        final int index;

        SendInfo(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket("localhost",8080);
                 OutputStream os = socket.getOutputStream()) {
                String msg = this.index + ":" + UUID.randomUUID().toString();
                log.debug("发送数据：{}",msg);
                os.write(msg.getBytes());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
