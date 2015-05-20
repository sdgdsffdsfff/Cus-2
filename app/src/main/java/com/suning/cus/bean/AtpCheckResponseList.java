package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/30.
 */
public class AtpCheckResponseList {
    /**
     * {"atpCheckResponseList":[
     *
     * {"atpResult":"",
     * "batch":"0060000056",
     * "commodity":"P011000045",
     * "commodityNumber":"2.000",
     * "freedomCount":"0.0",
     * "plant":"K025",
     * "price":"11.00",
     * "total":"1.0",
     * "unit":"S01",
     * "usedCount":"1.0"},
     *
     * {"atpResult":"","batch":"0060000020","commodity":"P011000046","commodityNumber":"3.000","freedomCount":"0.0","plant":"K025","price":"120.00","total":"0.0","unit":"S01","usedCount":"0.0"}],
     *
     * "isSuccess":"S"}
     */

    /**
     * 到货时间（当前状态）
     */
    private String atpResult;

    public String getAtpResult() {
        return atpResult;
    }

    public void setAtpResult(String atpResult) {
        this.atpResult = atpResult;
    }

    /**
     * 批次（供应商）
     */
    private String batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

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
     * 数量
     */
    private String commodityNumber;

    public String getCommodityNumber() {
        return commodityNumber;
    }

    public void setCommodityNumber(String commodityNumber) {
        this.commodityNumber = commodityNumber;
    }

    private String freedomCount;

    public String getFreedomCount() {
        return freedomCount;
    }

    public void setFreedomCount(String freedomCount) {
        this.freedomCount = freedomCount;
    }

    private String plant;

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String usedCount;

    public String getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(String usedCount) {
        this.usedCount = usedCount;
    }


}
