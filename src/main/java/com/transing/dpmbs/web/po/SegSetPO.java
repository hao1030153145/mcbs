package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summer on 2017/4/25.
 */
@ApiModel(value = "分词设置  对象")
public class SegSetPO extends WorkFlowParamBasePO{

    @ApiModelProperty("操作类型 0为不用新词，1为自动判断")
    private int actionType;

    @ApiModelProperty("选择的词库 list")
    private List<WordLibraryPO> wordLibraryList = new ArrayList<>();

    public List<WordLibraryPO> getWordLibraryList() {
        return wordLibraryList;
    }

    public void setWordLibraryList(List<WordLibraryPO> wordLibraryList) {
        this.wordLibraryList = wordLibraryList;
    }

    public void setActionType(int actionType){
        this.actionType = actionType;
    }

    public int getActionType(){
        return this.actionType;
    }
}
