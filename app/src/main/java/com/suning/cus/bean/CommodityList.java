package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/17.
 */
public class CommodityList {
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

    /**
     * 配件编码
     */
    private String commodity;

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    /**
     * 商品描述
     */
    private String commodityName;

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    /**
     * 地点
     */
    private String plant;

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * 批次
     */
    private String batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    /**
     * 数量
     */
    private String commodityNumber;

    public String getCommodityNumber() {
        return commodityNumber;
    }

    public void setCommodityNumber(String commodityNumber) {
        this.commodityNumber = commodityNumber;
    }

    /**
     * 配件单位
     */
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 单价
     */
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}

