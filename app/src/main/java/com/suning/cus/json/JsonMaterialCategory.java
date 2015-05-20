package com.suning.cus.json;

import com.suning.cus.bean.MaterialCategory;

import java.util.List;

/**
 * Created by 14110105 on 2015/3/19.
 */
public class JsonMaterialCategory extends JsonBase {

    private String materCategoryDesc;

    protected String totalPageNum;

    protected String pageSize;

    private List<MaterialCategory> materialList;


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

    public String getMaterCategoryDesc() {
        return materCategoryDesc;
    }

    public void setMaterCategoryDesc(String materCategoryDesc) {
        this.materCategoryDesc = materCategoryDesc;
    }

    public List<MaterialCategory> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<MaterialCategory> materialList) {
        this.materialList = materialList;
    }
}
