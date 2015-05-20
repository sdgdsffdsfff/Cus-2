package com.suning.cus.event;

import my.android.state.BaseState;

/**
 * 请求失败Event
 * Created by 14110105 on 2015/4/15.
 */
public class RequestFailEvent extends BaseState.UiCausedEvent{

    public String message;

    public RequestFailEvent(String message) {
        super(-1);
        this.message = message;
    }

    public RequestFailEvent(int callingId, String message) {
        super(callingId);
        this.message = message;
    }


}
