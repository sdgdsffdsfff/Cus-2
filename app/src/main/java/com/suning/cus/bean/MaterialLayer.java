package com.suning.cus.bean;

/**
 * 材料配件层次
 * Created by 14110105 on 2015/3/18.
 */
public class MaterialLayer {

    /**
     * 产品层次(品牌)
     */
    private String materLayerCode;
    /**
     * 产品层次描述
     */
    private String materLayerDesc;

    public String getMaterLayerCode() {
        return materLayerCode;
    }

    public void setMaterLayerCode(String materLayerCode) {
        this.materLayerCode = materLayerCode;
    }

    public String getMaterLayerDesc() {
        return materLayerDesc;
    }

    public void setMaterLayerDesc(String materLayerDesc) {
        this.materLayerDesc = materLayerDesc;
    }
}
