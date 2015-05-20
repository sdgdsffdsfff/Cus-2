package com.suning.cus.event;

import com.suning.cus.json.JsonMaterial;

/**
 * Created by 14110105 on 2015/4/18.
 */
public class SearchMaterialEvent {
    public JsonMaterial jsonMaterial;

    public SearchMaterialEvent(JsonMaterial jsonMaterial) {
        this.jsonMaterial = jsonMaterial;
    }
}
