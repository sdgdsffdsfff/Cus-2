package com.suning.cus.event;

import com.suning.cus.bean.Appliance;

import java.util.List;

/**
 * 电器信号查询Event
 * Created by 14110105 on 2015/4/17.
 */
public class ApplianceTypeEvent {
    public List<Appliance> appliances;

    public ApplianceTypeEvent(List<Appliance> appliances) {
        this.appliances = appliances;
    }
}
