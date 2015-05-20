package com.suning.cus.constants;

/**
 * Created by 14110105 on 2015/3/11.
 */
public interface TaskConstants {

    /**
     * 任务的serviceId
     */
    public static final String TASK_SERVICE_ID = "serviceId";

    /**
     * 查询开始日期
     */
    public static final String TASK_DATE = "taskDate";
    /**
     * 查询结束日期
     */
    public static final String TASK_END_DATE = "taskEndDate";

    /**
     * 当前第几页
     */
    public static final String CURRENT_PAGE = "currentPage";

    /**
     * 每页显示条数
     */
    public static final String PAGE_SIZE = "pageSize";


    public static final String SERVICE_ORDER_TYPE = "serviceOrderType";

    /**
     * 查询状态:1-查询完成订单，2-查询未完成订单，空或不输入-所有订单
     */
    public static final String QUERY_STATUS = "queryStatus";


}
