package com.suning.cus.event;

import com.suning.cus.bean.Material;

/**
 * Created by 14110105 on 2015/4/18.
 */
public class ClickFavEvent {

    public boolean isAdd;
    public Material material;

    public ClickFavEvent(boolean isAdd, Material material) {
        this.isAdd = isAdd;
        this.material = material;
    }
}
