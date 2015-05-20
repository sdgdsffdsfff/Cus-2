package com.suning.cus.event;

import com.suning.cus.bean.MessageList;
import com.suning.cus.json.JsonMessageGeneral;

import java.util.List;

/**
 * Created by 15010551 on 2015/4/15.
 */
public class MessageGeneralEvent {

    public JsonMessageGeneral jsonMessageGeneral;

    public MessageGeneralEvent(JsonMessageGeneral jsonMessageGeneral) {
        this.jsonMessageGeneral = jsonMessageGeneral;
    }
}
