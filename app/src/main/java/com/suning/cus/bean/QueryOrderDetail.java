package com.suning.cus.bean;

import java.util.List;

/**
 * Created by 11075539 on 2015/3/20.
 */
public class QueryOrderDetail {
    /**
     *      "createDate": "记录建立日期",
     "createTime": "记录建立时间",
     "description": "商品描述",
     "material": "商品",
     "partnerNo": "作业人员工号",
     "qualityassurance": "质保标识",
     "serviceOrder": "服务订单",
     "sourceType": "申请类型",
     "vbeln": "销售订单",
     */

    private String createDate;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private String material;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    private String partnerNo;

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    private String qualityassurance;

    public String getQualityassurance() {
        return qualityassurance;
    }

    public void setQualityassurance(String qualityassurance) {
        this.qualityassurance = qualityassurance;
    }

    private String serviceOrder;

    public String getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(String serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    private String vbeln;

    public String getVbeln() {
        return vbeln;
    }

    public void setVbeln(String vbeln) {
        this.vbeln = vbeln;
    }

    private List<QueryOrderDetailItemList> itemList;

    public List<QueryOrderDetailItemList> getItemList() {
        return itemList;
    }

    public void setItemList(List<QueryOrderDetailItemList> itemList) {
        this.itemList = itemList;
    }

    private String shortTextForSo;

    public String getShortTextForSo() {
        return shortTextForSo;
    }

    public void setShortTextForSo(String shortTextForSo) {
        this.shortTextForSo = shortTextForSo;
    }

}
