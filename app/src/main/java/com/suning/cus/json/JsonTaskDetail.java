package com.suning.cus.json;

import com.suning.cus.bean.TaskDetail;

/**
 * Created by 14110105 on 2015/3/14.
 */
public class JsonTaskDetail extends JsonBase {

    private TaskDetail taskDetail;

    public TaskDetail getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(TaskDetail taskDetail) {
        this.taskDetail = taskDetail;
    }
}
