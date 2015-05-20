package com.suning.cus.event;

import com.suning.cus.bean.Material;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class MaterialDelFavEvent {


    public Material material;

    public MaterialDelFavEvent(Material material) {
        this.material = material;
    }
}
