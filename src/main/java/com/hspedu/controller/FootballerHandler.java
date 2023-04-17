package com.hspedu.controller;

import com.hspedu.entity.Footballer;
import com.hspedu.hzxspringmvc.annotation.AutoWired;
import com.hspedu.hzxspringmvc.annotation.Controller;
import com.hspedu.hzxspringmvc.annotation.RequestMapping;
import com.hspedu.hzxspringmvc.annotation.RequestParam;
import com.hspedu.service.FootballerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Zexi He.
 * @date 2023/4/17 13:55
 * @description: 该类为 控制层的一个处理器对象
 */

@Controller(value = "footballerHandler")
public class FootballerHandler {

    @AutoWired
    private FootballerService footballerService;

    @RequestMapping(value = "/footballer/list")
    public void listFootballers(HttpServletRequest request,
                                HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");

        String header = "<h1>查看所有球员信息</h1>";

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

    //该方法通过调用 Service 层返回球员信息结果集合
    @RequestMapping(value = "/footballer/list2")
    public void listFootballersByService(HttpServletRequest request,
                                         HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        StringBuilder content = new StringBuilder("<h1>查看所有球员信息</h1>");
        List<Footballer> footballers = footballerService.listAllFootballers();
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Footballer footballer : footballers) {
            content.append("<tr><td>" + footballer.getId() +
                    "</td><td>" + footballer.getName() +
                    "</td><td>" + footballer.getClub());
        }
        content.append("</table>");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(content.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法通过调用 Service 层返回球员信息结果集合
    @RequestMapping(value = "/footballer/list2")
    public void listFootballersByName(@RequestParam(value = "name") String keyName,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");

        System.out.println("查询的关键字:" + keyName);

        StringBuilder content = new StringBuilder("<h1>查看所有球员信息</h1>");
        List<Footballer> footballers = footballerService.listFootballersByName(keyName);
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Footballer footballer : footballers) {
            content.append("<tr><td>" + footballer.getId() +
                    "</td><td>" + footballer.getName() +
                    "</td><td>" + footballer.getClub());
        }
        content.append("</table>");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(content.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
