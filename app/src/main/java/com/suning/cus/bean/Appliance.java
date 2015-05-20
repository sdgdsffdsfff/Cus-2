package com.suning.cus.bean;

/**
 * 电器信号
 * Created by 14110105 on 2015/3/23.
 */
public class Appliance {

    /**
     * 电器型号
     */
    private String cmmdtyCode;
    /**
     * 电器名称/描述
     */
    private String cmmdtyName;
    /**
     * 厂家型号
     */
    private String supplierCode;

    /**
     * 配件物料号
     */
    private String commodity;

    /**
     * 配件商品名称
     */
    private String productNm;

    public String getCmmdtyCode() {
        return cmmdtyCode;
    }

    public void setCmmdtyCode(String cmmdtyCode) {
        this.cmmdtyCode = cmmdtyCode;
    }

    public String getCmmdtyName() {
        return cmmdtyName;
    }

    public void setCmmdtyName(String cmmdtyName) {
        this.cmmdtyName = cmmdtyName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getProductNm() {
        return productNm;
    }

    public void setProductNm(String productNm) {
        this.productNm = productNm;
    }
}
