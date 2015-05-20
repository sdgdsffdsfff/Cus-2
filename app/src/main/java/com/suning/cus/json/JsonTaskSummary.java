package com.suning.cus.json;

import com.suning.cus.bean.TaskSummary;

import java.util.ArrayList;

/**
 *
 * Created by 14110105 on 2015/3/11.
 */
public class JsonTaskSummary {

    /**
     * 成功失败标志
     */
    private String isSuccess;

    private ArrayList<TaskSummary> taskList;

    private String errorDesc;


    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ArrayList<TaskSummary> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<TaskSummary> taskList) {
        this.taskList = taskList;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

}
