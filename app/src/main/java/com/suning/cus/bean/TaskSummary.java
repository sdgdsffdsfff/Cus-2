package com.suning.cus.bean;

/**
 * 日期相关的任务总结
 * Created by 14110105 on 2015/3/11.
 */
public class TaskSummary {

    /**
     * 服务器日期
     */
    private String taskDate;

    /**
     * 总任务数
     */
    private String totalTaskNum;

    /**
     * 新增任务数
     */
    private String newTaskNum;

    /**
     * 未完成任务数
     */
    private String unfinishedTaskNum;



    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTotalTaskNum() {
        return totalTaskNum;
    }

    public void setTotalTaskNum(String totalTaskNum) {
        this.totalTaskNum = totalTaskNum;
    }

    public String getNewTaskNum() {
        return newTaskNum;
    }

    public void setNewTaskNum(String newTaskNum) {
        this.newTaskNum = newTaskNum;
    }

    public String getUnfinishedTaskNum() {
        return unfinishedTaskNum;
    }

    public void setUnfinishedTaskNum(String unfinishedTaskNum) {
        this.unfinishedTaskNum = unfinishedTaskNum;
    }

}
