package com.suning.cus.event;

import com.suning.cus.bean.QueryOrderDetail;
import com.suning.cus.json.JsonQueryAccessoryOrderDetails;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class QueryAccessDetailEvent {

    public QueryOrderDetail queryOrderDetail;

    public QueryAccessDetailEvent(QueryOrderDetail queryOrderDetail) {
        this.queryOrderDetail = queryOrderDetail;
    }

}
