package com.qinfengsa.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 请求对象
 * @author: qinfengsa
 * @date: 2019/7/11 20:45
 */
public class GPRequest {

    /**
     * Netty Channel封装
     */
    private ChannelHandlerContext ctx;

    /**
     * http请求
     */
    private HttpRequest req;

    public GPRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }


    public String getUrl() {

        return req.uri();
    }

    public HttpMethod getMethod() {

        return req.method() ;
    }

}
