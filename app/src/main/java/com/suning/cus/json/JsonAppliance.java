package com.suning.cus.json;

import com.suning.cus.bean.Appliance;

import java.util.List;

/**
 * 电器型号相关
 * Created by 14110105 on 2015/3/23.
 */
public class JsonAppliance extends JsonBase {


    private String totalPageNum;

    private String pageSize;

    private List<Appliance> materialList;

    private List<Appliance> data;

    public String getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public List<Appliance> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Appliance> materialList) {
        this.materialList = materialList;
    }

    public List<Appliance> getData() {
        return data;
    }

    public void setData(List<Appliance> data) {
        this.data = data;
    }
}
