package com.suning.cus.config;

/**
 * URL相关的常量
 * Created by 14110105 on 2015/3/9.
 */
public class ServerConfig {

    public static final String URL_LOCAL = "http://10.24.16.189:8080/assnew-web/";
    public static final String URL_DEV = "http://scsdev.cnsuning.com/ass/";
    public static final String URL_SIT = "http://asssit.cnsuning.com/asas/";
    public static final String URL_SIT_W = "http://103.255.94.234/asas/";
    public static final String URL_PRE = "http://asspre.cnsuning.com/asas/";
    public static final String URL_PRE_W = "http://103.255.94.235/asas/";
    public static final String URL_PRD = "http://snass.suning.com/asas/";

    /**
     * 版本升级接口
     */
    public static final String URL_UPDATE_SIT = "http://tumssit.cnsuning" + "" +
            ".com/tums-web/upgrade/queryVersion.action?devicetype=ASS";
    public static final String URL_UPDATE_PRE = "http://tumspre.cnsuning" + "" +
            ".com/tums-web/upgrade/queryVersion.action?devicetype=ASS";
    public static final String URL_UPDATE_PRD = "http://tums.suning" + "" +
            ".com/tums-web/upgrade/queryVersion.action?devicetype=ASS";

    public static final String BASE_URL = URL_PRE;
    public static final String URL_UPDATE = URL_UPDATE_PRD;

    /**
     * 用户登录接口
     */
    public static final String URL_LOGIN = BASE_URL + "asloginAction.do";

    /**
     * 获取任务清单接口
     */
    public static final String URL_TASKS_SUMMARY = BASE_URL +
            "asAndroidServerAppQueryIndexTaskList.do";

    /**
     * 获取任务列表接口
     */
    public static final String URL_TASKS_LIST = BASE_URL + "asAndroidServerAppQueryTaskList.do";

    /**
     * 获取任务详情
     */
    public static final String URL_TASK_DETAIL = BASE_URL + "asAndroidServerAppQueryTaskDetail.do";

    /**
     * 预约排程
     */
    public static final String URL_BESPOKE_TIME = BASE_URL + "asBespokeTime.do";

    /**
     * 销单—完成、次日、另约、拒收、其它
     */
    public static final String URL_DESTROY_BILL = BASE_URL + "destoryBill.do";

    /**
     * W库存管理-配件信息读取
     *
     * @author 15010551
     */
    public static final String URL_GET_W_INFO = BASE_URL + "asWDetails.do";

    /**
     * W库存管理-配件归还
     *
     * @author 15010551
     */
    public static final String URL_ACCESS_RETURN = BASE_URL + "asAccessoryReturn.do";

    /**
     * 材料配件层次查询
     */
    public static final String URL_QUERY_MATER_LAYER = BASE_URL +
            "asAndroidServerAppQueryMaterLayer.do";
    /**
     * 材料配件类目查询
     */
    public static final String URL_QUERY_MATER_CATEGORY = BASE_URL +
            "asAndroidServerAppQueryMaterCategory.do";
    /**
     * 商品编码查询
     */
    public static final String URL_QUERY_MATER_CODE = BASE_URL +
            "asAndroidServerAppQueryMaterCode.do";

    /**
     * 订单管理-订单详情
     * 材配订单详情
     *
     * @author 15010551
     */
    public static final String URL_QUERY_ACCESS_ORDER_DETAILS = BASE_URL +
            "asQueryAccessoryOrderDetails.do";

    /**
     * 故障,维护措施查询一，二，三 级目录接口以及未完成原因接口
     */
    public static final String URL_QUERY_MAINTAIN = BASE_URL + "asMaintainAction.do";

    /**
     * 销单&备货-电器型号和厂家型号检索
     */
    public static final String URL_QUERY_MODEL = BASE_URL + "asCommodity.do";

    /**
     * 销单&备货-电器型号查询配件
     */
    public static final String URL_QUERY_MODEL_ACCESSORY = BASE_URL + "asQueryCommodityAccessory" +
            ".do";

    /**
     * 查询延保信息
     */
    public static final String URL_QUERY_PROLONG_INSURANCE = BASE_URL +
            "asAndroidServerAppQueryProlongInsurance.do";

    /**
     * 初始密码强制修改功能、密码修改功能
     *
     * @author 15010551
     */
    public static final String URL_UPDATE_PWD = BASE_URL + "asUserUpdatePWDAction.do";

    /**
     * 查询作业人员的消息
     *
     * @author 15010551
     */
    public static final String URL_MESSAGE_GENERAL = BASE_URL + "asMessageGeneral.do";

    /**
     * 作业人员查询消息详情
     *
     * @author 15010551
     */
    public static final String URL_MESSAGE_DETAIL = BASE_URL + "asMessageDetail.do";

    /**
     * 删除消息
     *
     * @author 15010551
     */
    public static final String URL_MESSAGE_DELETE = BASE_URL + "asDeleteMessage.do";

    /**
     * 用户注销
     *
     * @author 15010551
     */
    public static final String URL_LOGOUT = BASE_URL + "asLogoutAction.do";

    /**
     * 订单占用查询
     *
     * @author 15010551
     */
    public static final String URL_W_DETAIL_ORDER = BASE_URL + "asWDetailOrderActive.do";

    /**
     * 材料价格查询
     */
    public static final String URL_QUERY_MATER_PRICE = BASE_URL + "asMaterPrice.do";

    /**
     * 配件价格查询
     *
     * @author 15010551
     */
    public static final String URL_QUERY_MATER_PRICE_NEW = BASE_URL + "asQueryMaterPrice.do";

    /**
     * 配件收藏读取
     *
     * @author 15010551
     */
    public static final String URL_FAVORITE = BASE_URL + "asFavorite.do";

    /**
     * 配件收藏取消
     *
     * @author 15010551
     */
    public static final String URL_DELETE_FAVORITE = BASE_URL + "asDeleteFavoriteParts.do";

    /**
     * 配件收藏
     *
     * @author 15010551
     */
    public static final String URL_ADD_FAVORITE = BASE_URL + "asAddFavoriteParts.do";

    /**
     * 订单管理-订单信息查询
     *
     * @author 15010551
     */
    public static final String URL_QUERY_ORDER = BASE_URL + "queryOrder.do";

    /**
     * 销单&备货-ATP检查
     *
     * @author 15010551
     */
    public static final String URL_ATP_CHECK = BASE_URL + "asAtpCheckAndQueryPrice.do";

    /**
     * 超库龄预警
     *
     * @author 15010551
     */
    public static final String URL_WARNING_MATER = BASE_URL + "asWarningMaterialAge.do";

}
