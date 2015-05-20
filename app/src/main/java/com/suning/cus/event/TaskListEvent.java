package com.suning.cus.event;

import com.suning.cus.json.JsonTaskList;
import com.suning.cus.logical.TaskListProcessor.QueryFilter;

/**
 * 任务列表Event
 * Created by 14110105 on 2015/4/13.
 */
public class TaskListEvent {

    public QueryFilter mFilter;

    public QueryFilter getFilter() {
        return mFilter;
    }

    public void setFilter(QueryFilter mFilter) {
        this.mFilter = mFilter;
    }

    /*数据对象*/
    public JsonTaskList mJsonTaskList;

    public TaskListEvent(JsonTaskList mJsonTaskList) {
        this.mJsonTaskList = mJsonTaskList;
    }
}
