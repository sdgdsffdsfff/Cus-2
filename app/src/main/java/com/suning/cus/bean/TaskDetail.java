package com.suning.cus.bean;

import com.suning.cus.json.JsonBase;

import java.io.Serializable;

public class TaskDetail extends JsonBase implements Serializable{
    private String serviceId; // 服务订单号
    private String clientName; // 用户姓名
    private String clientPhone;// 手机号码
    private String clientTele;// 固定电话
    private String clientAddress;// 顾客地址
    private String productDesc;// 商品名称
    private String productCode; // 电器商品ID
    private String serviceProduct;// 服务商品
    private String serviceOrderType;// 服务订单类型
    private String serviceOrderStatus;// 服务订单状态
    private String serviceOrderDesc;// 服务订单类型描述
    private String serviceOrg; //服务组织
    private String status;  // 任务已处理和未处理标识 0,1
    private String sendTime;// 送货日期
    private String bookTime;// 预约时间
    private String bespokeTime;// 预约排程
    private String remark;// 备注信息
    private String reasonRemark; // 备注（2） 销单次日、另约填写的备注
    private String isNCMedium;// 是否新冷媒
    private String qualityAssurance;// 质保标识
    private String previousDesc;// 上次派工信息
    private String desc;// 故障描述
    private String materTotalAmount; // 材配价格
    private String serviceAmount; // 服务费用
    private String amount; // 实收总计
    private String maintainMark;// 安维标识
    private String saleOrg;// 销售组织
    private String channel;// 渠道
    private String productLayer;// 产品层次
    private String orderPrior; // 订单优先级
    private String operateTime; // 送货完成时间


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientTele() {
        return clientTele;
    }

    public void setClientTele(String clientTele) {
        this.clientTele = clientTele;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getServiceProduct() {
        return serviceProduct;
    }

    public void setServiceProduct(String serviceProduct) {
        this.serviceProduct = serviceProduct;
    }

    public String getServiceOrderType() {
        return serviceOrderType;
    }

    public void setServiceOrderType(String serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public String getServiceOrderDesc() {
        return serviceOrderDesc;
    }

    public void setServiceOrderDesc(String serviceOrderDesc) {
        this.serviceOrderDesc = serviceOrderDesc;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReasonRemark() {
        return reasonRemark;
    }

    public void setReasonRemark(String reasonRemark) {
        this.reasonRemark = reasonRemark;
    }

    public String getIsNCMedium() {
        return isNCMedium;
    }

    public void setIsNCMedium(String isNCMedium) {
        this.isNCMedium = isNCMedium;
    }

    public String getQualityAssurance() {
        return qualityAssurance;
    }

    public void setQualityAssurance(String qualityAssurance) {
        this.qualityAssurance = qualityAssurance;
    }

    public String getPreviousDesc() {
        return previousDesc;
    }

    public void setPreviousDesc(String previousDesc) {
        this.previousDesc = previousDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMaterTotalAmount() {
        return materTotalAmount;
    }

    public void setMaterTotalAmount(String materTotalAmount) {
        this.materTotalAmount = materTotalAmount;
    }

    public String getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(String serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMaintainMark() {
        return maintainMark;
    }

    public void setMaintainMark(String maintainMark) {
        this.maintainMark = maintainMark;
    }

    public String getSaleOrg() {
        return saleOrg;
    }

    public void setSaleOrg(String saleOrg) {
        this.saleOrg = saleOrg;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProductLayer() {
        return productLayer;
    }

    public void setProductLayer(String productLayer) {
        this.productLayer = productLayer;
    }

    public String getOrderPrior() {
        return orderPrior;
    }

    public void setOrderPrior(String orderPrior) {
        this.orderPrior = orderPrior;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getServiceOrg() {
        return serviceOrg;
    }

    public void setServiceOrg(String serviceOrg) {
        this.serviceOrg = serviceOrg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
