package com.suning.cus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.R;
import com.suning.cus.adapter.WManageExpandableListAdapter;
import com.suning.cus.bean.ManageWDetailData;
import com.suning.cus.bean.ManageWDetailItem;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.ManageConstants;
import com.suning.cus.event.GetWDetailEvent;
import com.suning.cus.event.QualityAssuranceEvent;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.json.JsonQualityAssurance;
import com.suning.cus.json.JsonWDetail;
import com.suning.cus.logical.GetWDetailProcessor;
import com.suning.cus.logical.QualityAssuranceProcessor;
import com.suning.cus.logical.QueryPriceProcessor;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class WManageActivity extends BaseActivity implements View.OnClickListener {

    // Intent Bundle Key
    public static final String TYPE = "WManageActivityType";

    // Constant: 表示备货W库流程
    public static final String NORMAL = "Normal";

    // Constant: 表示任务跳转W库流程
    public static final String JUMP = "Jump";

    // Activity Request Code: 配件归还
    public static final int REQUEST_CODE_ACCESS_RETURN = 0x0001;

    // Handler Msg Code:
    public static final int HANDLER_CODE_W_DETAIL_ITEM = 0x0001;

    // Handler Msg Code:
    public static final int HANDLER_CODE_W_DETAIL_ORDER = 0x0002;

    // Handler Msg Code:
    public static final int HANDLER_CODE_RETURN_DATA = 0x0003;

    // Handler Msg Code:
    public static final int HANDLER_CODE_RETURN_PRICE = 0x0004;

    // Handler Bundle Key: 商品编码
    public static final String MATNR = "matnr";

    /**
     * 业务相关
     */
    private String getType;

    private Context mContext;

    private String materCode, materDesc, materPrice, materAllowCount, materBatch;

    //任务跳转的Intent值
    private String mQualityAssurance, mServiceOrg;

    private ArrayList<String> assurances;

    WManageExpandableListAdapter mAdapter;

    public static Handler handler;

    /**
     * UI相关
     */
    private ExpandableListView wManageExpandableListView;

    private ImageButton getWDetailButton;

    private TextView wDetailTextView;

    private TextView freedomLimitTextView, totalLimitTextView;

    //ListFooter
    Button loadMoreBtn;
    View loading;

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getType = getIntent().getStringExtra(WManageActivity.TYPE);

        if (getType.equals(JUMP)) {
            //setContentView(R.layout.activity_w_manage_task);
            mQualityAssurance = getIntent().getStringExtra("qualityAssurance");
            mServiceOrg = getIntent().getStringExtra("serviceOrg");
            setCustomContentView(R.layout.activity_w_manage_task);
        } else {
            //setContentView(R.layout.activity_w_manage);
            setCustomContentView(R.layout.activity_w_manage);
            setOpsButtonVisiable(View.VISIBLE);
            setOpsButtonText(getString(R.string.title_new_access_return));
            setOpsButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAdapter != null) {
                        Collection<ManageWDetailItem> c = mAdapter.getSelectedItems();
                        if (c.size() != 0) {
                            List<ManageWDetailItem> list = new ArrayList<ManageWDetailItem>();
                            for (ManageWDetailItem itemd : c) {
                                list.add(itemd);
                            }
                            Intent intent = new Intent();
                            intent.setClass(WManageActivity.this, FittingBackActivity.class);
                            intent.putExtra(FittingBackActivity.W_DETAIL_ITEMS, MyUtils.toByteArray(list));
                            //startActivity(intent);
                            startActivityForResult(intent, REQUEST_CODE_ACCESS_RETURN);
                        } else {
                            T.showShort(WManageActivity.this, getString(R.string.select_one_item_notice));
                        }
                        //    getIntentBy(FittingBackActivity.class);
                    } else {
                        T.showShort(WManageActivity.this, getString(R.string.select_one_item_notice));
                    }
                }
            });
        }

        mContext = WManageActivity.this;
        setTitle(getString(R.string.title_w_manage));


