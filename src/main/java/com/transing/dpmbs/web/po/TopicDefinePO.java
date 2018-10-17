package com.transing.dpmbs.web.po;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

import java.util.Date;

public class TopicDefinePO {

    private String name;

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}