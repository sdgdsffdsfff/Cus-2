package com.suning.cus.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.AccessOrderDetailListAdapter;
import com.suning.cus.bean.QueryOrderDetail;
import com.suning.cus.bean.QueryOrderDetailItemList;
import com.suning.cus.constants.MapData;
import com.suning.cus.constants.OrderConstants;
import com.suning.cus.event.QueryAccessDetailEvent;
import com.suning.cus.logical.QueryAccessDetailProcessor;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.List;

import de.greenrobot.event.EventBus;

public class QueryAccessOrderDetailsActivity extends BaseActivity {

//    @ViewInject(R.id.lv_access_order_details)

    // Intent Bundle Key(in): 订单号
    public static final String VBELN = "vbeln";

    // Intent Bundle Key(in): 服务订单号
    public static final String SERVICE_ORDER = "serviceOrder";

    // Intent Bundle Key(in): 配件编码
    public static final String MATERIAL = "material";

    // Intent Bundle Key(in): 起始日期
    public static final String START_DATE = "startDate";

    // Intent Bundle Key(in): 终止日期
    public static final String END_DATE = "endDate";

    /**
     * 业务相关
     */
    private String vbeln;

    private String material;

    private String serviceOrder;

    private AccessOrderDetailListAdapter mAdapter;

    private AnimationAdapter mAnimAdapter;

    private String mStartDate;

    private String mEndDate;

/*    // 申请类型的map
    private Map<String, String> sourceTypeMap = new HashMap<String, String>();

    // 质保标识的map
    private Map<String, String> qualityAssMap = new HashMap<String, String>();

    // 装运条件的map
    private Map<String, String> shipmentMap = new HashMap<String, String>();

    // 订单状态的map
    private Map<String, String> orderStatusMap = new HashMap<String, String>();*/

    /**
     * UI相关
     */
    private ListView accessOrderDetailsListView;

    private TextView totalPayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_query_access_order_details);
        setTitle(getString(R.string.title_access_order_detail));
        vbeln = getIntent().getStringExtra(QueryAccessOrderDetailsActivity.VBELN);
        material = getIntent().getStringExtra(QueryAccessOrderDetailsActivity.MATERIAL);
        serviceOrder = getIntent().getStringExtra(QueryAccessOrderDetailsActivity.SERVICE_ORDER);
        mStartDate = getIntent().getStringExtra(QueryAccessOrderDetailsActivity.START_DATE);
        if (mStartDate == null) {
            mStartDate = "";
        }
        mEndDate = getIntent().getStringExtra(QueryAccessOrderDetailsActivity.END_DATE);
        if (mEndDate == null) {
            mEndDate = "";
        }

        initView();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        queryAccessDetail(vbeln, material, serviceOrder, mStartDate, mEndDate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView() {
        accessOrderDetailsListView = (ListView) findViewById(R.id.lv_access_order_details);
        totalPayTextView = (TextView) findViewById(R.id.tv_order_total_payment);
    }

    /**
     * 查看订单详情
     *
     * @param vbeln
     * @param material
     * @param serviceOrder
     */
    private void queryAccessDetail(String vbeln, String material, String serviceOrder, String startDate, String endDate) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(OrderConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(QueryAccessOrderDetailsActivity.this));
        params.addBodyParameter(OrderConstants.VBELN, vbeln);
        params.addBodyParameter(OrderConstants.MATERIAL, material);
        params.addBodyParameter(OrderConstants.SERVICE_ORDER, serviceOrder);
        params.addBodyParameter(OrderConstants.CREATE_DATE, startDate);
        params.addBodyParameter(OrderConstants.END_DATE, endDate);

        QueryAccessDetailProcessor mProcessor = new QueryAccessDetailProcessor(QueryAccessOrderDetailsActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_order_detail));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收订单详情的Event
     *
     * @param event
     */
    public void onEvent(QueryAccessDetailEvent event) {
        /**
         * 获取订单信息，写入listview header
         */
        QueryOrderDetail queryOrderDetail = event.queryOrderDetail;
        View headerView = getLayoutInflater().inflate(R.layout.listview_header_access_order_details, null);
        ((TextView) headerView.findViewById(R.id.tv_parter_no)).setText(queryOrderDetail.getPartnerNo());
        ((TextView) headerView.findViewById(R.id.tv_vbeln)).setText(queryOrderDetail.getVbeln());
        if (MapData.sourceTypeMap != null) {
            ((TextView) headerView.findViewById(R.id.tv_source_type)).setText((CharSequence) MapData.sourceTypeMap.get(queryOrderDetail.getSourceType()));
        } else {
            ((TextView) headerView.findViewById(R.id.tv_source_type)).setText(queryOrderDetail.getSourceType());
        }
        ((TextView) headerView.findViewById(R.id.tv_material)).setText(queryOrderDetail.getMaterial());
        if (MapData.qualityAssMap != null) {
            ((TextView) headerView.findViewById(R.id.tv_qualityassurance)).setText((CharSequence) MapData.qualityAssMap.get(queryOrderDetail.getQualityassurance()));
        } else {
            ((TextView) headerView.findViewById(R.id.tv_qualityassurance)).setText(queryOrderDetail.getQualityassurance());
        }
        ((TextView) headerView.findViewById(R.id.tv_serviceOrder)).setText(queryOrderDetail.getServiceOrder());
        ((TextView) headerView.findViewById(R.id.tv_createDate)).setText(queryOrderDetail.getCreateDate());
        ((TextView) headerView.findViewById(R.id.tv_createTime)).setText(queryOrderDetail.getCreateTime());
        ((TextView) headerView.findViewById(R.id.tv_mater_desc)).setText(queryOrderDetail.getShortTextForSo());
        accessOrderDetailsListView.addHeaderView(headerView);

        /**
         * 获取订单分步具体信息
         */
        List<QueryOrderDetailItemList> queryOrderDataList = queryOrderDetail.getItemList();
        if (queryOrderDataList == null) {
            T.showShort(QueryAccessOrderDetailsActivity.this, getString(R.string.query_order_detail_error));
        } else {
            if (queryOrderDataList.size() > 0) {
                String totalPriceStr = "0";
                for (int i = 0; i < queryOrderDataList.size(); i++) {
                    QueryOrderDetailItemList item = queryOrderDataList.get(i);
                    String priceStr = item.getCmmdtyPrice();
                    String cmmdtyNumStr = item.getTargetQty();
                    if (priceStr.isEmpty()) priceStr = "0";
                    if (cmmdtyNumStr.isEmpty()) cmmdtyNumStr = "0";
                    totalPriceStr = MathUtils.count(totalPriceStr, MathUtils.count(priceStr, cmmdtyNumStr, "*"), "+");
                }
                totalPayTextView.setText(totalPriceStr);
                mAdapter = new AccessOrderDetailListAdapter(QueryAccessOrderDetailsActivity.this);
                mAdapter.setShipmentMap(MapData.shipmentMap);
                mAdapter.setOrderStatusMap(MapData.orderStatusMap);
                mAdapter.setDataList(queryOrderDataList);

                mAnimAdapter = new AlphaInAnimationAdapter(mAdapter);
                mAnimAdapter.setAbsListView(accessOrderDetailsListView);
                accessOrderDetailsListView.setAdapter(mAnimAdapter);

                //    accessOrderDetailsListView.setAdapter(mAdapter);
            } else {
                T.showShort(QueryAccessOrderDetailsActivity.this, getString(R.string.query_order_detail_error));
            }
        }
    }
}
