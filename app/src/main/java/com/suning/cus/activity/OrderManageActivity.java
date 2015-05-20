package com.suning.cus.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.OrderManageListAdapter;
import com.suning.cus.bean.QueryOrderData;
import com.suning.cus.constants.ManageConstants;
import com.suning.cus.constants.OrderConstants;
import com.suning.cus.event.OrderActiveEvent;
import com.suning.cus.event.QueryOrderEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.QueryOrderActiveProcessor;
import com.suning.cus.logical.QueryOrderProcessor;
import com.suning.cus.utils.DateTimeUtils;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class OrderManageActivity extends BaseActivity {

    public static final String TYPE = "type";
    public static final String ORDER_MANAGE = "orderManage"; //订单管理
    public static final String ORDER_ACTIVE = "orderActive"; //订单占用查询
    public static final String MATNR = "matnr";

    public static final int HANDLER_CODE_ORDER_DETAIL = 0x0001;
    public static final String VBELN = "vbeln";
    public static final String SERVICE_ORDER = "serviceOrder";
    public static final String MATERIAL = "material";

    public static Handler handler;

    private String activityType;
    private String matnr; //在订单占用查询时才会通过intent传过来

    private Spinner typeSpinner;
    //    private Spinner dateSpinner;
    private ListView orderManageListView;
    private OrderManageListAdapter mAdapter;
    private AnimationAdapter mAnimAdapter;

    private DatePickerDialog.OnDateSetListener mDateListener1;
    private DatePickerDialog.OnDateSetListener mDateListener2;
    private String mStartDate = null;
    private String mEndDate = null;
    private String mStartDateSearch = null, mEndDateSearch = null;
    private Calendar mCalendar;
    private SimpleDateFormat dateFormat;

    private LinearLayout orderManageGridLayout;

    private TextView orderVbelnTextView;
    private TextView mDateStartView;
    private TextView mDateEndView;

    private String sourceTypeStr = "1"; //默认为1，配件申请单

    private ProgressDialog progressDialog;

    private Map<String, String> sourceTypeMap = new HashMap<String, String>();

    public static final int ORDER_CHECKED_STATUS_MY_ORDER = 0x0001;

    public static final int ORDER_CHECKED_STATUS_UNFINISH_ORDER = 0x0002;

    // 订单列表默认显示“订单未生成”
    private int orderCheckedStatus = ORDER_CHECKED_STATUS_UNFINISH_ORDER;

    private List<QueryOrderData> myOrder = null;
    private List<QueryOrderData> unfinishOrder = null;

    private View myOrderHeaderView;
    private TextView orderDesc;
    private View myOrderUnderLine;
    private View unfinishUnderLine;
    private TextView unfinishOrderNumTextView;

    class OrderInfo {

        String orderId;
        String wareMatnr;
        String wareNum;
        String orderStatus;
        String orderDate;

        public OrderInfo(String orderId, String wareMatnr, String wareNum, String orderStatus, String orderDate) {
            this.orderId = orderId;
            this.wareMatnr = wareMatnr;
            this.wareNum = wareNum;
            this.orderStatus = orderStatus;
            this.orderDate = orderDate;
        }
    }

    ;

    ArrayList<OrderInfo> orderInfos = new ArrayList<OrderInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_manage);
        setCustomContentView(R.layout.activity_order_manage);
        initView();

        EventBus.getDefault().register(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_CODE_ORDER_DETAIL:
                        String vbeln = msg.getData().getString(OrderManageActivity.VBELN);
                        String material = msg.getData().getString(OrderManageActivity.MATERIAL);
                        String serviceOrder = msg.getData().getString(OrderManageActivity.SERVICE_ORDER);
                        Intent intent = new Intent();
                        intent.setClass(OrderManageActivity.this, QueryAccessOrderDetailsActivity.class);
                        intent.putExtra(QueryAccessOrderDetailsActivity.VBELN, vbeln);
                        intent.putExtra(QueryAccessOrderDetailsActivity.MATERIAL, material);
                        intent.putExtra(QueryAccessOrderDetailsActivity.SERVICE_ORDER, serviceOrder);
                        intent.putExtra(QueryAccessOrderDetailsActivity.START_DATE, mStartDateSearch);
                        intent.putExtra(QueryAccessOrderDetailsActivity.END_DATE, mEndDateSearch);
                        startActivity(intent);
                        break;
                }
            }
        };

        activityType = getIntent().getStringExtra(OrderManageActivity.TYPE);
        if (activityType.equals(OrderManageActivity.ORDER_MANAGE)) {
            setTitle(getString(R.string.title_order_manage));
        } else if (activityType.equals(OrderManageActivity.ORDER_ACTIVE)) {
            setTitle(getString(R.string.title_order_active));
            orderManageGridLayout = (LinearLayout) findViewById(R.id.gl_order_manager);
            orderManageGridLayout.setVisibility(View.GONE);
            matnr = getIntent().getStringExtra(OrderManageActivity.MATNR);
            queryOrderActive(matnr);
        } else {
            finish();
        }

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

        typeSpinner = (Spinner) findViewById(R.id.spinner_type);
