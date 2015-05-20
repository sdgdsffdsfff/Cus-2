package com.suning.cus.event;

import com.suning.cus.bean.Material;

/**
 * Created by 14110105 on 2015/4/18.
 */
public class AddFavEvent {
    public Material material;

    public AddFavEvent(Material material) {
        this.material = material;
    }
}
