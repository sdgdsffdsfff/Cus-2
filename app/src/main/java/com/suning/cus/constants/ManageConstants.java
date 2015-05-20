package com.suning.cus.constants;

/**
 * Created by 11075539 on 2015/3/16.
 */
public interface ManageConstants {

    /**
       W库存管理-配件信息读取 接口字段
            employeeId 作业人员ID
            cmmdtyCode 商品编码/描述
            pageSize 每页个数
            currentPage 当前页数
     */
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String CMMDTY_CODE = "cmmdtyCode";
    public static final String PAGE_SIZE = "pageSize";
    public static final String CURRENT_PAGE = "currentPage";

    /**
     * W库存管理-配件归还 接口字段
     * “employeeId”:” 88226409” (账户ID：唯一的标识)
     */
    public static final String SOURCE_TYPE = "sourceType"; //配件归还，直接赋2
    public static final String COMMODITY_LIST = "commodityList";

    /**
     * W库存管理-单价查询 接口字段
     */
    public static final String MATER_CODES = "materCodes";

}
