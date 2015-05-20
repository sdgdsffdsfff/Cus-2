package com.suning.cus.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 查看订单详情需要用到的相关Map
 * Created by 15010551 on 2015/4/22.
 */
public class MapData {

    // 申请类型的map
    public static Map sourceTypeMap = new HashMap<String, String>(){{
        put("1", "配件申请");
        put("2", "新件归还");
        put("3", "旧件归还");
        put("4", "新件消耗");
        put("5", "新件购买");
        put("6", "配件超期");
    }};

    // 质保标识的map
    public static Map qualityAssMap = new HashMap<String, String>(){{
        put("01", "保内");
        put("02", "保外");
        put("03", "延保");
        put("04", "意外保");
    }};

    // 装运条件的map
    public static Map shipmentMap = new HashMap<String, String>(){{
        put("01", "配送");
        put("02", "自提");
    }};

    // 订单状态的map
    public static Map orderStatusMap = new HashMap<String, String>(){{
        put("A", "订单创建");
        put("B", "交货单创建");
        put("C", "送货完成");
        put("D", "送货改期");
        put("E", "送货取消");
        put("F", "交货单过账");
        put("G", "采购订单创建");
        put("H", "采购入库");
        put("M", "申请取消");
        put("N", "订单取消");
        //订单状态map默认为"配件操作申请"
    }};

}
