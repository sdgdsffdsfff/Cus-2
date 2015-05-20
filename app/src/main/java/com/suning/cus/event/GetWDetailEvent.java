package com.suning.cus.event;

import com.suning.cus.json.JsonWDetail;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class GetWDetailEvent {
    public JsonWDetail jsonWDetail;

    public GetWDetailEvent(JsonWDetail jsonWDetail) {
        this.jsonWDetail = jsonWDetail;
    }

}
