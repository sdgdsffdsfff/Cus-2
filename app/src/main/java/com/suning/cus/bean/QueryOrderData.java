package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class QueryOrderData {
    /**
     "data": [
     {
     "createDate": "建立日期",
     "material": "配件编码",
     "orderStatus": "订单状态",
     "targetQty": 数量,
     "vbeln": "订单编号"
     "serviceOrder": "服务订单号"
     }
     ]
     */

    private String createDate;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    private String material;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    private String orderStatus;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    private String targetQty;

    public String getTargetQty() {
        return targetQty;
    }

    public void setTargetQty(String targetQty) {
        this.targetQty = targetQty;
    }

    private String vbeln;

    public String getVbeln() {
        return vbeln;
    }

    public void setVbeln(String vbeln) {
        this.vbeln = vbeln;
    }

    private String serviceOrder;

    public String getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(String serviceOrder) {
        this.serviceOrder = serviceOrder;
    }
}
