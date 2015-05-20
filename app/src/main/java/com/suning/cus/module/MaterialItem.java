package com.suning.cus.module;

/**
 * Created by 14110105 on 2015/3/25.
 */
public class MaterialItem {

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 服务Id
     */
    private String serviceId;

    /**
     * 配件编码
     */
    private String materProduct;

    /**
     * 商品描述
     */
    private String materdesc;

    /**
     * 配件类型
     */
    private String materType;

    /**
     * 配件数量
     */
    private String materNumber;

    /**
     * 配件价格
     */
    private String materPrice;

    /**
     * 配件质保
     */
    private String materAssurance;

    /**
     * 配件批次
     */
    private String batch;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMaterProduct() {
        return materProduct;
    }

    public void setMaterProduct(String materProduct) {
        this.materProduct = materProduct;
    }

    public String getMaterNumber() {
        return materNumber;
    }

    public void setMaterNumber(String materNumber) {
        this.materNumber = materNumber;
    }

    public String getMaterPrice() {
        return materPrice;
    }

    public void setMaterPrice(String materPrice) {
        this.materPrice = materPrice;
    }

    public String getMaterAssurance() {
        return materAssurance;
    }

    public void setMaterAssurance(String materAssurance) {
        this.materAssurance = materAssurance;
    }

    public String getMaterType() {
        return materType;
    }

    public void setMaterType(String materType) {
        this.materType = materType;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMaterdesc() {
        return materdesc;
    }

    public void setMaterdesc(String materdesc) {
        this.materdesc = materdesc;
    }
}
