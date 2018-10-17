package com.transing.dpmbs.util;



import javax.mail.*;
import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Author:frank
 * @Description:邮箱工具类
 * @Date: 2018/4/20  13:48
 */
public class QQMailUtil {
    private static  final  String HOST="smtp.qq.com";//smtp服务器
    private static  final  String PORT="587";//端口号


    public static void sendMail(String title,String addresser,String command,String content,String addressee){
        //创建properties记录邮箱的一些属性
        final Properties pro=new Properties();
        //表示smtp发送邮件,必须进行身份验证
        pro.put("mail.smtp.auth","true");
        //填写smtp服务器
        pro.put("mail.smtp.host",HOST);
        //端口号
        pro.put("mail.smtp.port",PORT);
        //填写你的账号
        pro.put("mail.user",addresser);
        //smtp口令
        pro.put("mail.password",command);
        //构建授权信息,用于smtp进行身份验证
        Authenticator authenticator=new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String userName=pro.getProperty("mail.user");
                String password=pro.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        //使用环境属性和授权信息,创建邮件会话
        Session session=Session.getInstance(pro,authenticator);
        //创建邮件信息
        MimeMessage message=new MimeMessage(session);
        //设置发件人
        InternetAddress form = null;
        try {
            form = new InternetAddress(
                    pro.getProperty("mail.user"));
            message.setFrom(form);
            // 设置收件人的邮箱
            InternetAddress to = new InternetAddress(addressee);
            message.setRecipient(MimeMessage.RecipientType.TO, to);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");

            // 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
