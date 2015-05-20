package com.suning.cus.json;

import com.suning.cus.bean.CommodityList;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/17.
 */
public class JsonAccessoryReturn extends  JsonBase {
    /**
     “employeeId”:” 88226409” (账户ID：唯一的标识)
     {
     "commodityList": [
     {
     "commodity": "配件编码",
     "commodityName": "商品描述",
     "plant": "地点",
     "batch": "批次",
     "commodityNumber": "数量",
     "unit": "配件单位",
     "price": "单价"
     }
     ]
     }
     */

    private String employeeId;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    private List<CommodityList> commodityList;

    public List<CommodityList> getCommodityList() {
        return commodityList;
    }

    public void setCommodityList(List<CommodityList> commodityList) {
        this.commodityList = commodityList;
    }

}
