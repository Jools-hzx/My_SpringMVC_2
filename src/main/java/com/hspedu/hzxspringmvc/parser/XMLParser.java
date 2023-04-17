package com.hspedu.hzxspringmvc.parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @author Zexi He.
 * @date 2023/4/17 14:03
 * @description:    此类用于解析XML文件
 */
public class XMLParser {

    /**
     * 该方法返回带扫描的包名
     * @param xmlFileName XML配置文件名
     * @return  所有需要扫描的包名
     */
    public static String getScanPackages(String xmlFileName) {

        InputStream resourceAsStream = XMLParser.class.getClassLoader().getResourceAsStream(xmlFileName);

        //使用Dom4j解析配置的 XML 文件:
        //配置信息类似如下: <component-scan base-package="com.hspedu.controller"></component-scan>
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            //获取到 component-scan 标签
            Element element = rootElement.element("component-scan");
            //获取到其 base-package 属性值
            String packages = element.attributeValue("base-package");
            if (packages.length() > 0 && null != packages) {
                return packages;
            }
        } catch (DocumentException e) {
            System.out.println("文件:" + xmlFileName + "未找到");
            e.printStackTrace();
        }
        //否则返回 ""
        return "";
    }
}
