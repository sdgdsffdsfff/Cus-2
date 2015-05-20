package com.suning.cus.json;

import com.suning.cus.bean.QueryOrderData;
import com.suning.cus.bean.QueryOrderDetail;
import com.suning.cus.bean.QueryOrderDetailItemList;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class JsonQueryAccessoryOrderDetails extends JsonBase {
    /**
     * {
     "orderDetail": {
     "createDate": "记录建立日期",
     "createTime": "记录建立时间",
     "description": "商品描述",
     "material": "商品",
     "partnerNo": "作业人员工号",
     "qualityassurance": "质保标识",
     "serviceOrder": "服务订单",
     "sourceType": "申请类型",
     "vbeln": "销售订单",
     "itemList": [
     {
     "batch": "批次",
     "brandCode": "产品层次",
     "buzei": "凭证行项目",
     "cmmdtyPrice": "销售价格",
     "logisticsLog": "物流状态",
     "messageDesc": "场景",
     "orderStatus": "状态",
     "shipment": "装运条件",
     "targetQty": "数量",
     "targetQu": "单位",
     "updateDa": "更新日期"
     },
     {
     "batch": "批次",
     "brandCode": "产品层次",
     "buzei": "凭证行项目",
     "cmmdtyPrice": "销售价格",
     "logisticsLog": "物流状态",
     "messageDesc": "场景",
     "orderStatus": "状态",
     "shipment": "装运条件",
     "targetQty": "数量",
     "targetQu": "单位",
     "updateDa": "更新日期"
     }
     ]
     }
     }
     */

    private QueryOrderDetail orderDetail;

    public QueryOrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(QueryOrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }


}
