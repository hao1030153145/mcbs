package com.transing.dpmbs.web.po;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 */
public class TopicPO {
    private String projectName;
    private List<TopicDefinePO> topicList;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<TopicDefinePO> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<TopicDefinePO> topicList) {
        this.topicList = topicList;
    }
}
