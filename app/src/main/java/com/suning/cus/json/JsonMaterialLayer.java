package com.suning.cus.json;

import com.suning.cus.bean.MaterialLayer;

import java.util.List;

/**
 * Created by 14110105 on 2015/3/18.
 */
public class JsonMaterialLayer extends JsonBase {

    /**
     * 总页数
     */
    private String totalPageNum;
    /**
     * 一页中加载多少个
     */
    private String pageSize;

    private List<MaterialLayer> materialList;

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

    public List<MaterialLayer> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<MaterialLayer> materialList) {
        this.materialList = materialList;
    }
}
