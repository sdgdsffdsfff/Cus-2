package com.suning.cus.json;

/**
 * Created by 14110105 on 2015/3/31.
 */
public class JsonQualityAssurance extends JsonBase{

    /**
     * 是否保内编码
     */
    private String insurance;

    /**
     * 延期信息编码
     */
    private String prolong;

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getProlong() {
        return prolong;
    }

    public void setProlong(String prolong) {
        this.prolong = prolong;
    }
}
