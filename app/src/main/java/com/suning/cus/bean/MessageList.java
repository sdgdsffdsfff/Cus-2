package com.suning.cus.bean;

/**
 * Created by 11075539 on 2015/3/18.
 */
public class MessageList {
    /**
     {"isSuccess":"S","totalPageNum":"1","pageSize":"15",
     "messageList":[
     {"id":"84","time":"20141114","title":"开例会俄国人有"}
     {"id":"84","time":"20141114","title":"开例会俄国人有"}
     ]}
     */

    /**
     * 消息ID
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 时间
     */
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 消息名称
     */
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 状态：标记已读和未读
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
