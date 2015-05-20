package com.suning.cus.event;

import com.suning.cus.json.JsonUser;

/**
 * Created by 14110105 on 2015/4/13.
 */
public class LoginEvent {

    public final JsonUser jsonUser;

    public LoginEvent(JsonUser jsonUser) {
        this.jsonUser = jsonUser;
    }
}
