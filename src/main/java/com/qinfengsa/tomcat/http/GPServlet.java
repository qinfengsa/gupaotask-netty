package com.qinfengsa.tomcat.http;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Objects;

/**
 * @author: qinfengsa
 * @date: 2019/7/11 21:05
 */
public abstract class GPServlet {

    public final void service(GPRequest request,GPResponse response) throws Exception{
        // 判断调用的方法
        if (Objects.equals(HttpMethod.GET,request.getMethod())) {
            doGet(request,response);
        } else {
            doPost(request,response);
        }
    }

    protected abstract void doPost(GPRequest request, GPResponse response);

    protected abstract void doGet(GPRequest request, GPResponse response);
    
}
