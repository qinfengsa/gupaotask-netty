package com.qinfengsa.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * 响应
 * @author: qinfengsa
 * @date: 2019/7/11 21:05
 */
public class GPResponse {
    /**
     * Netty Channel封装
     */
    private ChannelHandlerContext ctx;

    /**
     * http请求
     */
    private HttpRequest req;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) {
        try {
            if (StringUtils.isBlank(out)) {
                return;
            }
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            httpResponse.headers().set("Content-Type", "text/html;");

            ctx.writeAndFlush (httpResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }

    }
}
