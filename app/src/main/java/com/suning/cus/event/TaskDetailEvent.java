package com.suning.cus.event;

import com.suning.cus.bean.TaskDetail;

import my.android.state.BaseState;

/**
 * Created by 14110105 on 2015/4/16.
 */
public class TaskDetailEvent extends BaseState.UiCausedEvent{

    public TaskDetail data;

    public TaskDetailEvent(int callingId, TaskDetail data) {
        super(callingId);
        this.data = data;
    }

    public TaskDetailEvent(TaskDetail data) {
        this(-1, data);
    }
}
