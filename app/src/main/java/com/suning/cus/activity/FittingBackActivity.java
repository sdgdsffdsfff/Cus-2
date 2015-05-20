package com.suning.cus.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.FittingBackListAdapter;
import com.suning.cus.bean.CommodityList;
import com.suning.cus.bean.ManageWDetailItem;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.constants.ManageConstants;
import com.suning.cus.event.AccessoryReturnEvent;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.AccessoryReturnProcessor;
import com.suning.cus.logical.QueryPriceProcessor;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 配件归还页面
 * 15010551
 */
public class FittingBackActivity extends BaseActivity {

    // Intent Bundle Key (in): 选中的W库商品
    public static final String W_DETAIL_ITEMS = "wDetailItems";

    // Handler Msg Code：删除item
    public static final int HANDLER_CODE_DEL_ITEM = 0x0001;

    // Handler Msg Code：获取价格
    public static final int HANDLER_CODE_QUERY_PRICE = 0x0002;

    /**
     * 业务相关
     */
    public static Handler handler;

    private FittingBackListAdapter mAdapter;

    private AnimationAdapter mAnimAdapter;

    private ArrayList<ManageWDetailItem> manageWDetailItems = null;

    private Map<String, String> materPriceMap = new HashMap<String, String>(); //K商品编码，V价格

    /**
     * UI相关
     */
    private ListView fittingBackListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_fitting_back);
        setTitle(getString(R.string.title_access_return));

        manageWDetailItems = (ArrayList<ManageWDetailItem>) MyUtils.toObject(getIntent().getByteArrayExtra(W_DETAIL_ITEMS));
        if (manageWDetailItems == null) {
            T.showShort(FittingBackActivity.this, getString(R.string.data_error));
            finish();
        }

        final StringBuffer mantrStr = new StringBuffer();
        for (int i = 0; i < manageWDetailItems.size(); i++) {
            ManageWDetailItem item = manageWDetailItems.get(i);
            mantrStr.append(item.getMaterialCode());
            mantrStr.append(",");
        }

        initView();
        initHandler();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        requestQueryPrice(mantrStr.toString());
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
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        fittingBackListView = (ListView) findViewById(R.id.lv_fitting_back);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_CODE_DEL_ITEM:
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case HANDLER_CODE_QUERY_PRICE:
                        mAdapter = new FittingBackListAdapter(FittingBackActivity.this);
                        mAdapter.setDataList(manageWDetailItems);
                        mAdapter.setMap(materPriceMap);

                        mAnimAdapter = new AlphaInAnimationAdapter(mAdapter);
                        mAnimAdapter.setAbsListView(fittingBackListView);
                        fittingBackListView.setAdapter(mAnimAdapter);

                        //    fittingBackListView.setAdapter(mAdapter);
                        break;
                }
            }
        };
    }

    /**
     * 获取商品价格
     *
     * @param mantrStr
     */
    private void requestQueryPrice(String mantrStr) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(FittingBackActivity.this));
        params.addBodyParameter(ManageConstants.MATER_CODES, mantrStr);

        QueryPriceProcessor mProcessor = new QueryPriceProcessor(FittingBackActivity.this, params, QueryPriceEvent.QUERY_PRICE_TYPE.ACCESS_BACK);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_price));
        mProcessor.sendPostRequest();
    }

    /**
     * 调用配件归还接口
     *
     * @param commdtyListStr
     */
    private void requestAccessoryReturn(String commdtyListStr) {
        System.out.println("Accessory Return json:" + commdtyListStr);
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(FittingBackActivity.this));
        params.addBodyParameter(ManageConstants.SOURCE_TYPE, "2");
        params.addBodyParameter(ManageConstants.COMMODITY_LIST, commdtyListStr);

        AccessoryReturnProcessor mProcessor = new AccessoryReturnProcessor(FittingBackActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_access_return));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收获取价格的Event
     *
     * @param event
     */
    public void onEvent(QueryPriceEvent event) {
        if (event.type == QueryPriceEvent.QUERY_PRICE_TYPE.ACCESS_BACK) {
            List<QueryMaterPriceData> priceDataList = event.priceDataList;
            if (priceDataList != null) {
                for (int i = 0; i < priceDataList.size(); i++) {
                    QueryMaterPriceData data = priceDataList.get(i);
                    materPriceMap.put(data.getMaterCode(), data.getMaterPrice());
                }
            }
            handler.sendEmptyMessage(HANDLER_CODE_QUERY_PRICE);
        }
    }

    /**
     * 接收配件归还的Event
     *
     * @param event
     */
    public void onEvent(AccessoryReturnEvent event) {
        // 15010551 操作成功后返回上一页面
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 接收请求失败的Event
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(FittingBackActivity.this, event.message);
    }

    /**
     * 确认按钮的响应方法
     *
     * @param v
     */
    public void go(View v) {

        if (mAdapter != null) {
            List<String> countList = mAdapter.getCountList();
            if (countList.size() > 0) {
                StringBuffer a = new StringBuffer();
                Gson gson = new Gson();
                a.append("[");

                List<CommodityList> commodityLists = new ArrayList<CommodityList>();
                for (int count = 0; count < manageWDetailItems.size(); count++) {
                    ManageWDetailItem item = manageWDetailItems.get(count);
                    CommodityList commodityList = new CommodityList();
                    commodityList.setCommodity(item.getMaterialCode());
                    commodityList.setCommodityName(item.getMaterDesc());
                    commodityList.setPlant(item.getPlant());
                    commodityList.setBatch(item.getSupplier());
                    commodityList.setCommodityNumber(countList.get(count));
                    commodityList.setUnit(item.getUnit());
                    commodityList.setPrice(materPriceMap.get(item.getMaterialCode()));
                    String str = gson.toJson(commodityList);
                    a.append(str);
                    if (count != manageWDetailItems.size() - 1) {
                        a.append(",");
                    }
                }
                a.append("]");
                requestAccessoryReturn(a.toString());

            } else {
                T.showShort(FittingBackActivity.this, getString(R.string.have_no_access));
            }
        } else {
            T.showShort(FittingBackActivity.this, getString(R.string.have_no_access));
        }
    }

    /**
     * 取消按钮的响应方法
     *
     * @param v
     */
    public void cancelAccessReturn(View v) {
        finish();
    }
}
