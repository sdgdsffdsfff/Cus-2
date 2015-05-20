package com.suning.cus.bean;

import java.io.Serializable;

/**
 * Created by 15010551 on 2015/3/16.
 */
public class ManageWDetailItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     [onSuccess:150]
     {"pageSize":"15",
     "totalPageNum":"1",
     "freedomLimit":0,
     "totalLimit":0,
     "data":[
     {"allowCount":54,"item":[
     {"allowCount":13,"materialAge":15,"materialCode":"CMMDTY_CODE1","occupyCount":7,"plant":"K025","supplier":"0001","unit":"MEASURE_UNIT1111"},
     {"allowCount":5,"materialAge":14,"materialCode":"CMMDTY_CODE1","occupyCount":0,"plant":"K025","supplier":"0001","unit":"MEASURE_UNIT1111"},
     {"allowCount":6,"materialAge":15,"materialCode":"CMMDTY_CODE1","occupyCount":0,"plant":"K025","supplier":"0001","unit":"MEASURE_UNIT1111"},
     {"allowCount":10,"materialAge":14,"materialCode":"CMMDTY_CODE1","occupyCount":0,"plant":"K025","supplier":"0001","unit":"MEASURE_UNIT1111"},
     {"allowCount":10,"materialAge":15,"materialCode":"CMMDTY_CODE1","occupyCount":0,"plant":"K025","supplier":"0002","unit":"MEASURE_UNIT1111"},
     {"allowCount":10,"materialAge":14,"materialCode":"CMMDTY_CODE1","occupyCount":0,"plant":"K025","supplier":"0002","unit":"MEASURE_UNIT1111"}
     ],"materialAge":15,"materialCode":"CMMDTY_CODE1","materialDesc":"CMMDTY_NAME1","occupyCount":7}],"isSuccess":"S"}
     */

    /**
     * 可用数量
     */
    private String allowCount;

    public String getAllowCount() {
        return allowCount;
    }

    public void setAllowCount(String allowCount) {
        this.allowCount = allowCount;
    }

    /**
     * 库龄
     */
    private String materialAge;

    public String getMaterialAge() {
        return  materialAge;
    }

    public void setMaterialAge(String materialAge) {
        this.materialAge = materialAge;
    }

    /**
     * 配件编码
     */
    private String materialCode;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    /**
     * 占用数量
     */
    private String occupyCount;

    public String getOccupyCount() {
        return occupyCount;
    }

    public void setOccupyCount(String occupyCount) {
        this.occupyCount = occupyCount;
    }

    /**
     * 供应商
     */
    private String supplier;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /**
     * 地点
     */
    private String plant;

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * 配件单位
     */
    private String unit;

    public String getUnit() {
        return  unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String materDesc;

    public String getMaterDesc() {
        return materDesc;
    }

    public void setMaterDesc(String materDesc) {
        this.materDesc = materDesc;
    }
}
