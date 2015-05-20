package com.suning.cus.bean;

/**
 * Created by 15010551 on 2015/3/25.
 */
public class OrderActiveData {

    /**
     * {
     "isSuccess": "S",
     "data": [
     {
     "createDate": "建立日期",
     "material": "配件编码",
     "orderStatus": "订单状态",
     "targetQty": 数量,
     "vbeln": "订单编号"
     }
     ]
     }

     失败
     {
     "isSuccess": "E",
     "errorDesc": "错误信息"
     }

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
}
