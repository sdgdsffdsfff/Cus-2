package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/25.
 */
public class CommodityListATP {

    /**
     * "commodityList":[{
     "commodity"：配件编码
     "commodityNumber"：数量
     },
     {
     "commodity"：配件编码
     "commodityNumber"：数量
     }]
     */

    private String commodity;

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    private String commodityNumber;

    public String getCommodityNumber() {
        return commodityNumber;
    }

    public void setCommodityNumber(String commodityNumber) {
        this.commodityNumber = commodityNumber;
    }
}
