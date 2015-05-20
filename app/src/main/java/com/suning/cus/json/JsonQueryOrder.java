package com.suning.cus.json;

import com.suning.cus.bean.QueryOrderData;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class JsonQueryOrder extends  JsonBase{

    private List<QueryOrderData> data;

    public List<QueryOrderData> getData() {
        return data;
    }

    public void setData(List<QueryOrderData> data) {
        this.data = data;
    }
}
