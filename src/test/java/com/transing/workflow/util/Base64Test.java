package com.transing.workflow.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.transing.dpmbs.util.Base64Util;
import sun.misc.BASE64Decoder;

import java.io.*;

import static org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.InputType.file;

/**
 * Created by byron on 2018/1/30 0030.
 */
public class Base64Test {
    public static boolean generateImage(String imgStr, String path) {
        if(imgStr == null){
            return false;
        }else{
            BASE64Decoder base64Decoder = new BASE64Decoder();
            try{
                byte[] b = base64Decoder.decodeBuffer(imgStr);
                for(int i = 0; i < b.length; ++i){
                    if (b[i] < 0) {
                        b[i] += 256;
                    }
                }
                FileOutputStream out = new FileOutputStream(path);
                out.write(b);
                out.flush();
                out.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean GenerateImage2(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "D:\\data\\new.jpeg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static String readToString(String strt) {
        try {
            String encoding = "UTF-8";
            File file = new File(strt);
            Long filelength = file.length();
            byte[] filecontent = new byte[filelength.intValue()];
            try {
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return new String(filecontent, encoding);
            } catch (UnsupportedEncodingException e) {
                System.err.println("The OS does not support " + encoding);
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) {
       // GenerateImage2(readToString("C:\\Users\\Administrator\\Desktop\\base641.txt"));
       String str = readToString("C:\\Users\\Administrator\\Desktop\\base64.txt").substring(1,readToString
               ("C:\\Users\\Administrator\\Desktop\\base64.txt").length());

       // GenerateImage2(str);
        Base64Util.GenerateImage(str,"D:\\data\\img.png");
//        System.out.println(str);
    }

}
