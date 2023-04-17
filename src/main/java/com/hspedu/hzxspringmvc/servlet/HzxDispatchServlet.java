package com.hspedu.hzxspringmvc.servlet;

import com.hspedu.hzxspringmvc.annotation.RequestMapping;
import com.hspedu.hzxspringmvc.context.HzxApplicationContext;
import com.hspedu.hzxspringmvc.handler.HzxHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public void init() throws ServletException {
        //初始化IOC
        applicationContext = new HzxApplicationContext();
        applicationContext.init();

        //初始化控制器映射
        executeHandlerMapping();
        System.out.println("\n控制器隐射器:" + handlerList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("中央分发控制器的 doPost 方法被调用了....");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("中央分发控制器的 doGet 方法被调用了");
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
