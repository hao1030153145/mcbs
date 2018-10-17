package com.transing.workflow.constant;

import com.transing.dpmbs.integration.bo.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * @author lanceyan
 */
public class Constants {

    public static final String FILE_SEP = System.getProperty("file.separator");

    public static final String USER_HOME = System.getProperty("user.home") + FILE_SEP;

    public static final String ACTION = ".action";

    public static final  long COOKIE_MAX_AGE = 60 * 60 * 24 * 7 * 4L; // 默认cookie保存4个星期
    public static final  int COOKIE_TWO_YEAR_AGE = 60 * 60 * 24 * 365 * 2; // 两年的cookie
    public static final  int COOKIE_ONE_YEAR_AGE = 60 * 60 * 24 * 365 * 1;

    public static final String WEB_LOGIN_KEY = "WEIXIN_VERIFY_KEY";
    public static final String LINK_USER_COOKIE_LOGIN = "lk_sess";

    // 用于自动登录，后续采用分离式cache
    protected static final Map<String, User> SESSION_CACHE = new HashMap<>();


    /**
     * 用户注册来源常量
     */
    public static final int USER_REGISTE_FROM_WEIXIN = 1; //从微信来源注册
    public static final int USER_REGISTE_FROM_WEIBO = 2; //从微博哦来源注册
    public static final int USER_REGISTE_FROM_SNAPCHAT = 21; //从微信来源注册



    /**
     * 字段注释
     * session存放的用户对象key值
     */
    public static final String WITH_SESSION_USER = "withSessionUser";
    /**
     * 字段注释
     * cookie自动登陆保存key
     */
    public static final String LOGIN_COOKIE_SIGN = "with_cookie_sign";

    /**
     * 后台系统的高亮菜单id
     */
    public static final String BOSS_MENU_ID = "trans_boss_menu_id";

    /**
     * 字段注释
     * request 对象里的userbo数据
     */
    public static final String REQUEST_USERBO = "user";

    /**
     * 字段注释
     * 登陆验证秘钥key
     */
    public static final String LOGIN_KEY = "WITH_LOGIN_VERIFY_KEY";

    private Constants() {
        throw new IllegalAccessError("Constants class");
    }

    public static final String WORK_FLOW_TYPE_NO_DATAIMPORT = "dataImport";
    public static final String WORK_FLOW_TYPE_NO_DATACRAWL = "dataCrawl";
    public static final String WORK_FLOW_TYPE_NO_DATA_M_CRAWL = "dataMcrawl";
    public static final String WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT = "semanticAnalysisObject";//语义分析
    public static final String WORK_FLOW_TYPE_NO_WORDSEGMENTATION = "wordSegmentation";//分词
    public static final String WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING = "themeAnalysisSetting";
    public static final String WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION = "topicAnalysisDefinition";
    public static final String WORK_FLOW_TYPE_NO_DATAOUTPUT = "dataOutput";
    public static final String WORK_FLOW_TYPE_NO_FILEOUTPUT = "fileOutput";
    public static final String WORK_FLOW_TYPE_NO_STATISTICAL = "statisticalAnalysis";
    public static final String WORK_FLOW_TYPE_NO_DATAFILTER = "dataFilter";
    public static final String WORK_FLOW_TYPE_NO_CONDITION = "condition";
    public static final String WORK_FLOW_TYPE_NO_PUShOSS = "pushOSS";
    public static final String PARAM_TYPE = "analysisLevel";

    public final static int JOB_RESULT_TYPE_SENTENCE = 17;
    public final static int JOB_RESULT_TYPE_SECTION = 18;
    public final static int JOB_RESULT_TYPE_ARTICLE = 19;

    /**
     * dpmss传递数据的key
     */
    public static final String DATA_TYPE_KEY = "dataType";
    public static final String DATA_IDS_KEY = "idArray";

    public static final String DATA_TYPE_SENTENCE = "sentence";
    public static final String DATA_TYPE_SECTION = "section";
    public static final String DATA_TYPE_ARTICLE = "article";

    /**
     * kafka定义的topic
     */
    public static final String KAFKA_CRAWL_TOPIC = "crawlTopic";
    public static final String KAFKA_M_CRAWL_TOPIC = "mCrawlTopic";
    public static final String KAFKA_DPMSS_TOPIC = "dpmss_topic";
    public static final String KAFKA_DPMBS_TOPIC = "dpmbs_topic";
    public static final String TOKEN = "transingAdmin";

}