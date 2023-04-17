package com.hspedu.controller;

import com.hspedu.hzxspringmvc.annotation.Controller;
import com.hspedu.hzxspringmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Zexi He.
 * @date 2023/4/17 13:57
 * @description:    该类为 控制层的一个处理器对象
 */

@Controller(value = "contestHandler")
public class ContestHandler {

    @RequestMapping(value = "/contest/list")
    public void listContest(HttpServletRequest request,
                            HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");

        String header = "<h1>查看所有比赛信息</h1>";

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(header);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
