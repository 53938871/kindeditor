package com.bangduoduo.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class RichTextInitListener implements ServletContextListener {

    public static final String GLOBAL_LOCATION = "rich_text_location";
    public static final String GLOBAL_WEB_HOST = "rich_text_host";

    private static final String RICH_TEXT_FILE = "richtext.properties";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        String env = System.getProperty("env", "dev");
        Properties p = new Properties();
        String path = File.separator + env + File.separator + RICH_TEXT_FILE;
        try {
            if(env == null || "".equals(env.trim())) {
                path = File.separator + RICH_TEXT_FILE;
            }
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
            p.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("找不到富文本配置文件",e);
        }
        String location = p.getProperty("location");
        String host = p.getProperty("host");
        context.setAttribute(GLOBAL_LOCATION, location);
        context.setAttribute(GLOBAL_WEB_HOST, host);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        context.removeAttribute(GLOBAL_LOCATION);
        context.removeAttribute(GLOBAL_WEB_HOST);
    }
}
