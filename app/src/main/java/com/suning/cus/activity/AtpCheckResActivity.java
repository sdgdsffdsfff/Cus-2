package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.AtpCheckResListAdapter;
import com.suning.cus.bean.AtpCheckResponseList;
import com.suning.cus.bean.CommodityList;
import com.suning.cus.bean.Material;
import com.suning.cus.constants.ManageConstants;
import com.suning.cus.event.AccessoryReturnEvent;
import com.suning.cus.event.AtpCheckEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.AccessoryReturnProcessor;
import com.suning.cus.logical.AtpCheckProcessor;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * ATP检查页面
 * 15010551
 */
public class AtpCheckResActivity extends BaseActivity {

    // Constant: 任务流程跳转ATP
    public static final String ATP_REQUEST_TYPE_TASK = "task";

    // Constant：备货流程跳转ATP
    public static final String ATP_REQUEST_TYPE_BACKUP = "backup";

    // Intent Bundle Key (in): 请求类型，用以区分备货和任务流程
    public static final String ATP_REQUEST_TYPE = "atpRequestType";

    // Intent Bundle Key (in): ATP检查COMMODITY_LIST字段的json数据
    public static final String COMMDTY_LIST = "commodityList";

    // Intent Bundle Key (in): 商品编码和商品名称的Map
    public static final String CODE_NAME_MAP = "codeNameMap";

    // Intent Bundle Key (out): 商品列表，传给任务页面。
    public static final String MATERIAL_LIST = "MaterialList";

    // Handler Msg Code: 删除item
    public static final int HANDLER_CODE_ATP_DEL = 0x0001;

    // Handler Bundle Key：传递删除item的价格
    public static final String DEL_ITEM_PRICE = "delItemPrice";

    /**
     * 业务相关
     */
    public static Handler handler;

    private AtpCheckResListAdapter mAdapter;

    private AnimationAdapter mAnimAdapter;

    // 请求类型，用以区分备货和任务流程
    public String atpRequestType;

    // 商品编码和商品名称的MAP
    private Map<String, String> codeNameMap = new HashMap<String, String>();

    // ATP检查COMMODITY_LIST字段的json数据
    private String commdtyList;

    // 标识ATP检查是否成功
    private boolean isAtpSucc = false;

    /**
     * UI相关
     */
    private ListView atpCheckResListView;

    private TextView totalPaymentTextView;

