package com.hspedu.hzxspringmvc.context;

import com.hspedu.hzxspringmvc.annotation.AutoWired;
import com.hspedu.hzxspringmvc.annotation.Controller;
import com.hspedu.hzxspringmvc.annotation.Service;
import com.hspedu.hzxspringmvc.parser.XMLParser;
import com.sun.xml.internal.ws.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zexi He.
 * @date 2023/4/17 14:04
 * @description: 此类模拟完成各种 IOC 功能
 */
public class HzxApplicationContext {

    private List<String> classFullNameList = new ArrayList<>();
    //该集合模拟容器机制，存放 beanName 和 bean 实例
    public ConcurrentHashMap<String, Object> IOC = new ConcurrentHashMap<>();

    //该方法用于初始化容器，完成各种注入操作
    public void init(String xmlConfigLocation) {
        String scanPackages = XMLParser.getScanPackages(xmlConfigLocation);
        //这里考虑后续可能会有多个待扫描的包
        if (scanPackages.length() > 1) {
            //以 , 分割
            String[] packages = scanPackages.split(",");
            for (String aPackage : packages) {
                getClassFullNameByScan(aPackage);
            }
        } else {
            getClassFullNameByScan(scanPackages);
        }
        System.out.println("\nclassFullNameList:" + classFullNameList);

        //将bean注入
        createBeanInstance();
        System.out.println("\nIOC:" + IOC);

        //进行自动装配
        executeAutoWired();
        System.out.println("ok");
    }

    //该方法扫描目标包下的所有类，得到类的全类名
    private void getClassFullNameByScan(String packName) {
        //将带扫描的包名替换成路径
        String path = packName.replaceAll("\\.", "/");
        System.out.println("包路径:" + path);
        //获取该路径在项目中的位置
        URL url = HzxApplicationContext.class.getClassLoader().getResource(path);
        System.out.println("带扫描包的 url:" + url);

        //得到该 url 对应的文件
        File file = new File(url.getFile());
        if (file.isDirectory()) {
            //如果是目录，遍历子文件和子目录
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    //如果是子目录递归调用
                    String subPack = packName + "." + f.getName();
//                    System.out.println("子包:" + subPack);
                    getClassFullNameByScan(subPack);
                } else {
                    //如果是子文件 且为 .class 文件
                    if (f.getName().endsWith(".class")) {
                        //获取其全类名并存放到集合中
                        String className = f.getName().split(".class")[0];
                        String classFullName = packName + "." + className;
//                        System.out.println("获得全类名:" + classFullName);
                        classFullNameList.add(classFullName);
                    }
                }
            }
        }
    }

    //该方法通过全类名将bean实例化注入IOC
    private void createBeanInstance() {

        //添加保护机制
        if (classFullNameList.isEmpty()) {
            System.out.println("此时没有需要注入的类");
            return;
        }

        for (String className : classFullNameList) {
            //1.得到其 Class 对象
            try {
                Class<?> clazz = Class.forName(className);
                //2.判断是否含有 @Controller 注解
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //2.1 如果含有此注解，实例化
                    Object instance = clazz.newInstance();
                    //2.2 判断注解是否有配置 value()
                    Controller annotation = clazz.getAnnotation(Controller.class);
                    String beanName = annotation.value();
                    if (!"".equals(beanName) && null != beanName) {
                        //如果配置了,按照其配置的beanName 存放到 IOC 内
                        IOC.put(beanName, instance);
                    } else {
                        //否则按照类名小写存放
                        beanName = StringUtils.decapitalize(clazz.getSimpleName());
                        IOC.put(beanName, instance);
                    }
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //3. 注入被 Servcie 注释的 bean 对象
                    Object instance = clazz.newInstance();
                    Service annotation = clazz.getAnnotation(Service.class);
                    String beanName = annotation.value();

                    //以其实现的接口的首字母小写作为bean name 注入
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        String name = StringUtils.decapitalize(anInterface.getSimpleName());
                        //放入到IOC
                        IOC.put(name, instance);
                    }
                    //以其配置的 value 或者类名首字母小写注入
                    if (!"".equals(beanName) && null != beanName) {
                        IOC.put(beanName, instance);
                    } else {
                        IOC.put(StringUtils.decapitalize(clazz.getSimpleName()), instance);
                    }
                } else {
                    //扩展其他注解
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //该方法通过 AutoWired 注解判断执行自动装配
    private void executeAutoWired() {
        for (Map.Entry<String, Object> entry : IOC.entrySet()) {

            //遍历获取bean的class对象
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();

            //获取class类的所有field
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                //判断是否被@AutoWired注释
                if (declaredField.isAnnotationPresent(AutoWired.class)) {
                    AutoWired annotation = declaredField.getAnnotation(AutoWired.class);
                    String name = annotation.value();
                    if (!"".equals(name)) {
                        executeAutoWiredIfInstancePresent(declaredField, name, instance);
                    } else {
                        //value()为""
                        //默认按照 field 的类型首字母小写进行查找
                        name = StringUtils.decapitalize(declaredField.getType().getSimpleName());
                        executeAutoWiredIfInstancePresent(declaredField, name, instance);
                    }
                }
            }
        }
    }

    //该方法尝试从IOC中获取待自动装配的实例
    private void executeAutoWiredIfInstancePresent(Field declaredField, String beanName, Object instance) {
        Object bean = IOC.get(beanName);
        if (null != bean) {
            //如果bean不为null，进行装配
            try {
                declaredField.setAccessible(true);
                declaredField.set(instance, bean);
                declaredField.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            //否则进行报错
            throw new NullPointerException("自动装配 beanName:" + beanName + " 失败");
        }
    }
}
