package com.suning.cus.event;

import com.suning.cus.bean.MaterialCategory;

import java.util.List;

/**
 * Created by 14110105 on 2015/4/18.
 */
public class SearchMaterialCategoryEvent {

    public List<MaterialCategory> categories;
    public String totalPageNumber;

    public SearchMaterialCategoryEvent(List<MaterialCategory> categories, String totalPageNumber) {
        this.categories = categories;
        this.totalPageNumber = totalPageNumber;
    }
}
