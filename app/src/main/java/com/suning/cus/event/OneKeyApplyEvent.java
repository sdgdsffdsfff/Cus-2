package com.suning.cus.event;

import com.suning.cus.bean.Appliance;

import java.util.List;

/**
 * 一键申请Event
 * Created by 14110105 on 2015/4/17.
 */
public class OneKeyApplyEvent {
    public List<Appliance> appliances;

    public OneKeyApplyEvent(List<Appliance> appliances) {
        this.appliances = appliances;
    }
}
