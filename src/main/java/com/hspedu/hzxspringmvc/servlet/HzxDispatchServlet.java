package com.hspedu.hzxspringmvc.servlet;

import com.hspedu.hzxspringmvc.context.HzxApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Zexi He.
 * @date 2023/4/17 13:44
 * @description:
 */
public class HzxDispatchServlet extends HttpServlet {

    private HzxApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        //初始化IOC
        applicationContext = new HzxApplicationContext();
        applicationContext.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("中央分发控制器的 doPost 方法被调用了....");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("中央分发控制器的 doGet 方法被调用了");
    }
}
