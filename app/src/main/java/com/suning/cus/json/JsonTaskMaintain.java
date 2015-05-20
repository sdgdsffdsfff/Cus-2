package com.suning.cus.json;

import com.suning.cus.bean.TaskMaintain;

import java.util.List;

/**
 * Created by 14110105 on 2015/3/23.
 */
public class JsonTaskMaintain extends JsonBase {

    private String totalPageNum;

    private String pageSize;

    private List<TaskMaintain> categoryOneList;

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

    public List<TaskMaintain> getCategoryOneList() {
        return categoryOneList;
    }

    public void setCategoryOneList(List<TaskMaintain> categoryOneList) {
        this.categoryOneList = categoryOneList;
    }
}
