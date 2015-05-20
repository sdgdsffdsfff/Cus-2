package com.suning.cus.json;

import com.suning.cus.bean.QueryMaterPriceData;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/19.
 */
public class JsonQueryMaterPrice extends  JsonBase {

    private String price;

    private List<QueryMaterPriceData> data;

    public List<QueryMaterPriceData> getData() {
        return data;
    }

    public void setData(List<QueryMaterPriceData> data) {
        this.data = data;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
