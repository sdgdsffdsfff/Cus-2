package com.suning.cus.json;

import com.suning.cus.bean.Material;

import java.util.List;

/**
 * Created by 14110105 on 2015/3/19.
 */
public class JsonMaterial extends JsonBase {


    protected String totalPageNum;

    protected String pageSize;

    protected List<Material> materialList;

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

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }
}
