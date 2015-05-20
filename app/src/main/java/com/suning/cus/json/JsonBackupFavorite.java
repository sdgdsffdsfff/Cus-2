package com.suning.cus.json;

import com.suning.cus.bean.BackupFavData;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/19.
 */
public class JsonBackupFavorite extends JsonBase {

    private String totalPageNum;

    public String getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    private String pageSize;

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    private List<BackupFavData> data;

    public List<BackupFavData> getData() {
        return data;
    }

    public void setData(List<BackupFavData> data) {
        this.data = data;
    }
}
