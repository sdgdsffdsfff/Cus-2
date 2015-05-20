package com.suning.cus.event;

import com.suning.cus.bean.Material;

/**
 * Created by 14110105 on 2015/4/16.
 */
public class MaterialDeleteEvent {

    public Material material;

    public MaterialDeleteEvent(Material material) {
        this.material = material;
    }
}
