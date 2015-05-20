package com.suning.cus.event;

import com.suning.cus.bean.QueryOrderData;

import java.util.List;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class QueryOrderEvent {

    public List<QueryOrderData> data;

    public QueryOrderEvent(List<QueryOrderData> data) {
        this.data = data;
    }
}