/*        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.fitting_ops, android.R.layout.simple_spinner_dropdown_item);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new android.support.v7.app.ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                return false;
            }
        });*/

        initView();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = null;
                Intent intent = null;

                switch (msg.what) {
                    case HANDLER_CODE_W_DETAIL_ITEM:
                        bundle = msg.getData();
                        //ManageWDetailItem item = (ManageWDetailItem) bundle.getSerializable(BaseConstants.W_DETAIL_ITEM_BUNDLE_KEY);
                        materCode = bundle.getString("material_code");
                        materDesc = bundle.getString("material_name");
                        materAllowCount = bundle.getString("material_allow_count");
                        materBatch = bundle.getString("material_batch");
                        requestQueryPrice(materCode);
                        break;
                    case HANDLER_CODE_W_DETAIL_ORDER:
                        bundle = msg.getData();
                        String matnr = bundle.getString(WManageActivity.MATNR);
                        //T.showShort(WManageActivity.this, matnr);
                        intent = new Intent();
                        intent.putExtra(OrderManageActivity.TYPE, OrderManageActivity.ORDER_ACTIVE);
                        intent.putExtra(OrderManageActivity.MATNR, matnr);
                        intent.setClass(WManageActivity.this, OrderManageActivity.class);
                        startActivity(intent);
                        break;
                    case HANDLER_CODE_RETURN_PRICE:
                        requestQualityAssurance();
                        break;
                    case HANDLER_CODE_RETURN_DATA:
                        intent = new Intent();
                        intent.putExtra("material_code", materCode);
                        intent.putExtra("material_name", materDesc);
                        intent.putExtra("material_price", materPrice);
                        intent.putExtra("material_allow_count", materAllowCount);
                        intent.putExtra("material_batch", materBatch);
                        intent.putExtra("material_assurance", assurances);
                        setResult(BaseConstants.W_MANAGE_RESULT_CODE, intent);
                        finish();
                        break;
                }
            }
        };
        EventBus.getDefault().register(this);
        requestGetWDetail("", "1");

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    private void requestGetWDetail(String cmmdtyCode, String currentPage) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(WManageActivity.this));
        params.addBodyParameter(ManageConstants.CMMDTY_CODE, cmmdtyCode);
        params.addBodyParameter(ManageConstants.PAGE_SIZE, "");
        params.addBodyParameter(ManageConstants.CURRENT_PAGE, currentPage);

        GetWDetailProcessor mProcessor = new GetWDetailProcessor(WManageActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_w_info));
        mProcessor.sendPostRequest();
    }

    public void onEvent(GetWDetailEvent event) {
        JsonWDetail jsonWDetail = event.jsonWDetail;
        totalLimitTextView.setText(jsonWDetail.getTotalLimit());
        freedomLimitTextView.setText(jsonWDetail.getFreedomLimit());

        List<ManageWDetailData> data = jsonWDetail.getData();

        // 将footerView重置
//                            setFooterViewNotLoading();
        if (data != null) {
            if (getType.equals(JUMP)) {
                //   Map<String, Integer> pMap = new HashMap<String, Integer>(); //新list中供应商和位置的map
//                                    pMap.put(data.get(0).getItem().get(0).getSupplier(), 0); //先将0号位的供应商编号放进map

                for (int i = 0; i < data.size(); i++) {
                    ManageWDetailData d = data.get(i);
                    List<ManageWDetailItem> newItems = new ArrayList<ManageWDetailItem>();
                    Map<String, Integer> pMap = new HashMap<String, Integer>();
                    for (int j = 0; j < d.getItem().size(); j++) {
                        ManageWDetailItem item = d.getItem().get(j);
                        if (pMap.containsKey(item.getSupplier())) {
                            int position = pMap.get(item.getSupplier());
                            String occupy = newItems.get(position).getOccupyCount();
                            String allow = newItems.get(position).getAllowCount();
                            //    String newOccupy = Integer.parseInt(occupy) + Integer.parseInt(item.getOccupyCount()) + "";
                            //    String newAllow = Integer.parseInt(allow) + Integer.parseInt(item.getAllowCount()) + "";
                            String newOccupy = MathUtils.count(occupy, item.getOccupyCount(), "+");
                            String newAllow = MathUtils.count(allow, item.getAllowCount(), "+");
                            newItems.get(position).setOccupyCount(newOccupy);
                            newItems.get(position).setAllowCount(newAllow);
                        } else {
                            int size = pMap.size();
                            pMap.put(item.getSupplier(), size);
                            ManageWDetailItem newItem = new ManageWDetailItem();
                            newItem.setSupplier(item.getSupplier());
                            newItem.setMaterialCode(item.getMaterialCode());
                            newItem.setAllowCount(item.getAllowCount());
                            newItem.setOccupyCount(item.getOccupyCount());
                            newItem.setMaterDesc(d.getMaterialDesc());
                            newItem.setMaterialAge("");
                            newItem.setPlant("");
                            newItem.setUnit("");
                            newItems.add(newItem);
                        }
                    }
                    data.get(i).setItem(newItems);
                }
            } else {    // W库管理，根据库龄从大到小将数据排列
                // 直接插入排序
                List<ManageWDetailData> newItems = new ArrayList<ManageWDetailData>();
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    int max = MyUtils.String2Int(data.get(0).getMaterialAge());
                    int maxPos = 0;
                    for (int j = 0; j < data.size(); j++) {
                        if (max < MyUtils.String2Int(data.get(j).getMaterialAge())) {
                            maxPos = j;
                            max = MyUtils.String2Int(data.get(j).getMaterialAge());
                        }
                    }
                    newItems.add(data.get(maxPos));
                    data.remove(maxPos);
                }
                data = newItems;
            }
            if (mAdapter == null) {
                mAdapter = new WManageExpandableListAdapter(WManageActivity.this, getType);
                mAdapter.appendDataList(data);
//                                    wManageExpandableListView.removeFooterView(footer);
//                                    if (CURRENT_PAGE < Integer.parseInt(jsonWDetail.getTotalPageNum())) {
//                                        wManageExpandableListView.addFooterView(footer);
//                                    } else {
//                                        T.showShort(WManageActivity.this, "没有更多数据了");
//                                    }
                wManageExpandableListView.setAdapter(mAdapter);
            } else {
                // 15010551
                // 该判断用于分页查询，目前的逻辑没有分页不会执行到这。保留此段代码，用于以后可能的修改。
                mAdapter.appendDataList(data);
//                                    if (CURRENT_PAGE >= Integer.parseInt(jsonWDetail.getTotalPageNum())) {
//                                        wManageExpandableListView.removeFooterView(footer);
//                                        T.showShort(WManageActivity.this, "没有更多数据了");
//                                    } else {
                //    wManageExpandableListView.addFooterView(footer);
//                                    }
                mAdapter.notifyDataSetChanged();
            }

            // 获取成功CURRENT_PAGE才加1
//                                CURRENT_PAGE += 1;
        } else {
            mAdapter = null;
            wManageExpandableListView.setAdapter(mAdapter);
            T.showShort(WManageActivity.this, getString(R.string.query_w_empty));
        }
    }

    public void onEvent(RequestFailEvent event) {
        T.showShort(WManageActivity.this, event.message);
    }

    private void initView() {

        wDetailTextView = (TextView) findViewById(R.id.tv_w_search);

        freedomLimitTextView = (TextView) findViewById(R.id.tv_freedom_limit);
        totalLimitTextView = (TextView) findViewById(R.id.tv_total_limit);

        getWDetailButton = (ImageButton) findViewById(R.id.bt_get_w_detail);
        getWDetailButton.setOnClickListener(this);

        wManageExpandableListView = (ExpandableListView) findViewById(R.id.lv_w_manage);

/*        manageInfoView = findViewById(R.id.ll_w_manage_info);
        if (getType.equals(JUMP)) {
            manageInfoView.setVisibility(View.GONE);
            manageListViewHeader = getLayoutInflater().inflate(R.layout.listview_header_w_manage_task, null);
        } else {
            manageListViewHeader = getLayoutInflater().inflate(R.layout.listview_header_w_manage, null);
        }
        wManageExpandableListView.addHeaderView(manageListViewHeader);*/

        //ListFooter
//        footer = getLayoutInflater().inflate(R.layout.listview_footer_w_manage, null);
//        loadMoreBtn = (Button)footer.findViewById(R.id.bt_load_more);
//        loading = footer.findViewById(R.id.ll_loading);
//        loadMoreBtn.setOnClickListener(this);
    }


    private void getIntentBy(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get_w_detail:
//                CURRENT_PAGE = 1;
                mAdapter = null;
                requestGetWDetail(wDetailTextView.getText().toString(), "1");
                break;
            case R.id.bt_load_more:
//                setFooterViewLoading();

//                getWDetail(wDetailTextView.getText().toString(), CURRENT_PAGE+1+"");
                break;
        }
    }

