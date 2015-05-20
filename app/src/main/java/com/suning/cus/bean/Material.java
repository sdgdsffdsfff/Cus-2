package com.suning.cus.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 非配件类型
 * Created by 14110105 on 2015/3/17.
 */
public class Material implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 商品编码
     */
    private String materCode;
    /**
     * 商品描述
     */
    private String materDesc;
    /**
     * 商品类型物料类型
     */
    private String materType;

    /**
     * 配件批次
     */
    private String batch;

    /**
     * 商品价格
     */
    private String materPrice;

    /**
     * 商品数量
     */
    private String materNumber;

    private String store;

    /**
     * 选择后的质保标识
     */
    private String materAssurance;

    /**
     * 质保标识名称列表
     */
    private ArrayList<String> assuranceList;

    /**
     * 产品层次(品牌)
     */
    private String materCategoryCode;

    /**
     * 产品层次描述
     */
    private String materCategoryDesc;

    private boolean isFavorite = false;


    public String getMaterCode() {
        return materCode;
    }

    public void setMaterCode(String materCode) {
        this.materCode = materCode;
    }

    public String getMaterDesc() {
        return materDesc;
    }

    public void setMaterDesc(String materDesc) {
        this.materDesc = materDesc;
    }

    public String getMaterType() {
        return materType;
    }

    public void setMaterType(String materType) {
        this.materType = materType;
    }

    public String getMaterAssurance() {
        return materAssurance;
    }

    public void setMaterAssurance(String materAssurance) {
        this.materAssurance = materAssurance;
    }

    public String getMaterCategoryCode() {
        return materCategoryCode;
    }

    public void setMaterCategoryCode(String materCategoryCode) {
        this.materCategoryCode = materCategoryCode;
    }

    public String getMaterCategoryDesc() {
        return materCategoryDesc;
    }

    public void setMaterCategoryDesc(String materCategoryDesc) {
        this.materCategoryDesc = materCategoryDesc;
    }

    public String getMaterPrice() {
        return materPrice;
    }

    public void setMaterPrice(String materPrice) {
        this.materPrice = materPrice;
    }

    public String getMaterNumber() {
        return materNumber;
    }

    public void setMaterNumber(String materNumber) {
        this.materNumber = materNumber;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public ArrayList<String> getAssuranceList() {
        return assuranceList;
    }

    public void setAssuranceList(ArrayList<String> assuranceList) {
        this.assuranceList = assuranceList;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
