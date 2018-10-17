package com.transing.dpmbs.util;

import com.jeeframework.util.validate.Validate;
import org.apache.commons.collections.map.HashedMap;
import org.eclipse.jetty.util.ArrayQueue;

import java.io.*;
import java.util.*;

public class CSVFileUtil {

    private FileInputStream fis = null;
    private InputStreamReader isw = null;
    private BufferedReader br = null;

    public CSVFileUtil(String filename,String encode) throws Exception {
        fis = new FileInputStream(filename);
        isw = new InputStreamReader(fis, encode);
        br = new BufferedReader(isw);
    }

    // ==========以下是公开方法=============================
    /**
     * 从CSV文件流中读取一个CSV行。
     *
     * @throws Exception
     */
    public String readLine() throws Exception {

        StringBuffer readLine = new StringBuffer();
        boolean bReadNext = true;

        while (bReadNext) {
            //
            if (readLine.length() > 0) {
                readLine.append("\r\n");
            }
            // 一行
            String strReadLine = br.readLine();

            // readLine is Null
            if (strReadLine == null) {
                return null;
            }
            readLine.append(strReadLine);

            // 如果双引号是奇数的时候继续读取。考虑有换行的是情况。
            if (countChar(readLine.toString(), '"', 0) % 2 == 1) {
                bReadNext = true;
            } else {
                bReadNext = false;
            }
        }
        return readLine.toString();
    }

    /**
     *把CSV文件的一行转换成字符串数组。指定数组长度，不够长度的部分设置为null。
     */
    public static String[] fromCSVLine(String source, int size) {
        ArrayList tmpArray = fromCSVLinetoArray(source);
        if (size < tmpArray.size()) {
            size = tmpArray.size();
        }
        String[] rtnArray = new String[size];
        tmpArray.toArray(rtnArray);
        return rtnArray;
    }

    /**
     * 把CSV文件的一行转换成字符串数组。不指定数组长度。
     */
    public static ArrayList fromCSVLinetoArray(String source) {
        if (source == null || source.length() == 0) {
            return new ArrayList();
        }
        int currentPosition = 0;
        int maxPosition = source.length();
        int nextComma = 0;
        ArrayList rtnArray = new ArrayList();
        while (currentPosition < maxPosition) {
            nextComma = nextComma(source, currentPosition);
            rtnArray.add(nextToken(source, currentPosition, nextComma));
            currentPosition = nextComma + 1;
            if (currentPosition == maxPosition) {
                rtnArray.add("");
            }
        }
        return rtnArray;
    }


    /**
     * 把字符串类型的数组转换成一个CSV行。（输出CSV文件的时候用）
     */
    public static String toCSVLine(String[] strArray) {
        if (strArray == null) {
            return "";
        }
        StringBuffer cvsLine = new StringBuffer();
        for (int idx = 0; idx < strArray.length; idx++) {
            String item = addQuote(strArray[idx]);
            cvsLine.append(item);
            if (strArray.length - 1 != idx) {
                cvsLine.append(',');
            }
        }
        return cvsLine.toString();
    }

    /**
     * 字符串类型的List转换成一个CSV行。（输出CSV文件的时候用）
     */
    public static String toCSVLine(ArrayList strArrList) {
        if (strArrList == null) {
            return "";
        }
        String[] strArray = new String[strArrList.size()];
        for (int idx = 0; idx < strArrList.size(); idx++) {
            strArray[idx] = (String) strArrList.get(idx);
        }
        return toCSVLine(strArray);
    }

    // ==========以下是内部使用的方法=============================
    /**
     *计算指定文字的个数。
     *
     * @param str 文字列
     * @param c 文字
     * @param start  开始位置
     * @return 个数
     */
    private int countChar(String str, char c, int start) {
        int i = 0;
        int index = str.indexOf(c, start);
        return index == -1 ? i : countChar(str, c, index + 1) + 1;
    }

    /**
     * 查询下一个逗号的位置。
     *
     * @param source 文字列
     * @param st  检索开始位置
     * @return 下一个逗号的位置。
     */
    private static int nextComma(String source, int st) {
        int maxPosition = source.length();
        boolean inquote = false;
        while (st < maxPosition) {
            char ch = source.charAt(st);
             if (!inquote && ch == ',') {
                break;
            } /*else if ('"' == ch) {
                inquote = !inquote;
            }*/
            st++;
        }
        return st;
    }

    /**
     * 取得下一个字符串
     */
    private static String nextToken(String source, int st, int nextComma) {
        StringBuffer strb = new StringBuffer();
        int next = st;
        while (next < nextComma) {
            char ch = source.charAt(next++);
            if (ch == '"') {
                if ((st + 1 < next && next < nextComma) && (source.charAt(next) == '"')) {
                    strb.append(ch);
                    next++;
                }
            } else {
                strb.append(ch);
            }
        }
        return strb.toString();
    }

