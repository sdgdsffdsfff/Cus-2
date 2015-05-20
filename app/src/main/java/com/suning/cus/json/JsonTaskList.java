package com.suning.cus.json;

import com.suning.cus.bean.Task;

import java.util.List;

/**
 *
 * Created by 14110105 on 2015/3/11.
 */
public class JsonTaskList extends JsonBase {

    /**
     * 总任务数
     */
    private String totalTaskNum;

    /**
     * 总页数
     */
    private String totalPageNum;
    /**
     * 一页中加载多少个
     */
    private String pageSize;
    /**
     * 选择的日期
     */
    private String taskDate;

    /**
     * 取到的任务数
     */
    private String listNum;

    /**
     * 历史任务列表
     */
    private List<Task> list;


    public String getListNum() {
        return listNum;
    }

    public void setListNum(String listNum) {
        this.listNum = listNum;
    }

    public String getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

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

    public List<Task> getList() {
        return list;
    }

    public void setList(List<Task> list) {
        this.list = list;
    }
}
