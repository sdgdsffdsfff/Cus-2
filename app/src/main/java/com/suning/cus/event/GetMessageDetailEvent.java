package com.suning.cus.event;

import com.suning.cus.json.JsonMessageDetail;

/**
 * Created by 15010551 on 2015/4/15.
 */
public class GetMessageDetailEvent {

    public JsonMessageDetail jsonMessageDetail;

    public GetMessageDetailEvent(JsonMessageDetail jsonMessageDetail) {
        this.jsonMessageDetail = jsonMessageDetail;
    }

}
