package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.bean.Appliance;
import com.suning.cus.event.ApplianceTypeEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.ApplianceTypeProcessor;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 电器型号审配
 * Created by 14110105 on 2015/3/18.
 */
public class ApplianceTypeApplyActivity extends BaseActivity {

    public static final int REQUEST_CODE_APPLY = 1;

    @ViewInject(R.id.et_search_suning)
    private EditText mSearchText1;
    @ViewInject(R.id.et_search_factory)
    private EditText mSearchText2;
    @ViewInject(R.id.iv_search_suning)
    private ImageView mSearchButton1;
    @ViewInject(R.id.iv_search_factory)
    private ImageView mSearchButton2;
    @ViewInject(R.id.lv_list)
    private ListView mListView;

    private SimpleAdapter mAdapter;
    private List<Map<String, String>> mResource;
    private List<Appliance> mAppliances;

    /**
     * 配件类型
     */
    private String mProductType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_appliance_type_apply);
        setTitle(getString(R.string.title_appliance_type_apply));
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*注册subscribers*/
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*把自己从subscribers中移除，不再接收*/
        EventBus.getDefault().unregister(this);
    }

    public void initViews() {
        /*组件注册*/
        ViewUtils.inject(this);

        mProductType = getIntent().getStringExtra("product_type");

        if (TextUtils.isEmpty(mProductType)) {
            mProductType = "Z011";
        }

        mResource = new ArrayList<>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = mAppliances.get(position).getCmmdtyName();
                String matnr = mAppliances.get(position).getCmmdtyCode();

                Intent intent = new Intent(ApplianceTypeApplyActivity.this, OneKeyApplyActivity.class);
                intent.putExtra("product_type", mProductType);
                intent.putExtra("product_name", name);
                intent.putExtra("product_code", matnr);
                intent.putExtra("is_from_appliance", true);

                startActivityForResult(intent, REQUEST_CODE_APPLY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onClick(View v) {

        String model;
        switch (v.getId()) {
            case R.id.iv_search_suning:
                model = mSearchText1.getText().toString();
                if (!TextUtils.isEmpty(model)) {
                    doSearch(model, 1);
                } else {
                    T.showShort(this, R.string.error_required_appliance_type_suning);
                }
                break;

            case R.id.iv_search_factory:
                model = mSearchText2.getText().toString();
                if (!TextUtils.isEmpty(model)) {
                    doSearch(model, 2);
                } else {
                    T.showShort(this, R.string.error_required_appliance_type_factory);
                }
                break;

            default:
                break;
        }
    }

    public void doSearch(String model, int type) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("pageSize", "100");
        if (type == 1) {
            params.addBodyParameter("cmmdtyCode", model);
        } else {
            params.addBodyParameter("supplierCode", model);
        }

        ApplianceTypeProcessor processor = new ApplianceTypeProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_searching);
        processor.sendPostRequest();

    }


    /**
     * 查询成功后的回调
     *
     * @param event
     */
    public void onEvent(ApplianceTypeEvent event) {

        List<Appliance> appliances = event.appliances;

        if (appliances != null && appliances.size() > 0) {
            mAppliances = appliances;
            mResource.clear();
            for (Appliance appliance : mAppliances) {
                Map<String, String> itemData = new HashMap<>();
                itemData.put("name", appliance.getCmmdtyName());

                /*去掉编码前面的九个0*/
                String code1 = appliance.getCmmdtyCode();
                if(code1.length() > 9) {
                    code1 = code1.substring(code1.length() - 9 ,code1.length());
                }

                itemData.put("code1", code1);
                itemData.put("code2", appliance.getSupplierCode());

                mResource.add(itemData);
            }

            mAdapter = new SimpleAdapter(ApplianceTypeApplyActivity.this, mResource, R.layout.list_item_two_line_two_row,
                    new String[]{"name", "code1", "code2"}, new int[]{R.id.tv_text_1, R.id.tv_code_1, R.id.tv_code_2});

            mListView.setAdapter(mAdapter);

        }
    }

    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(this, event.message);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
        }
    }
}
