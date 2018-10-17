package com.transing.dpmbs.integration.bo;
/**
 * 可视化模板类
 * Created by byron on 2017/8/17 0017.
 */
public class VisTemplateBO {
    private String id;
    private String url;
    private String createTime;
    private String lastmodifyTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastmodifyTime() {
        return lastmodifyTime;
    }

    public void setLastmodifyTime(String lastmodifyTime) {
        this.lastmodifyTime = lastmodifyTime;
    }
}
