package com.transing.dpmbs.integration.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/7 0007.
 */
public class CustomMap<K,V>  extends HashMap implements Map{
    @Override
    public boolean equals(Object o) {
        if(o==null){
            return false;
        }
        CustomMap map = (CustomMap)o;

        if(this.get("key").equals(map.get("key"))){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
