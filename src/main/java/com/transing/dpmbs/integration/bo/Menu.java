package com.transing.dpmbs.integration.bo;

public class Menu {
    private int id;
    private String name;
    private String url;
    private int pid;
    private String indexNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getIndex() {
        return indexNum;
    }

    public void setIndex(String indexNum) {
        this.indexNum = indexNum;
    }
}
