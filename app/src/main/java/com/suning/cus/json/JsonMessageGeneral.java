package com.suning.cus.json;

import com.suning.cus.bean.MessageList;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/18.
 */
public class JsonMessageGeneral extends JsonBase {
    /**
    {"isSuccess":"S","totalPageNum":"1","pageSize":"15",
     "messageList":[
     {"id":"84","time":"20141114","title":"开例会俄国人有"}
     {"id":"84","time":"20141114","title":"开例会俄国人有"}
     ]}
    */

    /**
     * 总页数
     */
    private String totalPageNum;

    public String getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    /**
     * 每页显示几条
     */
    private String pageSize;

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 消息列表
     */
    private List<MessageList> messageList;

    public List<MessageList> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageList> messageList) {
        this.messageList = messageList;
    }
}
