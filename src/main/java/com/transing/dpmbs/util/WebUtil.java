/**
 * @project: with
 * @Title: WebUtil.java
 * @Package: com.transing.dpmbs.util
 * <p/>
 * Copyright (c) 2014-2017 Jeeframework.com Limited, Inc.
 * All rights reserved.
 */
package com.transing.dpmbs.util;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.util.net.IPUtils;
import com.jeeframework.util.validate.Validate;
import org.apache.struts2.RequestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * Web端数据工具类
 * <p/>
 *
 * @author lance
 * @version 1.0 2015-3-4 下午02:34:50
 * @Description: 所有web相关的操作工具方法
 */
public class WebUtil {

    private static Properties configProperties = SpringContextHolder.getBean("configProperties");

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    private WebUtil() {
    }

    /**
     * 根据request获取uri请求
     *
     * @param request
     * @return
     */
    public static String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request
                .getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        uri = RequestUtils.getServletPath(request);
        if (!Validate.isEmpty(uri)) {
            return uri;
        }

        uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

    /**
     * 简单描述：组装静态资源的绝对URL
     * <p/>
     *
     * @param staticURI
     * @return
     */
    public static String combinateStaticURL(String staticURI) {

        if (Validate.isEmpty(staticURI)) {
            return "";
        }
        if (staticURI.startsWith(HTTP_PREFIX)) {
            return staticURI;
        }

        String host = getStaticServerByEnv();

        return HTTP_PREFIX + host + staticURI;

    }

    /**
     * 简单描述：返回用户的头像
     * <p/>
     *
     * @param avatarURI
     * @param width     0 原图 640 260 100 4种规格
     * @return
     */
    public static String getUserAvatar(String avatarURI, int width) {

        if (Validate.isEmpty(avatarURI)) {
            return ""; //返回系统默认头像
        }
        if (avatarURI.startsWith(HTTP_PREFIX) || avatarURI.startsWith(HTTPS_PREFIX)) {
            return avatarURI;
        }

        String ext = com.jeeframework.util.io.FileUtils.getExtention(avatarURI);
        int posOfUnderScode = avatarURI.lastIndexOf('_');
        String destImagePrefix = "";
        if (posOfUnderScode >= 0) {
            destImagePrefix = avatarURI.substring(0, posOfUnderScode);
        }
        String suffix ;
        if (!Validate.isEmpty(destImagePrefix)) {
            suffix = destImagePrefix + "_" + width + ext;
        } else {
            int posOfLastDot = avatarURI.lastIndexOf('.');
            String avatarURITmp = posOfLastDot > 0 ? avatarURI.substring(0, posOfLastDot) : avatarURI;
            suffix = avatarURITmp + "_" + width + ext;
        }

        if (!suffix.startsWith("/")) {
            suffix = "/" + suffix;
        }

        String host = getStaticServerByEnv();

        return HTTP_PREFIX + host + suffix;

    }

    /**
     * 根据环境配置返回web服务器的地址，这个是动态代码的服务器
     *
     * @return
     */
    public static String getWebServerByEnv() {
        String host = "www.jeeframework.com";

        return getHostNameByEnv(host, IPUtils.LOCAL_IP + ":8080", "dev.jeeframework.com", "test.jeeframework.com", "www.jeeframework.com");
    }

    public static String getHostNameByEnv(String host, String localhost, String devHost, String testHost, String idcHost) {
        String confEnv = System.getProperty("conf.env");
        String hostTmp = null;
        if (!Validate.isEmpty(confEnv)) {
            if ("local".equals(confEnv)) {
                hostTmp = localhost;
            }
            if ("dev".equals(confEnv)) {
                hostTmp = devHost;
            }
            if ("test".equals(confEnv)) {
                hostTmp = testHost;
            }
            if ("idc".equals(confEnv)) {
                hostTmp = idcHost;
            }
        }
        if (Validate.isEmpty(hostTmp)) {
            hostTmp = host;
        }
        return hostTmp;
    }

    /**
     * 根据环境配置返回静态资源服务器的地址
     *
     * @return
     */
    public static String getStaticServerByEnv() {
        String host = "static.jeeframework.com";
        return getHostNameByEnv(host, "staticdev.jeeframework.com", "http://dpmfs2dev.dookoo.net", "http://dpmfs2test.dookoo.net", "http://dpmfs.dookoo.net");
    }

    /**
     * 根据环境配置返回算法平台服务器的地址
     *
     * @return
     */
    public static String getApbsServerByEnv() {
        return configProperties.getProperty("apbs.url");
    }

    /**
     * 根据环境配置返回算法平台服务器的地址
     *
     * @return
     */
    public static String getDpmbsUploadByEnv() {
        return configProperties.getProperty("dpmbs_upload.url");
    }

    /**
     * 根据环境配置返回调度系统服务器的地址
     *
     * @return
     */
    public static String getDpmssServerByEnv() {
        return configProperties.getProperty("dpmss.url");
    }

    /**
     * 根据环境配置返回语料库服务器的地址
     *
     * @return
     */
    public static String getCorpusServerByEnv() {
        return configProperties.getProperty("corpus.url");
    }

    /**
     * 获取环境配置返回调度系统文件下载的路径
     * @return
     */
    public static String getDpmssFilePathByEnv(){
        return configProperties.getProperty("dpmss_download.url");
    }

    /**
     * 获取 抓取系统 的地址
     * @return
     */
    public static String getCrawlServerByEnv(){
        return configProperties.getProperty("crawl.url");
    }

    public static String getCrawlBsServerByEnv(){
        return configProperties.getProperty("crawlbs.url");
    }

    public static String getMCrawlServerByEnv(){
        return configProperties.getProperty("mCrawl.url");
    }

    public static String getBaseServerByEnv(){
        return configProperties.getProperty("base.url");
    }

    public static String getLocalHostServerByEnv(){
        return configProperties.getProperty("dpmbs.url");
    }

}