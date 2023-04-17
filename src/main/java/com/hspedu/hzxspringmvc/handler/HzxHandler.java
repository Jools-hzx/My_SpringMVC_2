package com.hspedu.hzxspringmvc.handler;

import java.lang.reflect.Method;

/**
 * @author Zexi He.
 * @date 2023/4/17 15:03
 * @description:    此类模拟 HandlerMapping 机制
 */
public class HzxHandler {

    private String url;
    private Object instance;
    private Method method;

    public HzxHandler() {
    }

    public HzxHandler(String url, Object instance, Method method) {
        this.url = url;
        this.instance = instance;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "HzxHandler{" +
                "url='" + url + '\'' +
                ", instance=" + instance +
                ", method=" + method +
                '}';
    }
}