    /**
     * 在字符串的外侧加双引号。如果该字符串的内部有双引号的话，把"转换成""。
     *
     * @param item  字符串
     * @return 处理过的字符串
     */
    private static String addQuote(String item) {
        if (item == null || item.length() == 0) {
            return "\"\"";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        for (int idx = 0; idx < item.length(); idx++) {
            char ch = item.charAt(idx);
            if ('"' == ch) {
                sb.append("\"\"");
            } else {
                sb.append(ch);
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static File createFileAndColName(String filePath, String fileName,  String[] colNames){
        File csvFile = new File(filePath, fileName);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csvFile, "GBK");
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<colNames.length; i++){
                if( i<colNames.length-1 )
                    sb.append(colNames[i]+",");
                else
                    sb.append(colNames[i]+"\r\n");

            }
            pw.print(sb.toString());
            pw.flush();
            pw.close();
            return csvFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean appendListDate(File csvFile, List<List<String>> data){
        try {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), "GBK"), 1024);
            for(int i=0; i<data.size(); i++){
                List tempData = data.get(i);
                StringBuffer sb = new StringBuffer();
                for(int j=0; j<tempData.size(); j++){
                    if(j<tempData.size()-1)
                        sb.append(tempData.get(j)+",");
                    else
                        sb.append(tempData.get(j)+"\r\n");
                }
                bw.write(sb.toString());
                if(i%1000==0)
                    bw.flush();
            }
            bw.flush();
            bw.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean appendDate(File csvFile, List<String> data){
        try {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), "GBK"), 1024);
            StringBuffer sb = new StringBuffer();
            for(int j=0; j<data.size(); j++){
                if(j<data.size()-1)
                    sb.append(data.get(j)+",");
                else
                    sb.append(data.get(j)+"\r\n");
            }
            bw.write(sb.toString());
            bw.flush();
            bw.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(Runtime.getRuntime().availableProcessors());//cpu核心数

        /*File file = new File("D:\\document\\test");
        File[] files = file.listFiles();

        for (File file1:files) {

            Thread t = new Thread(new ImportDataRunable(file1));
            t.start();

            *//*CSVFileUtil csvFileUtil = new CSVFileUtil(file1.getAbsolutePath(),"gb2312");
            String firstSource = csvFileUtil.readLine();

            String source = csvFileUtil.readLine();
            int i = 1;

            while (!Validate.isEmpty(source)) {

                List<String> list = CSVFileUtil.fromCSVLinetoArray(source);

                if(list.size() < 18){
                    System.out.println(file1.getAbsolutePath()+"##"+list.size()+"@@@"+list.toString());

                }

                i++;
                source = csvFileUtil.readLine();

            }*//*
        }*/

    }


    public static void main2(String[] args) {
        try {
            String filePath = "D:\\document\\test\\";
            CSVFileUtil csvFileUtil = new CSVFileUtil("D:\\document\\VGC-数据-201705.csv","gb2312");
            String firstSource = csvFileUtil.readLine();

            String source = csvFileUtil.readLine();

            File fileDir = new File(filePath);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            File csvFile1 = createFileAndColName(filePath, "csv1.csv",firstSource.split(","));
            File csvFile2 = createFileAndColName(filePath, "csv2.csv",firstSource.split(","));
            File csvFile3 = createFileAndColName(filePath, "csv3.csv",firstSource.split(","));
            File csvFile4 = createFileAndColName(filePath, "csv4.csv",firstSource.split(","));
            File csvFile5 = createFileAndColName(filePath, "csv5.csv",firstSource.split(","));
            Queue<File> mapQueue = new ArrayQueue();
            mapQueue.add(csvFile1);
            mapQueue.add(csvFile2);
            mapQueue.add(csvFile3);
            mapQueue.add(csvFile4);
            mapQueue.add(csvFile5);
            int i = 1;
            File file = mapQueue.remove();
            mapQueue.add(file);

            List<List<String>> listArrayList = new ArrayList<>();

            while (!Validate.isEmpty(source)){

                source = source.replaceAll("\\r","");
                source = source.replaceAll("\\n","");

                List<String> list = CSVFileUtil.fromCSVLinetoArray(source);

                listArrayList.add(list);

                appendDate(file,list);
                if(i%1000==0){
                    file = mapQueue.remove();
                    listArrayList.clear();
                    mapQueue.add(file);
                }

                i++;

                source = csvFileUtil.readLine();
            }

            if(listArrayList.size() > 0){
                for (List<String> list:listArrayList) {

                    file = mapQueue.remove();
                    mapQueue.add(file);

                    appendDate(file,list);
                }
            }

            for (File f:mapQueue) {
                new Thread(new ImportDataRunable(f)).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
