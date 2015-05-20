package com.suning.cus.json;

import com.suning.cus.bean.ManageWDetailData;

import java.util.List;

/**
 * Created by 11075539 on 2015/3/16.
 */
public class JsonWDetail extends JsonBase {
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
     * 每页个数
     */
    private String pageSize;

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 总页数
     */
    private String totalPageNum;

    public String getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    /**
     * 可用额度
     */
    private String freedomLimit;

    public String getFreedomLimit() {
        return freedomLimit;
    }

    public void setFreedomLimit(String freedomLimit) {
        this.freedomLimit = freedomLimit;
    }

    /**
     * 总额度
     */
    private String totalLimit;

    public String getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(String totalLimit) {
        this.totalLimit = totalLimit;
    }

    private List<ManageWDetailData> data;

    public List<ManageWDetailData> getData() {
        return data;
    }

    public void setData(List<ManageWDetailData> data) {
        this.data = data;
    }

}
