package com.suning.cus.event;

import com.suning.cus.bean.AtpCheckResponseList;

import java.util.List;

/**
 * Created by 15010551 on 2015/4/16.
 */
public class AtpCheckEvent {

    public List<AtpCheckResponseList> atpCheckResponseList;

    public boolean isAtpCheckSucc;

    public AtpCheckEvent(List<AtpCheckResponseList> atpCheckResponseList, boolean isAtpCheckSucc) {
        this.atpCheckResponseList = atpCheckResponseList;
        this.isAtpCheckSucc = isAtpCheckSucc;
    }
}
