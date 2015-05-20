package com.suning.cus.event;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class DelFavEvent {

    public boolean isSuccess;

    public int position;

    public DelFavEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
        position = -1;
    }

    public DelFavEvent(boolean isSuccess, int position) {
        this.isSuccess = isSuccess;
        this.position = position;
    }

}
