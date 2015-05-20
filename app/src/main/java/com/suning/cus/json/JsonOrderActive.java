package com.suning.cus.json;

import com.suning.cus.bean.OrderActiveData;
import com.suning.cus.bean.QueryOrderData;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/25.
 */
public class JsonOrderActive extends JsonBase {
    /**
     * {
     "isSuccess": "S",
     "data": [
     {
     "createDate": "建立日期",
     "material": "配件编码",
     "orderStatus": "订单状态",
     "targetQty": 数量,
     "vbeln": "订单编号"
     }
     ]
     }

     失败
     {
     "isSuccess": "E",
     "errorDesc": "错误信息"
     }

     */

    private List<QueryOrderData> data;

    public List<QueryOrderData> getData() {
        return data;
    }

    public void setData(List<QueryOrderData> data) {
        this.data = data;
    }
}