//        dateSpinner = (Spinner)findViewById(R.id.spinner_date);
        orderManageListView = (ListView) findViewById(R.id.lv_order_manage);

        mDateStartView = (TextView) findViewById(R.id.tv_date_start);
        mDateEndView = (TextView) findViewById(R.id.tv_date_end);
        orderVbelnTextView = (TextView) findViewById(R.id.tv_order_vbeln);

        mDateListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mStartDate = dateFormat.format(calendar.getTime());
                mDateStartView.setText(mStartDate);
//                mDateEndView.setEnabled(true);

            }
        };

        mDateListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mEndDate = dateFormat.format(calendar.getTime());
                mDateEndView.setText(mEndDate);
            }
        };

        final String typeSpinnerStings[] = getResources().getStringArray(R.array.spinner_type_strings);
        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeSpinnerStings);
        sourceTypeMap.put("配件申请", "1");
        sourceTypeMap.put("新件归还", "2");
        sourceTypeMap.put("超期购买", "6");
        sourceTypeMap.put("旧件归还", "3");
        sourceTypeMap.put("新件消耗", "4");
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourceTypeStr = sourceTypeMap.get(typeSpinnerStings[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String dateSpinnerStrings[] = getResources().getStringArray(R.array.spinner_date_strings);
        ArrayAdapter<String> dateSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateSpinnerStrings);
//        dateSpinner.setAdapter(dateSpinnerAdapter);

        mCalendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mDateStartView.setText(dateFormat.format(mCalendar.getTime()));
        mDateEndView.setText(dateFormat.format(mCalendar.getTime()));

        myOrderHeaderView = findViewById(R.id.lv_header_order_manage);
        orderDesc = (TextView) myOrderHeaderView.findViewById(R.id.tv_order_desc);
        myOrderUnderLine = findViewById(R.id.view_my_order_under_line);
        unfinishUnderLine = findViewById(R.id.view_unfinish_under_line);
        unfinishOrderNumTextView = (TextView) findViewById(R.id.tv_unfinished_order_num);
        unfinishOrderNumTextView.setVisibility(View.GONE);
    }


    /**
     * 显示时间选择框
     *
     * @param v 被点击的View
     */
    public void showDatePickDialog(View v) {
        DatePickerDialog dialog;
        if (v.getId() == R.id.tv_date_start) {
            dialog = new DatePickerDialog(this, mDateListener1, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            dialog = new DatePickerDialog(this, mDateListener2, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }

        DatePicker picker = dialog.getDatePicker();

//        picker.setMaxDate(mCalendar.getTimeInMillis());
        dialog.show();

    }

    private void requestQueryOrder(String vbeln, String createDate, String endDate, String sourceType) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(OrderConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OrderManageActivity.this));
        params.addBodyParameter(OrderConstants.VBELN, vbeln);
        params.addBodyParameter(OrderConstants.SOURCE_TYPE, sourceType);
        params.addBodyParameter(OrderConstants.CREATE_DATE, createDate);
        params.addBodyParameter(OrderConstants.END_DATE, endDate);

        mStartDateSearch = createDate;
        mEndDateSearch = endDate;

        QueryOrderProcessor mProcessor = new QueryOrderProcessor(OrderManageActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_order));
        mProcessor.sendPostRequest();

    }

    public void onEvent(QueryOrderEvent event) {
        List<QueryOrderData> data = event.data;
        if (data == null) {
            T.showShort(OrderManageActivity.this, getString(R.string.query_order_error));
            if (mAdapter != null) {
                myOrder = null;
                unfinishOrder = null;
                mAdapter.clearDataList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                unfinishOrderNumTextView.setText("0");
                unfinishOrderNumTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            myOrder = new ArrayList<QueryOrderData>();
            unfinishOrder = new ArrayList<QueryOrderData>();
            // 15010551 根据订单编号是否为空分离成“我的订单”和“未完成订单”
            for (int pos = 0; pos < data.size(); pos++) {
                QueryOrderData item = data.get(pos);
                if (TextUtils.isEmpty(item.getVbeln())) {    // 为完成订单
                    unfinishOrder.add(item);
                } else { // 我的订单
                    myOrder.add(item);
                }
            }
            // 对“我的订单”和“未完成订单”按照订单编号和服务订单号进行从大到小的排序
            unfinishOrder = sortOrderList(unfinishOrder, SERVICE_ORDER);
            myOrder = sortOrderList(myOrder, VBELN);

            if (mAdapter == null) {
                mAdapter = new OrderManageListAdapter(OrderManageActivity.this);
            } else {
                mAdapter.clearDataList();
            }
            if (orderCheckedStatus == ORDER_CHECKED_STATUS_MY_ORDER) {
                mAdapter.setDataList(myOrder);
            } else {
                mAdapter.setDataList(unfinishOrder);
            }

            // 显示未处理订单的总数
            unfinishOrderNumTextView.setText(String.valueOf(unfinishOrder.size()));
            unfinishOrderNumTextView.setVisibility(View.VISIBLE);

            mAdapter.setOrderCheckedStatus(orderCheckedStatus);

            mAnimAdapter = new SwingRightInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(orderManageListView);
            orderManageListView.setAdapter(mAnimAdapter);
            //    orderManageListView.setAdapter(mAdapter);
        }
    }

    public void onEvent(RequestFailEvent event) {
        T.showShort(OrderManageActivity.this, event.message);
    }

    public void queryOrderClick(View v) {
        String vbeln = orderVbelnTextView.getText().toString();
        String startDate = mDateStartView.getText().toString();
        String endDate = mDateEndView.getText().toString();
        String sourceType = sourceTypeStr;

        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            if (start.getTime() > end.getTime()) {
                T.showShort(OrderManageActivity.this, getString(R.string.date_order_error));
            } else if (DateTimeUtils.getDateMargin(endDate, startDate) > 7) {
                T.showShort(this, R.string.toast_beyond_date);
            } else {
                requestQueryOrder(vbeln, startDate, endDate, sourceType);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            T.showShort(OrderManageActivity.this, getString(R.string.date_range_error));
        }


    }

    private void queryOrderActive(String cmmdtyCode) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OrderManageActivity.this));
        params.addBodyParameter(ManageConstants.CMMDTY_CODE, cmmdtyCode);

        QueryOrderActiveProcessor mProcessor = new QueryOrderActiveProcessor(OrderManageActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_order_active));
        mProcessor.sendPostRequest();
    }

    public void onEvent(OrderActiveEvent event) {
        List<QueryOrderData> data = event.data;
        if (data == null) {
            T.showShort(OrderManageActivity.this, getString(R.string.query_order_error));
            if (mAdapter != null) {
                myOrder = null;
                unfinishOrder = null;
                mAdapter.clearDataList();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                unfinishOrderNumTextView.setText("0");
                unfinishOrderNumTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            myOrder = new ArrayList<QueryOrderData>();
            unfinishOrder = new ArrayList<QueryOrderData>();
            // 15010551 根据订单编号是否为空分离成“我的订单”和“未完成订单”
            for (int pos = 0; pos < data.size(); pos++) {
                QueryOrderData item = data.get(pos);
                if (TextUtils.isEmpty(item.getVbeln())) {    // 为完成订单
                    unfinishOrder.add(item);
                } else { // 我的订单
                    myOrder.add(item);
                }
            }

            // 对“我的订单”和“未完成订单”按照订单编号和服务订单号进行从大到小的排序
            unfinishOrder = sortOrderList(unfinishOrder, SERVICE_ORDER);
            myOrder = sortOrderList(myOrder, VBELN);

            if (mAdapter == null) {
                mAdapter = new OrderManageListAdapter(OrderManageActivity.this);
            } else {
                mAdapter.clearDataList();
            }
            if (orderCheckedStatus == ORDER_CHECKED_STATUS_MY_ORDER) {
                mAdapter.setDataList(myOrder);
            } else {
                mAdapter.setDataList(unfinishOrder);
            }

            // 显示未处理订单的总数
            unfinishOrderNumTextView.setText(String.valueOf(unfinishOrder.size()));
            unfinishOrderNumTextView.setVisibility(View.VISIBLE);

            mAdapter.setOrderCheckedStatus(orderCheckedStatus);

            mAnimAdapter = new SwingRightInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(orderManageListView);
            orderManageListView.setAdapter(mAnimAdapter);
            //    orderManageListView.setAdapter(mAdapter);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_order:
                switchToMyOrder();
                break;
            case R.id.tv_unfinish_order:
                switchToUnfinish();
                break;
        }
    }

    private void switchToMyOrder() {
        orderCheckedStatus = ORDER_CHECKED_STATUS_MY_ORDER;
        orderDesc.setText(getString(R.string.order_num));
        myOrderUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
        unfinishUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
        if (mAdapter != null) {
            mAdapter.setOrderCheckedStatus(orderCheckedStatus);
            mAdapter.setDataList(myOrder);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void switchToUnfinish() {
        orderCheckedStatus = ORDER_CHECKED_STATUS_UNFINISH_ORDER;
        orderDesc.setText(getString(R.string.service_order_num));
        myOrderUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
        unfinishUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
        if (mAdapter != null) {
            mAdapter.setOrderCheckedStatus(orderCheckedStatus);
            mAdapter.setDataList(unfinishOrder);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 对订单list按照订单编号或服务订单号进行排序
     *
     * @param dataList
     * @param listType 标示是订单列表还是服务订单列表
     */
    private List<QueryOrderData> sortOrderList(List<QueryOrderData> dataList, String listType) {
        // 直接插入排序
        List<QueryOrderData> newList = new ArrayList<QueryOrderData>();
        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            long max;
            if (listType.equals(VBELN)) {
                max = MyUtils.String2Long(dataList.get(0).getVbeln());
            } else {
                max = MyUtils.String2Long(dataList.get(0).getServiceOrder());
            }
            int maxPos = 0;
            for (int j = 0; j < dataList.size(); j++) {
                long tmp;
                if (listType.equals(VBELN)) {
                    tmp = MyUtils.String2Long(dataList.get(j).getVbeln());
                } else {
                    tmp = MyUtils.String2Long(dataList.get(j).getServiceOrder());
                }
                if (max < tmp) {
                    maxPos = j;
                    max = tmp;
                }
            }
            newList.add(dataList.get(maxPos));
            dataList.remove(maxPos);
        }
        return newList;
    }

}
