package com.suning.cus.event;

import com.suning.cus.bean.QueryMaterPriceData;

import java.util.List;

/**
 * Created by 15010551 on 2015/4/16.
 */
public class QueryPriceEvent {

    // 为避免event冲突，引入type。
    // 可把有用到此EVENT的流程加入此type。
    // 在创建Processor时传入。
    public static enum  QUERY_PRICE_TYPE {
        ACCESS_BACK, W_MANAGE, MY_FAV_BACKUP, TASK
    }

    public List<QueryMaterPriceData> priceDataList;

    public String price;

    public boolean isSuccess;

    public QUERY_PRICE_TYPE type;

    public QueryPriceEvent(List<QueryMaterPriceData> priceDataList, boolean isSuccess, QUERY_PRICE_TYPE type) {
        this.priceDataList = priceDataList;
        this.isSuccess = isSuccess;
        this.type = type;
    }

    public QueryPriceEvent(String price, boolean isSuccess, QUERY_PRICE_TYPE type) {
        this.price = price;
        this.isSuccess = isSuccess;
        this.type = type;
    }

}
