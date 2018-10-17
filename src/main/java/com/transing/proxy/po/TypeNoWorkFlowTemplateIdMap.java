package com.transing.proxy.po;

/**
 *  typeNo和流程模板的映射类
 * Created by byron on 2018/4/11 0011.
 */
public enum  TypeNoWorkFlowTemplateIdMap {
    sina_live("sina_live",591),
    sina_weibo("sina_weibo",558),
    youku_live("youku_live",559),
    tencent_live("tencent_live",590),
    wangyi_live ("wangyi_live",546),
    yidian_live ("yidian_live",596);
    private String name;
    private int workFlowId;
    private TypeNoWorkFlowTemplateIdMap(String name,int workFlowId) {
        this.name = name;
        this.workFlowId = workFlowId;
    }
    public static Integer getWorkFlowId(String name) {
        for (TypeNoWorkFlowTemplateIdMap c : TypeNoWorkFlowTemplateIdMap.values()) {
            if (c.getName().equals(name)) {
                return c.workFlowId;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
