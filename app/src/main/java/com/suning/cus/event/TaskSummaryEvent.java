package com.suning.cus.event;

import com.suning.cus.bean.TaskSummary;

import java.util.ArrayList;

/**
 * 任务Summary列表Event
 * Created by 14110105 on 2015/4/13.
 */
public class TaskSummaryEvent {

    /*数据 对象*/
    public ArrayList<TaskSummary> mTasks;

    public TaskSummaryEvent(ArrayList<TaskSummary> tasks) {
        this.mTasks = tasks;
    }
}
