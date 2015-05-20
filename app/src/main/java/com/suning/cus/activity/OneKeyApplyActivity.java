package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.adapter.MaterialListAdapter;
import com.suning.cus.bean.Appliance;
import com.suning.cus.bean.Material;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.constants.MaterialConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.AddFavEvent;
import com.suning.cus.event.ClickFavEvent;
import com.suning.cus.event.MaterialDelFavEvent;
import com.suning.cus.event.OneKeyApplyEvent;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.AddFavProcessor;
import com.suning.cus.logical.MaterialDelFavProcessor;
import com.suning.cus.logical.OneKeyApplyProcessor;
import com.suning.cus.logical.QueryPriceProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 一键申请界面
 * Created by 14110105 on 2015/3/18.
 */
public class OneKeyApplyActivity extends BaseActivity {


    @ViewInject(R.id.tv_model)
    private TextView mModelName;
    @ViewInject(R.id.lv_list)
    private ListView mListView;

    private String mProductType;
    private String mProductName;
    private String mProductCode;

    private String material_code;
    private String material_name;

    private MaterialListAdapter mAdapter;
    private List<Material> mMaterials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_one_key_apply);

        mProductName = getIntent().getStringExtra("product_name");
        mProductCode = getIntent().getStringExtra("product_code");
        mProductType = getIntent().getStringExtra("product_type");

        boolean isFromAppliance = getIntent().getBooleanExtra("is_from_appliance", false);

        if (isFromAppliance) {
            setTitle(getString(R.string.title_appliance_type_apply));
        } else {
            setTitle(getString(R.string.title_material_one_key_apply));
        }

        if (TextUtils.isEmpty(mProductType)) {
            mProductType = "Z011";
        }

        init();
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

    /**
     * 初始化
     */
    public void init() {

        ViewUtils.inject(this);

        mModelName.setText(mProductName);

        mMaterials = new ArrayList<>();
        mAdapter = new MaterialListAdapter(this);
        mAdapter.setmMaterials(mMaterials);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                material_code = mMaterials.get(position).getMaterCode();
                material_name = mMaterials.get(position).getMaterDesc();

                requestPrice(material_code, material_name);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        doSearch();
    }


    public void requestPrice(final String code, final String name) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OneKeyApplyActivity.this));
        params.addBodyParameter(MaterialConstants.MATERIAL_CODES, code);

        QueryPriceProcessor mProcessor = new QueryPriceProcessor(this, params,
                QueryPriceEvent.QUERY_PRICE_TYPE.TASK);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_price));
        mProcessor.sendPostRequest();

    }

    /**
     * 查询价格成功回调
     */
    public void onEvent(QueryPriceEvent event) {

        String material_price = "0";

        if (event.type == QueryPriceEvent.QUERY_PRICE_TYPE.TASK) {
            List<QueryMaterPriceData> priceDataList = event.priceDataList;
            if (priceDataList != null && priceDataList.size() > 0) {
                QueryMaterPriceData price = priceDataList.get(0);

                material_price = price.getMaterPrice();
            }
        }


        Intent intent = new Intent();

        intent.putExtra("material_code", material_code);
        intent.putExtra("material_name", material_name);
        intent.putExtra("material_price", material_price);

        setResult(RESULT_OK, intent);
        finish();

    }


    private void doSearch() {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OneKeyApplyActivity.this));
        params.addBodyParameter("matnr", mProductCode);
        params.addBodyParameter("pageSize", "100");

        OneKeyApplyProcessor processor = new OneKeyApplyProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_searching);
        processor.sendPostRequest();

    }

    /**
     * 查询成功后的回调
     *
     * @param event
     */
    public void onEvent(OneKeyApplyEvent event) {

        List<Appliance> appliances = event.appliances;
        mMaterials.clear();

        if (appliances != null && appliances.size() > 0) {
            for (Appliance appliance : appliances) {
                Material material = new Material();
                material.setMaterCode(appliance.getCommodity());
                material.setMaterDesc(appliance.getProductNm());
                mMaterials.add(material);
            }

        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(this, event.message);
    }

    /**
     * 点击收藏按钮后回调
     *
     * @param event
     */
    public void onEvent(ClickFavEvent event) {
        if (event.material != null) {

            if (event.isAdd) {
                saveFavorite(event.material);
            } else {
                deleteFavorite(event.material);
            }
        }
    }

    /**
     * 添加收藏成功后回调
     *
     * @param event
     */
    public void onEvent(AddFavEvent event) {

        if (event.material != null) {
            Material material = event.material;
            T.showShort(this, R.string.add_fav_success);
            material.setFavorite(true);
        } else {
            T.showShort(this, R.string.add_fav_fail);
        }
    }

    /**
     * 收藏材料
     *
     * @param material
     */
    public void saveFavorite(final Material material) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OneKeyApplyActivity.this));
        params.addBodyParameter("materialCode", material.getMaterCode());
        params.addBodyParameter("materialDesc", material.getMaterDesc());
        params.addBodyParameter("cmmdtyType", mProductType);

        AddFavProcessor processor = new AddFavProcessor(this, params, material);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_submitting);
        processor.sendPostRequest();

    }


    /**
     * 删除收藏成功回调
     *
     * @param event
     */
    public void onEvent(MaterialDelFavEvent event) {

        if (event.material != null) {
            Material material = event.material;
            T.showShort(this, getString(R.string.del_fav_success));
            material.setFavorite(false);
        } else {
            T.showShort(this, R.string.del_fav_fail);
        }

    }

    /**
     * 删除收藏
     * @param material 配件
     */
    public void deleteFavorite(final Material material) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(OneKeyApplyActivity.this));
        params.addBodyParameter("materialCode", material.getMaterCode());

        /*发送请求*/
        MaterialDelFavProcessor mProcessor = new MaterialDelFavProcessor(this, params, material);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_del_fav));
        mProcessor.sendPostRequest();

    }


}
