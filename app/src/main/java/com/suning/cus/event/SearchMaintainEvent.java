package com.suning.cus.event;

import com.suning.cus.bean.TaskMaintain;

import java.util.List;

/**
 * 故障、维修措施查询事件
 * Created by 14110105 on 2015/4/17.
 */
public class SearchMaintainEvent {
    public List<TaskMaintain> taskMaintains;

    public SearchMaintainEvent(List<TaskMaintain> taskMaintains) {
        this.taskMaintains = taskMaintains;
    }
}
