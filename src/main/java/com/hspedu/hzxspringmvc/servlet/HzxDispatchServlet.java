package com.hspedu.hzxspringmvc.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hspedu.hzxspringmvc.annotation.RequestMapping;
import com.hspedu.hzxspringmvc.annotation.RequestParam;
import com.hspedu.hzxspringmvc.annotation.ResponseBody;
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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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
        System.out.println("\n控制器映射:" + handlerList);
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
//                method.invoke(instance, request, response);

                //动态获取待执行方法的形参类型列表
                Class<?>[] parameterTypes = method.getParameterTypes();

                //初始化我们自己的实参列表，存放请求发来的实参, 长度与形参列表一致
                Object[] params = new Object[parameterTypes.length];
                System.out.println("params.length:" + params.length);

                //先处理形参类型为 HttpServletRequest 或者 HttpServletResponse
                for (int i = 0; i < parameterTypes.length; i++) {
                    //取出一个形参类型
                    Class<?> parameterType = parameterTypes[i];
                    if (HttpServletRequest.class.isAssignableFrom(parameterType)) {
                        params[i] = request;
                    } else if (HttpServletResponse.class.isAssignableFrom(parameterType)) {
                        params[i] = response;
                    }
                }

                //6.3 处理含有或不含有@RequestParam 注解的方法
                //1.先获取 请求携带的所有参数名和其对应的value, 这里简单处理，假设一个k对应一个v
                Map<String, String[]> parameterMap = request.getParameterMap();
                //2.编写方法获取该实参列表应该存放对应形参类型值的位置
                Parameter[] parameters = method.getParameters();

                for (String k : parameterMap.keySet()) {
                    int index = getParamValueIndex(k, parameters);
                    if (-1 != index) {
                        //如果不是-1。说明找到了位置; 这里简化，默认一个k仅配对一个v
                        params[index] = parameterMap.get(k)[0];
                    }
                }

                System.out.println("封装好的实参列表:" + Arrays.toString(params));

                //执行方法
                Object res = method.invoke(instance, params);
                executeResult(res, request, response, method);
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

    //该方法模拟完成视图解析步骤
    private void executeResult(Object res,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Method method) {
        if (res instanceof String) {
            //如果是String 类型，默认进行转发或者重定向
            String result = ((String) res);
            //"forward:/login_ok.jsp"
            try {
                if (result.contains(":")) {
                    String[] split = result.split(":");
                    String viewType = split[0];     //视图处理类型: forward / redirect
                    String viewLocation = split[1];
                    if ("forward".equals(viewType)) {
                        request.getRequestDispatcher(viewLocation).forward(request, response);
                    } else if ("redirect".equals(viewType)) {
                        response.sendRedirect(request.getContextPath() + viewLocation);
                    }
                } else {
                    //默认按照请求转发处理
                    request.getRequestDispatcher(result).forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (res instanceof List) {
            //如果返回结果是 List 类型，按照Json格式返回
            if (method.isAnnotationPresent(ResponseBody.class)) {
                String value = method.getAnnotation(ResponseBody.class).value();
                if ("json".equals(value)) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = mapper.writeValueAsString(res);
                        PrintWriter writer = response.getWriter();
                        writer.write(json);
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 该方法返回请求的参数值应该存放到实参列表内的位置索引
     *
     * @param reqParamName 请求携带的参数名
     * @param parameters   待执行方法的形参名列表
     * @return 位置索引
     */
    private int getParamValueIndex(String reqParamName, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                //如果该形参携带注解，判断请求的参数名是否和value一致
                String value = parameter.getAnnotation(RequestParam.class).value();
                if (reqParamName.equals(value)) {
                    return i;
                }
            } else if (parameter.getName().equals(reqParamName)) {
                //否则，按照默认形参名匹配
                return i;
            }
        }
        //如果未匹配到，返回-1
        return -1;
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

    //该方法将url映射等信息封装到 HzxHandler 并存储到集合中
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
