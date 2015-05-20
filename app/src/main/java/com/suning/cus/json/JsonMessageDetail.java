package com.suning.cus.json;

/**
 * Created by 15010551 on 2015/3/18.
 */
public class JsonMessageDetail extends JsonBase {

    /**
     * {"isSuccess":"S","time":"20141114","content":"为4用途4i7","title":"开例会俄国人有"}
     */

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
     * 内容
     */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 标题
     */
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
