package com.qinfengsa.tomcat;

import com.qinfengsa.tomcat.http.GPRequest;
import com.qinfengsa.tomcat.http.GPResponse;
import com.qinfengsa.tomcat.http.GPServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 基于Netty构建Tomcat
 * @author: qinfengsa
 * @date: 2019/7/11 19:03
 */
@Slf4j
public class GPTomcat {

    /**
     * 端口号
     */
    private int port;

    /**
     * servlet Map容器
     */
    private Map<String, GPServlet> servletMap = new HashMap<>();

    /**
     * 解析配置文件
     */
    private Properties webxml = new Properties();

    public GPTomcat(int port) {
        this.port = port;
    }


    /**
     * tomcat 初始化
     */
    private void init() {

        String path = this.getClass().getResource("/").getPath();
        String webPath = path + "web.properties";

        try (FileInputStream fis = new FileInputStream(webPath)){
            webxml.load(fis);

            for (Object obj : webxml.keySet() ) {
                String key = obj.toString();
                if (key.endsWith(".url")) {

                    String classKey = key.replace(".url",".className");

                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(classKey);
                    GPServlet servlet = (GPServlet) Class.forName(className).newInstance();
                    servletMap.put(url,servlet);
                }

            }

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * tomcat启动
     */
    public void start(){
        init();
        // 配置服务端的 NIO 线程池,用于网络事件处理，实质上他们就是 Reactor 线程组

        // BOSS线程 用于服务端接受客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker线程 用于进行 SocketChannel 网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 是 Netty 用于启动 NIO 服务端的辅助启动类，用于降低开发难度
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)

                    // 针对主线程的配置 分配线程最大数量 128
					.option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // HttpResponseEncoder 编码器
                            pipeline.addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            pipeline.addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            pipeline.addLast(new GPTomcatServerHandler());
                        }
                    });;
            // bind绑定端口，sync同步等待
            ChannelFuture future = bootstrap.bind(port).sync();
            log.debug("{},服务器开始监听端口，等待客户端连接.........",Thread.currentThread().getName() );
            // 等待端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GPTomcat(8080).start();
    }


    class GPTomcatServerHandler extends ChannelInboundHandlerAdapter {

        /**
         * 收到客户端消息，自动触发
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            // http请求
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                GPRequest request = new GPRequest(ctx,req);
                GPResponse response = new GPResponse(ctx,req);

                String url = request.getUrl();

                GPServlet servlet = servletMap.get(url);

                if (Objects.nonNull(servlet)) {

                    servlet.service(request,response);

                } else {
                    response.write("404 - Not Found");
                }


            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        /**
         * 发生异常
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error(cause.getMessage(),cause);
            ctx.close();
        }
    }
}
