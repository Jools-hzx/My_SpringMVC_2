package com.hspedu.hzxspringmvc.servlet;

import com.hspedu.hzxspringmvc.annotation.RequestMapping;
import com.hspedu.hzxspringmvc.context.HzxApplicationContext;
import com.hspedu.hzxspringmvc.handler.HzxHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zexi He.
 * @date 2023/4/17 13:44
 * @description:
 */
public class HzxDispatchServlet extends HttpServlet {

    private HzxApplicationContext applicationContext;
    //该集合存放HzxHandler对象，模拟HandlerMapping功能
    private List<HzxHandler> handlerList = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        //获取xml配置文件的位置
        /*
        <init-param>
            <param-name>contextConfiguration</param-name>
            <param-value>classpath:config.xml</param-value>
        </init-param>
         */
        String xmlLocation = config.getInitParameter("contextConfiguration").split(":")[1];

        //初始化IOC
        applicationContext = new HzxApplicationContext();
        applicationContext.init(xmlLocation);

        //初始化控制器映射
        executeHandlerMapping();
        System.out.println("\n控制器隐射器:" + handlerList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("中央分发控制器的 doGet 方法被调用了");
        executeDispatch(req, resp);
    }

    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        System.out.println("请求的URI:" + requestURI);

        //设置获取请求参数的编码格式
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("设置request编码格式不正确");
            e.printStackTrace();
        }

        //遍历 handlerList
        HzxHandler handler = getHandler(request, requestURI);
        if (null != handler) {
            Method method = handler.getMethod();
            Object instance = handler.getInstance();
            //使用反射机制执行该方法
            try {
                //先考虑形参只为 HttpServletRequest 和 HttpServletResponse 的情况
                method.invoke(instance, request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //如果没有找到handler，返回404
            response.setContentType("text/html;charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.write("<h1>404 NOT FOUND");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //该方法根据浏览器发送的uri得到相应的handler去处理
    private HzxHandler getHandler(HttpServletRequest request, String requestURI) {
        for (HzxHandler hzxHandler : handlerList) {
            //完整url: 工程项目名 + RequestMapping配置的 value
            String url = request.getContextPath() + hzxHandler.getUrl();
            if (requestURI.equals(url)) {
                //如果找到，返回该Handler
                return hzxHandler;
            }
        }
        return null;
    }

    private void executeHandlerMapping() {
        //判断IOC是否为空
        if (applicationContext.IOC.isEmpty()) {
            System.out.println("IOC此时为空！");
            return;
        }
        //遍历IOC中存储的实例
        for (Map.Entry<String, Object> entry : applicationContext.IOC.entrySet()) {
            //1.获取到实例
            Object instance = entry.getValue();
            //获取其 class 对象
            Class<?> clazz = instance.getClass();
            //2.获取该Class 对象中所声明的所有方法
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                //2.1 判断是否有注解,并且注解配置不能为空字串
                if (method.isAnnotationPresent(RequestMapping.class) &&
                        !"".equals(method.getAnnotation(RequestMapping.class).value())) {
                    //封装到 HzxHandler
                    String url = method.getAnnotation(RequestMapping.class).value();
                    HzxHandler hzxHandler = new HzxHandler();
                    hzxHandler.setUrl(url);
                    hzxHandler.setInstance(instance);
                    hzxHandler.setMethod(method);
                    //2.2 封装完毕后存放到集合中
                    handlerList.add(hzxHandler);
                }
            }
        }
    }
}
