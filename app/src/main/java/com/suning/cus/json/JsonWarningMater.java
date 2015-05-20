package com.suning.cus.json;

/**
 * Created by 15010551 on 2015/4/10.
 */
public class JsonWarningMater extends JsonBase {
    /**
     {
     " isSuccess ": "S",
     "data": "您的商品**、**、**有部分已达到预警天数**天，请及时归还！"
     }
     如果没有快超期的商品data为空或不存在
     返回失败时：
     {
     “isSuccess”:”E”
     “errorDesc”:”” (失败原因描述)
     }
     */
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
