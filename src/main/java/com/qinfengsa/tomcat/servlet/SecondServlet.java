package com.qinfengsa.tomcat.servlet;

import com.qinfengsa.tomcat.http.GPRequest;
import com.qinfengsa.tomcat.http.GPResponse;
import com.qinfengsa.tomcat.http.GPServlet;

/**
 * servlet 实现
 * @author: qinfengsa
 * @date: 2019/7/11 21:05
 */
public class SecondServlet extends GPServlet {

    @Override
    public void doGet(GPRequest request, GPResponse response)  {
        this.doPost(request, response);
    }

    @Override
    public void doPost(GPRequest request, GPResponse response) {
        response.write("This is Second Servlet");
    }
}
