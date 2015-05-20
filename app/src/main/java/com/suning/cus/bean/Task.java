package com.suning.cus.bean;

/**
 * 任务
 * Created by 14110105 on 2015/3/11.
 */
public class Task {

    /**
     * 服务订单
     */
    private String serviceId;
    /**
     * 顾客地址
     */
    private String clientAddress;
    /**
     * 服务商品
     */
    private String serviceProduct;
    /**
     * 预约时间
     */
    private String bookTime;
    /**
     * 预约排程时间
     */
    private String bespokeTime;

    /**
     * 任务状态 1-未读, 2-已读, 3-完成
     */
    private String status;

    /**
     * 销单状态 destoryStatus：E0002完成、E0003次日、E0004另约，E0005服务取消
     */
    private String destoryStatus;

    /**
     * 服务订单状态：E0006 服务完成、E0007 服务未完成、E0008 服务取消
     */
    private String serviceOrderStatus;

    /**
     * 是否为重要任务
     */
    private String redBpFlag;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getServiceProduct() {
        return serviceProduct;
    }

    public void setServiceProduct(String serviceProduct) {
        this.serviceProduct = serviceProduct;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getBespokeTime() {
        return bespokeTime;
    }

    public void setBespokeTime(String bespokeTime) {
        this.bespokeTime = bespokeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDestoryStatus() {
        return destoryStatus;
    }

    public void setDestoryStatus(String destoryStatus) {
        this.destoryStatus = destoryStatus;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public String getRedBpFlag() {
        return redBpFlag;
    }

    public void setRedBpFlag(String redBpFlag) {
        this.redBpFlag = redBpFlag;
    }
}