    private Button atpResOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_atp_check_res);
        setTitle(getString(R.string.shopcar));

        atpRequestType = getIntent().getStringExtra(ATP_REQUEST_TYPE);
        commdtyList = getIntent().getStringExtra(COMMDTY_LIST);
        codeNameMap = (Map<String, String>) MyUtils.toObject(getIntent().getByteArrayExtra(CODE_NAME_MAP));

        initView();
        initHandler();

        EventBus.getDefault().register(this);
        requestAtpCheck(commdtyList);  // ATP检查
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
        atpCheckResListView = (ListView) findViewById(R.id.lv_atp_check_response);
        totalPaymentTextView = (TextView) findViewById(R.id.tv_atp_total_payment);
        atpResOkButton = (Button) findViewById(R.id.bt_atp_res_ok);
        atpResOkButton.setEnabled(false);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_CODE_ATP_DEL:  // ATP页面删除某个item
                        mAdapter.notifyDataSetChanged();
                        String delItemPrice = msg.getData().getString(DEL_ITEM_PRICE); //获取删除的商品的价格
                        String newTotalPrice = MathUtils.count(totalPaymentTextView.getText().toString(), delItemPrice, "-");
                        totalPaymentTextView.setText(newTotalPrice);
                        break;
                }
            }
        };
    }

    /**
     * ATP页面的确认按钮点击事件
     */
    public void operationAfterAtp(View v) {
        if (mAdapter == null) {
            requestAtpCheck(commdtyList);
        } else {
            List<AtpCheckResponseList> dataList = mAdapter.getDataList();
            if (ATP_REQUEST_TYPE_BACKUP.equals(atpRequestType)) {   // 备货流程调用ATP
                if (isAtpSucc == false) {  // ATP检查失败，重新加载
                    requestAtpCheck(commdtyList);
                } else {    // 确定
                    if (dataList != null) {
                        if (dataList.size() > 0) {
                            String cmmdtyList = toAccesReturnJson();
                            // 15010551 如果价格为0，则给出提示
/*                            if (cmmdtyList.equals("EXIST_ZERO_PRICE")) {
                                T.showShort(AtpCheckResActivity.this, "存在配件价格为空，请删除此配件或在R3中维护相应价格信息");
                                return;
                            }*/
                            requestAccessoryReturn(cmmdtyList);    // 调用订单归还接口
                        } else {
                            T.showShort(AtpCheckResActivity.this, getString(R.string.shopcar_is_empty));
                        }
                    }
                }
            } else {    // 任务流程调用ATP
                if (isAtpSucc == false) {
                    requestAtpCheck(commdtyList);
                } else {
                    if (dataList != null) {
                        if (dataList.size() > 0) {
                            Intent intent = new Intent();
                            ArrayList<Material> materialList = new ArrayList<Material>();
                            for (int i = 0; i < dataList.size(); i++) {
                                AtpCheckResponseList data = dataList.get(i);
                                Material material = new Material();
                                material.setMaterCode(data.getCommodity());
                                material.setMaterType(data.getBatch());
                                material.setMaterNumber(data.getCommodityNumber());
                                material.setMaterDesc(codeNameMap.get(data.getCommodity()));
                                String price = data.getPrice();
                                // 15010551 如果价格为0，则给出提示
/*                                if (Double.parseDouble(price) == 0) {
                                    T.showShort(AtpCheckResActivity.this, "存在配件价格为空，请删除此配件或在R3中维护相应价格信息");
                                    return ;
                                }*/
                                material.setMaterPrice(price);
                                materialList.add(material);
                            }
                            intent.putExtra(MATERIAL_LIST, materialList);   // 回传给任务页面
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AtpCheckResActivity.this);
                            builder.setMessage(getString(R.string.atp_shopcar_empty_notice));
                            builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            builder.setNegativeButton(getString(R.string.cancel), null);
                            builder.show();
                        }
                    }
                }
            }
        }
    }

    /**
     * ATP页面的取消按钮点击事件
     */
    public void cancelAfterAtp(View v) {
        finish();
    }

    /**
     * 调用ATP检查接口
     *
     * @param commodityList COMMODITY_LIST字段的json值
     */
    private void requestAtpCheck(String commodityList) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(AtpCheckResActivity.this));
        params.addBodyParameter(ManageConstants.COMMODITY_LIST, commodityList);

        AtpCheckProcessor mProcessor = new AtpCheckProcessor(AtpCheckResActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_atp_check));
        mProcessor.sendPostRequest();
    }

    /**
     * 调用配件归还接口
     *
     * @param commdtyListStr
     */
    private void requestAccessoryReturn(String commdtyListStr) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(AtpCheckResActivity.this));
        params.addBodyParameter(ManageConstants.SOURCE_TYPE, "1"); //备货1 配件归还2
        params.addBodyParameter(ManageConstants.COMMODITY_LIST, commdtyListStr);

        AccessoryReturnProcessor mProcessor = new AccessoryReturnProcessor(AtpCheckResActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_submit_order));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收ATP检查结果Event
     *
     * @param event
     */
    public void onEvent(AtpCheckEvent event) {
        if (event.isAtpCheckSucc) {
            List<AtpCheckResponseList> atpCheckResponseList = event.atpCheckResponseList;
            String totalPrice = "0";
            for (int position = 0; position < atpCheckResponseList.size(); position++) {
                AtpCheckResponseList data = atpCheckResponseList.get(position);
                String price = MathUtils.count(data.getPrice(), data.getCommodityNumber(), "*");
                totalPrice = MathUtils.count(totalPrice, price, "+");
            }
            totalPaymentTextView.setText(totalPrice);
            mAdapter = new AtpCheckResListAdapter(AtpCheckResActivity.this);
            mAdapter.setCodeNameMap(codeNameMap);
            mAdapter.setDataList(atpCheckResponseList);

            mAnimAdapter = new AlphaInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(atpCheckResListView);
            atpCheckResListView.setAdapter(mAnimAdapter);
            //    atpCheckResListView.setAdapter(mAdapter);
            //返回成功才将提交按钮置为可用
            setAtpResOkButtonStatus(true, getString(R.string.sure), true);
        } else {
            setAtpResOkButtonStatus(true, getString(R.string.atp_reload), false);
        }
    }

    /**
     * 接收配件归还Event
     *
     * @param event
     */
    public void onEvent(AccessoryReturnEvent event) {
        // 15010551 操作成功后返回上一页面
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 接收请求失败Event
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(AtpCheckResActivity.this, event.message);
    }

    /**
     * 转成配件归还接口的json数据
     */
    private String toAccesReturnJson() {

        List<AtpCheckResponseList> dataList = mAdapter.getDataList();

        StringBuffer a = new StringBuffer();
        Gson gson = new Gson();
        a.append("[");

        List<CommodityList> commodityLists = new ArrayList<CommodityList>();
        for (int count = 0; count < dataList.size(); count++) {
            AtpCheckResponseList data = dataList.get(count);
            CommodityList commodityList = new CommodityList();
            commodityList.setCommodity(data.getCommodity());
            commodityList.setCommodityName(codeNameMap.get(data.getCommodity()));
            commodityList.setPlant(data.getPlant());
            commodityList.setBatch(data.getBatch());
            commodityList.setCommodityNumber(data.getCommodityNumber());
            commodityList.setUnit(data.getUnit());
            String price = data.getPrice();
/*            if (Double.parseDouble(price) == 0) {   // 如果存在价格为0的物品，则返回
                return "EXIST_ZERO_PRICE";
            }*/
            commodityList.setPrice(data.getPrice());
            String str = gson.toJson(commodityList);
            a.append(str);
            if (count != dataList.size() - 1) {
                a.append(",");
            }
        }
        a.append("]");
        return a.toString();
    }

    /**
     * 设置ATP页面按钮的状态的文字
     *
     * @param isEnable
     * @param btnText
     */
    private void setAtpResOkButtonStatus(boolean isEnable, String btnText, boolean isAtpSucc) {
        if (atpResOkButton != null) {
            atpResOkButton.setEnabled(isEnable);
            atpResOkButton.setText(btnText);
        }
        this.isAtpSucc = isAtpSucc;
    }
}
