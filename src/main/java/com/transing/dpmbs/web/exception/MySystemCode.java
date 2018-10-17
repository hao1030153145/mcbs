package com.transing.dpmbs.web.exception;

import com.jeeframework.webframework.exception.SystemCode;

/**
 * SystemCode
 * 系统错误编码枚举
 *
 * @author lance
 * @date 2016/3/21 0021
 */
public class MySystemCode extends SystemCode {

    public static final int CUSTOM_EXCEPTION = 1_10_14;
    public static final String CUSTOM_EXCEPTION_MESSAGE = "自定义错误!";

    static {
        errorMessageMap.put(CUSTOM_EXCEPTION, CUSTOM_EXCEPTION_MESSAGE);
    }

    public static final int JSONFORMAT_EXCEPTION = 1_10_15;
    public static final String JSONFORMAT_EXCEPTION_MESSAGE = "不是合法的JSON格式!";

    static {
        errorMessageMap.put(JSONFORMAT_EXCEPTION, JSONFORMAT_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_APPVERSION_EXCEPTION = 1_11_1;
    public static final String BIZ_APPVERSION_EXCEPTION_MESSAGE = "查询程序版本号不能为空!";

    static {
        errorMessageMap.put(BIZ_APPVERSION_EXCEPTION, BIZ_APPVERSION_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_SUBJECTAREAS_EXIST_EXCEPTION = 1_12_1;
    public static final String BIZ_SUBJECTAREAS_EXIST_EXCEPTION_MESSAGE = "选择的主题域已经被选择过了!请选择其他的吧!";

    static {
        errorMessageMap.put(BIZ_SUBJECTAREAS_EXIST_EXCEPTION, BIZ_SUBJECTAREAS_EXIST_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_DELETE_EXCEPTION = 1_11_1;
    public static final String BIZ_DELETE_EXCEPTION_MESSAGE = "删除失败!";

    static {
        errorMessageMap.put(BIZ_DELETE_EXCEPTION, BIZ_DELETE_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_CREATE_PROJECT = 1_11_2;
    public static final String BIZ_CREATE_PROJECT_MESSAGE = "项目名称已存在!";

    static {
        errorMessageMap.put(BIZ_CREATE_PROJECT, BIZ_CREATE_PROJECT_MESSAGE);
    }

    public static final int BIZ_START_PROJECT_EXCEPTION = 1_11_8;
    public static final String BIZ_START_PROJECT_EXCEPTION_MESSAGE = "项目启动失败!";

    static {
        errorMessageMap.put(BIZ_START_PROJECT_EXCEPTION, BIZ_START_PROJECT_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_START_PROJECT_CONFIG_EXCEPTION = 1_11_3;
    public static final String BIZ_START_PROJECT_CONFIG_EXCEPTION_MESSAGE = "项目启动失败!请先完善配置!";

    static {
        errorMessageMap.put(BIZ_START_PROJECT_CONFIG_EXCEPTION, BIZ_START_PROJECT_CONFIG_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_EXCEL_SIZE = 1_11_6;
    public static final String BIZ_EXCEL_SIZE_MESSAGE = "文件太大或者文件格式不正确";

    static {
        errorMessageMap.put(BIZ_EXCEL_SIZE, BIZ_EXCEL_SIZE_MESSAGE);
    }

    public static final int BIZ_DATA_QUERY_EXCEPTION = 1_11_4;
    public static final String BIZ_DATA_QUERY_EXCEPTION_MESSAGE = "没有查询到数据!";

    static {
        errorMessageMap.put(BIZ_DATA_QUERY_EXCEPTION, BIZ_DATA_QUERY_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_SUBJECTSET_EXIET_EXCEPTION = 1_11_5;
    public static final String BIZ_SUBJECTSET_EXIET_EXCEPTION_MESSAGE = "不能重复添加主题域!";

    static {
        errorMessageMap.put(BIZ_SUBJECTSET_EXIET_EXCEPTION, BIZ_SUBJECTSET_EXIET_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_WORKDSEG_EXIET_EXCEPTION = 1_11_7;
    public static final String BIZ_WORKDSEG_EXIET_EXCEPTION_MESSAGE = "不能重复添加分词设置!";

    static {
        errorMessageMap.put(BIZ_WORKDSEG_EXIET_EXCEPTION, BIZ_WORKDSEG_EXIET_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_PROJECT_NOT_COMPLETE_EXCEPTION = 1_11_8;
    public static final String BIZ_PROJECT_NOT_COMPLETE_EXCEPTION_MESSAGE = "项目未配置完成!请完善配置!";

    static {
        errorMessageMap.put(BIZ_PROJECT_NOT_COMPLETE_EXCEPTION, BIZ_PROJECT_NOT_COMPLETE_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_PARAMERRO_EXCEPTION = 1_12_1;
    public static final String BIZ_PARAMERRO_EXCEPTION_MESSAGE = "参数填写错误!";

    static {
        errorMessageMap.put(BIZ_PARAMERRO_EXCEPTION, BIZ_PARAMERRO_EXCEPTION_MESSAGE);
    }


    public static final int SAVEVIS_EXCEPTION = 1_20_9;
    public static final String SAVEVIS_EXCEPTION_MESSAGE = "保存信息失败";

    static {
        errorMessageMap.put(SAVEVIS_EXCEPTION, SAVEVIS_EXCEPTION_MESSAGE);
    }

    public static final int ACTION_EXCEPTION = 1_100_1;
    public static final String ACTION_EXCEPTION_MESSAGE = "操作失败";

    static {
        errorMessageMap.put(ACTION_EXCEPTION, ACTION_EXCEPTION_MESSAGE);
    }

    public static final int BIZ_CREATE_PROJECTEXPORT = 1_11_11;
    public static final String BIZ_CREATE_PROJECTEXPORT_MESSAGE = "文件名已存在!";

    static {
        errorMessageMap.put(BIZ_CREATE_PROJECTEXPORT, BIZ_CREATE_PROJECTEXPORT_MESSAGE);
    }

    public static final int BIZ_PROJECTEXPORT_FILENAME_NOALLOW = 1_11_12;
    public static final String BIZ_PROJECTEXPORT_FILENAME_NOALLOW_MESSAGE = "文件名不能有特殊字符!";

    static {
        errorMessageMap.put(BIZ_PROJECTEXPORT_FILENAME_NOALLOW, BIZ_PROJECTEXPORT_FILENAME_NOALLOW_MESSAGE);
    }

    public static final int BIZ_PROJECTNAMETOOLONG= 1_11_15;
    public static final String BIZ_PROJECTNAMETOOLONG_MESSAGE = "名称过长!";

    static {
        errorMessageMap.put(BIZ_PROJECTNAMETOOLONG, BIZ_PROJECTNAMETOOLONG_MESSAGE);
    }

    public static final int TOKEN_ERROR = 1_11_13;
    public static final String TOKEN_ERROR_MESSAGE = "token不匹配!";

    static {
        errorMessageMap.put(TOKEN_ERROR, TOKEN_ERROR_MESSAGE);
    }

    public static final int START_VISPROJECT_ERROR = 1_11_14;
    public static final String START_VISPROJECT_ERROR_MESSAGE = "启动项目失败，请检查该项目的工作流!";

    static {
        errorMessageMap.put(START_VISPROJECT_ERROR, START_VISPROJECT_ERROR_MESSAGE);
    }
}
