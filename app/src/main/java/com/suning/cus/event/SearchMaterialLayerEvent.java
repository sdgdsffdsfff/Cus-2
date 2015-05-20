package com.suning.cus.event;

import com.suning.cus.bean.MaterialLayer;

import java.util.List;

/**
 * 查询商品类目Event
 * Created by 14110105 on 2015/4/17.
 */
public class SearchMaterialLayerEvent {

    public List<MaterialLayer> layers;

    public SearchMaterialLayerEvent(List<MaterialLayer> layers) {
        this.layers = layers;
    }
}
