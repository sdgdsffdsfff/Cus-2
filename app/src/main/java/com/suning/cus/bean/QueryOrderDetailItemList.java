package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class QueryOrderDetailItemList {

    /**
     *      "itemList": [
     {
     "batch": "批次",
     "brandCode": "产品层次",
     "buzei": "凭证行项目",
     "cmmdtyPrice": "销售价格",
     "logisticsLog": "物流状态",
     "messageDesc": "场景",
     "orderStatus": "状态",
     "shipment": "装运条件",
     "targetQty": "数量",
     "targetQu": "单位",
     "updateDa": "更新日期"
     "description": "备注"
     },
     */

    private String batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    private String brandCode;

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    private String buzei;

    public String getBuzei() {
        return buzei;
    }

    public void setBuzei(String buzei) {
        this.buzei = buzei;
    }

    private String cmmdtyPrice;

    public String getCmmdtyPrice() {
        return cmmdtyPrice;
    }

    public void setCmmdtyPrice(String cmmdtyPrice) {
        this.cmmdtyPrice = cmmdtyPrice;
    }

    private String logisticsLog;

    public String getLogisticsLog() {
        return logisticsLog;
    }

    public void setLogisticsLog(String logisticsLog) {
        this.logisticsLog = logisticsLog;
    }

    private String messageDesc;

    public String getMessageDesc() {
        return messageDesc;
    }

    public void setMessageDesc(String messageDesc) {
        this.messageDesc = messageDesc;
    }

    private String orderStatus;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    private String shipment;

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    private String targetQty;

    public String getTargetQty() {
        return targetQty;
    }

    public void setTargetQty(String targetQty) {
        this.targetQty = targetQty;
    }

    private String targetQu;

    public String getTargetQu() {
        return targetQu;
    }

    public void setTargetQu(String targetQu) {
        this.targetQu = targetQu;
    }

    private String updateDa;

    public String getUpdateDa() {
        return updateDa;
    }

    public void setUpdateDa(String updateDa) {
        this.updateDa = updateDa;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
