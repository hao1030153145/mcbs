package com.transing.dpmbs.web.po;

import net.sf.json.JSONArray;

/**
 * Created by Administrator on 2017/12/7.
 */
public class DimensionActionPO {
    private String typeOf;
    private String action;
    private String actionName;
    private JSONArray actionProp;

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public JSONArray getActionProp() {
        return actionProp;
    }

    public void setActionProp(JSONArray actionProp) {
        this.actionProp = actionProp;
    }
}