/*    private void setFooterViewLoading() {
        loadMoreBtn.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void setFooterViewNotLoading() {
        loadMoreBtn.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }*/


    /*
        */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACCESS_RETURN) {
            if (resultCode == RESULT_OK) {
                //15010551 配件归还成功返回W库页面时进行一次刷新
                mAdapter = null;
                requestGetWDetail("", "1");
            }
        }
    }

    private void requestQueryPrice(String mantrStr) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(WManageActivity.this));
        params.addBodyParameter(ManageConstants.MATER_CODES, mantrStr);

        QueryPriceProcessor mProcessor = new QueryPriceProcessor(WManageActivity.this, params, QueryPriceEvent.QUERY_PRICE_TYPE.W_MANAGE);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_price));
        mProcessor.sendPostRequest();
    }

    /**
     * 获取质保标识
     */
    private void requestQualityAssurance() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("materType", "Z011"); // 配件类型，固定C011
        params.addBodyParameter("materCode", materCode);
        params.addBodyParameter("qualityAssurance", mQualityAssurance);
        params.addBodyParameter("serviceOrg", mServiceOrg);

        QualityAssuranceProcessor processor = new QualityAssuranceProcessor(WManageActivity.this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(getString(R.string.search_quality_assurance));
        processor.sendPostRequest();
    }

    public void onEvent(QueryPriceEvent event) {
        if (event.type == QueryPriceEvent.QUERY_PRICE_TYPE.W_MANAGE) {
            if (event.isSuccess) {
                List<QueryMaterPriceData> priceDataList = event.priceDataList;
                if (priceDataList != null) {
                    materPrice = priceDataList.get(0).getMaterPrice();
                }
            }
            handler.sendEmptyMessage(HANDLER_CODE_RETURN_PRICE);
        }
    }

    public void onEvent(QualityAssuranceEvent event) {

        JsonQualityAssurance jsonQualityAssurance = event.jsonQualityAssurance;

        assurances = new ArrayList<>();

        if (!TextUtils.isEmpty(jsonQualityAssurance.getInsurance())) {
            assurances.add(jsonQualityAssurance.getInsurance());
        }

        if (!TextUtils.isEmpty(jsonQualityAssurance.getProlong())) {
            assurances.add(jsonQualityAssurance.getProlong());
        }

        if (assurances.size() > 0) {
            handler.sendEmptyMessage(HANDLER_CODE_RETURN_DATA);
        }
    }

}
