package com.suning.cus.bean;

import java.util.List;

/**
 * Created by 11075539 on 2015/3/16.
 */
public class ManageWDetailData {
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
     * 是否高亮
     */
    private String highlight;

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    /**
     * item字段
     */
    private List<ManageWDetailItem> item;

    public List<ManageWDetailItem> getItem() {
        return item;
    }

    public void setItem(List<ManageWDetailItem> item) {
        this.item = item;
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

    public void setMaterialCode(String materialAge) {
        this.materialAge = materialAge;
    }

    /**
     * 配件描述
     */
    private String materialDesc;

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
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

}
