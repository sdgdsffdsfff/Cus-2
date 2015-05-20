package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.adapter.MaterialCategoryAdapter;
import com.suning.cus.adapter.MaterialListAdapter;
import com.suning.cus.bean.Material;
import com.suning.cus.bean.MaterialCategory;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.constants.MaterialConstants;
import com.suning.cus.constants.PullStatus;
import com.suning.cus.constants.TaskConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.AddFavEvent;
import com.suning.cus.event.ClickFavEvent;
import com.suning.cus.event.MaterialDelFavEvent;
import com.suning.cus.event.QualityAssuranceEvent;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.SearchMaterialCategoryEvent;
import com.suning.cus.event.SearchMaterialEvent;
import com.suning.cus.extras.pulltorefresh.PullToRefreshBase;
import com.suning.cus.extras.pulltorefresh.PullToRefreshListView;
import com.suning.cus.json.JsonMaterial;
import com.suning.cus.json.JsonQualityAssurance;
import com.suning.cus.logical.AddFavProcessor;
import com.suning.cus.logical.MaterialDelFavProcessor;
import com.suning.cus.logical.QualityAssuranceProcessor;
import com.suning.cus.logical.QueryMaterialPriceProcessor;
import com.suning.cus.logical.QueryPriceProcessor;
import com.suning.cus.logical.SearchMaterialCategoryProcessor;
import com.suning.cus.logical.SearchMaterialProcessor;
import com.suning.cus.utils.KeyBoardUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 查询配件
 */
public class SearchMaterialActivity extends BaseActivity implements OnItemClickListener {

    /**
     * 请求价格
     */
    public static final int REQUEST_CODE_BRAND = 1;

    public static final String EXTRA_MATERIAL = "material";

    /**
     * 服务订单类型
     * 由Intent传递过来的
     */
    private String mServiceOrderType;
    private String mQualityAssurance;
    private String mServiceOrg;
    private boolean isTaskFinish;
    private String mSaleOrg;
    private String mChannel;


    @ViewInject(R.id.sp_type)
    private Spinner mTypeSpinner;
    @ViewInject(R.id.et_brand)
    private EditText mBrandEdit;
    @ViewInject(R.id.et_search_category)
    private EditText mCategoryEdit;
    @ViewInject(R.id.et_search_material)
    private EditText mMaterialEdit;
    @ViewInject(R.id.lv_materials)
    private PullToRefreshListView mPullToRefresh;
    private ListView mMaterListView;

    private String mMaterType;

    private ArrayAdapter<CharSequence> adapterMaterType2;
    private ArrayAdapter<CharSequence> adapterMaterType3;

    private MaterialCategoryAdapter mCategoryAdapter;
    private List<MaterialCategory> mCategories;
    private String mCategoryName;

    private MaterialListAdapter mMaterialAdapter;
    private List<Material> mMaterials;
    private Material mSelectedMaterial;


    /**
     * 1 = 材料类目， 2 = 材料
     */
    private int flag = 1;

    /**
     * 当前页
     */
    private String mCurrentPage = "1";

    /**
     * 任务总页数
     */
    private String mTotalPageNum;

    private PullStatus mPullState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_material_search);
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

        mServiceOrderType = getIntent().getStringExtra(TaskConstants.SERVICE_ORDER_TYPE);
        mQualityAssurance = getIntent().getStringExtra("qualityAssurance");
        mServiceOrg = getIntent().getStringExtra("serviceOrg");
        /*是否是从TaskFinishActivity跳转过来，是为true
          由Intent传递过来的*/
        isTaskFinish = getIntent().getBooleanExtra("isTaskFinish", false);
        mSaleOrg = getIntent().getStringExtra("saleOrg");
        mChannel = getIntent().getStringExtra("channel");

        if (isTaskFinish) {
            setTitle(getString(R.string.title_un_material_apply));
        } else {
            setTitle(getString(R.string.title_material_apply));
        }

        mMaterListView = mPullToRefresh.getRefreshableView();
        mPullToRefresh.setPullRefreshEnabled(false);
        mPullToRefresh.setScrollLoadEnabled(true);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (flag == 1 && !TextUtils.isEmpty(mCategoryName)) {
                    mPullState = PullStatus.PULL_UP;
                    SearchCategoryList(mCategoryEdit.getText().toString().trim());
                }

                if (flag == 2) {
                    mPullState = PullStatus.PULL_UP;
                    searchMaterList();
                }
            }
        });

        initSpinner();

        mCategories = new ArrayList<>();
        mCategoryAdapter = new MaterialCategoryAdapter(this);
        mCategoryAdapter.setCategories(mCategories);

        mMaterials = new ArrayList<>();
        mMaterialAdapter = new MaterialListAdapter(this);
        mMaterialAdapter.setmMaterials(mMaterials);
        /*是否显示收藏按钮，如果是从任务完成界面跳过来的就不需要显示*/
        mMaterialAdapter.showFavBtn(!isTaskFinish);

        initClick();
    }


    /**
     * 初始化Spinner
     */
    public void initSpinner() {

        adapterMaterType2 = ArrayAdapter.createFromResource(this, R.array.material_type2,
                android.R.layout.simple_spinner_item);
        adapterMaterType2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterMaterType3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterMaterType3.add("Z011  配件");
        adapterMaterType3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        String serviceOrderType = mServiceOrderType;

        if (TextUtils.isEmpty(serviceOrderType)) {

            mTypeSpinner.setAdapter(adapterMaterType3);

        } else {
            mTypeSpinner.setAdapter(adapterMaterType2);
        }

        mTypeSpinner.setOnItemSelectedListener(new OnItemSelectListenerImp());

    }

    public void initClick() {
        mMaterListView.setOnItemClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_brand:
                Intent intent = new Intent(this, SearchMaterialLayerActivity.class);
                intent.putExtra("materType", mMaterType);
                startActivityForResult(intent, REQUEST_CODE_BRAND);
                break;
            case R.id.iv_search_category:
                String categoryName = mCategoryEdit.getText().toString().trim();
                mCategoryName = categoryName;
                if (!TextUtils.isEmpty(categoryName)) {
                    flag = 1;
                    mCurrentPage = "1";
                    mTotalPageNum = null;
                    mPullState = PullStatus.PULL_DOWN;
                    mMaterListView.setAdapter(mCategoryAdapter);
                    SearchCategoryList(categoryName);
                    KeyBoardUtils.closeKeyboard(mCategoryEdit, this);
                } else {
                    T.showShort(this, R.string.error_required_category_name);
                }
                break;
            case R.id.iv_search_material:
                String materName = mMaterialEdit.getText().toString().trim().toUpperCase();
                if (!materName.equals("")) {
                    flag = 2;
                    mCurrentPage = "1";
                    mTotalPageNum = null;
                    mPullState = PullStatus.PULL_DOWN;
                    mMaterListView.setAdapter(mMaterialAdapter);
                    searchMaterList();
                    /*隐藏软键盘*/
                    KeyBoardUtils.closeKeyboard(mMaterialEdit, this);
                } else {
                    T.showShort(this, R.string.error_required_material_name);
                }
                break;
            default:
                break;
        }
    }

    private class OnItemSelectListenerImp implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mMaterType = parent.getItemAtPosition(position).toString().split(" ")[0].trim();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }


    /**
     * 查询商品类目
     *
     * @param categoryName
     */
    public void SearchCategoryList(String categoryName) {

        if (mPullState == PullStatus.PULL_UP) {
            mPullToRefresh.onPullUpRefreshComplete();
        }

        if (mPullState == PullStatus.PULL_UP && mTotalPageNum != null) {
            int currentPage = Integer.valueOf(mCurrentPage);
            int totalPageNum = Integer.valueOf(mTotalPageNum);
            if (currentPage >= totalPageNum) {
                T.showShort(this, R.string.toast_no_more_data);
                mPullToRefresh.onPullUpRefreshComplete();
                mPullToRefresh.setHasMoreData(false);
                return;
            }
            mCurrentPage = String.valueOf(++currentPage);
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter(MaterialConstants.MATERIAL_CATEGORY_DESC, categoryName);
        params.addBodyParameter(TaskConstants.CURRENT_PAGE, mCurrentPage);

        SearchMaterialCategoryProcessor processor = new SearchMaterialCategoryProcessor(this,
                params);
        processor.sendPostRequest();

    }

    /**
     * 查询材料类目成功回调
     *
     * @param event
     */
    public void onEvent(SearchMaterialCategoryEvent event) {

        if (mPullState == PullStatus.PULL_DOWN) {
            mCategories.clear();
        }

        List<MaterialCategory> categories = event.categories;

        mTotalPageNum = event.totalPageNumber;

        if (!TextUtils.isEmpty(mTotalPageNum)) {

            if (Integer.valueOf(mTotalPageNum) > 1) {
                mPullToRefresh.setHasMoreData(true);
            } else {
                mPullToRefresh.setHasMoreData(false);
            }

            if (categories != null && categories.size() > 0) {
                mCategories.addAll(categories);
            }
        } else {
            T.showShort(this, R.string.error_category_not_found);
        }

        mCategoryAdapter.notifyDataSetChanged();
    }


    /**
     * 查询配件列表
     */
    public void searchMaterList() {

        if (mPullState == PullStatus.PULL_UP && mTotalPageNum != null) {
            int currentPage = Integer.valueOf(mCurrentPage);
            int totalPageNum = Integer.valueOf(mTotalPageNum);
            if (currentPage >= totalPageNum) {
                T.showShort(this, R.string.toast_no_more_data);
                mPullToRefresh.onPullUpRefreshComplete();
                mPullToRefresh.setHasMoreData(false);
                return;
            }
            mCurrentPage = String.valueOf(++currentPage);
        }

        String firstLaycode = mBrandEdit.getText().toString().trim();
        String seondLaycode = mCategoryEdit.getText().toString().trim();
        String materName = mMaterialEdit.getText().toString().trim().toUpperCase();

        RequestParams params = new RequestParams();
        params.addBodyParameter(MaterialConstants.MATERIAL_TYPE, mMaterType);
        params.addBodyParameter(MaterialConstants.MATERIAL_LAYER_CODE, firstLaycode);
        params.addBodyParameter(MaterialConstants.MATERIAL_CATEGORY_CODE, seondLaycode);
        params.addBodyParameter(MaterialConstants.MATERIAL_DESC, materName);
        params.addBodyParameter(TaskConstants.CURRENT_PAGE, mCurrentPage);

        /*请求处理*/
        SearchMaterialProcessor processor = new SearchMaterialProcessor(this, params);
        if (mPullState == PullStatus.PULL_DOWN) {
            processor.setDialogEnabled(true);
            processor.setDialogMessage(R.string.search_waiting);
        }
        processor.sendPostRequest();

    }

    /**
     * 材料查询成功后回调
     *
     * @param event
     */
    public void onEvent(SearchMaterialEvent event) {

        if (mPullState == PullStatus.PULL_UP) {
            mPullToRefresh.onPullUpRefreshComplete();
        }

        // 1.如果是下来刷新，则先清空数据
        if (mPullState == PullStatus.PULL_DOWN) {
            mMaterials.clear();
        }

        JsonMaterial jsonMaterial = event.jsonMaterial;

        mTotalPageNum = jsonMaterial.getTotalPageNum();

        // 2.如果总页数为null，说明没有数据
        if (!TextUtils.isEmpty(mTotalPageNum)) {
            if (Integer.valueOf(mTotalPageNum) > 1) {
                mPullToRefresh.setHasMoreData(true);
            } else {
                mPullToRefresh.setHasMoreData(false);
            }

            List<Material> materials = jsonMaterial.getMaterialList();

            if (materials != null && materials.size() > 0) {
                mMaterials.addAll(materials);
            }
        } else {
            T.showShort(this, R.string.error_material_not_found);
        }

        mMaterialAdapter.notifyDataSetChanged();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (1 == flag) {
            TextView searchCodeText = (TextView) view.findViewById(R.id.tv_text_2);
            String mSencondLaycode = searchCodeText.getText().toString().trim();
            mCategoryEdit.setText(mSencondLaycode);
        }
        if (2 == flag) {

            mSelectedMaterial = mMaterials.get(position);
            /*设置配件类型*/
            mSelectedMaterial.setMaterType(mMaterType);
            requestPrice(mSelectedMaterial);

        }
    }

    /**
     * 获取质保标识
     *
     * @param material 配件
     */
    public void requestQualityAssurance(final Material material) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("materType", mMaterType);
        params.addBodyParameter("materCode", material.getMaterCode());
        params.addBodyParameter("qualityAssurance", mQualityAssurance);
        params.addBodyParameter("serviceOrg", mServiceOrg);

        QualityAssuranceProcessor processor = new QualityAssuranceProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.search_quality_assurance);
        processor.sendPostRequest();

    }

    /**
     * 获取质保标识成功后回调
     *
     * @param event
     */
    public void onEvent(QualityAssuranceEvent event) {

        if (mSelectedMaterial != null) {
            JsonQualityAssurance jsonQualityAssurance = event.jsonQualityAssurance;
            ArrayList<String> assurances = new ArrayList<>();

            if (!TextUtils.isEmpty(jsonQualityAssurance.getInsurance())) {
                assurances.add(jsonQualityAssurance.getInsurance());
            }

            if (!TextUtils.isEmpty(jsonQualityAssurance.getProlong())) {
                assurances.add(jsonQualityAssurance.getProlong());
            }

            final Intent intent = new Intent();
            intent.putExtra(EXTRA_MATERIAL, mSelectedMaterial);

            intent.putExtra("material_type", mSelectedMaterial.getMaterType());
            intent.putExtra("material_code", mSelectedMaterial.getMaterCode());
            intent.putExtra("material_name", mSelectedMaterial.getMaterDesc());
            intent.putExtra("material_price", mSelectedMaterial.getMaterPrice());

            if (assurances.size() > 0) {
                intent.putExtra("material_assurance", assurances);
            }

            setResult(RESULT_OK, intent);
            finish();
        }

    }

    /**
     * 获取配件价格
     *
     * @param material 配件
     */
    public void requestPrice(final Material material) {

        RequestParams params = new RequestParams();
        /*非配件查询价格的接口跟配件不一样，所以需要分开查询*/
        if (!isTaskFinish) {
            params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                    (SearchMaterialActivity.this));

            params.addBodyParameter(MaterialConstants.MATERIAL_CODES, material.getMaterCode());

            QueryPriceProcessor processor = new QueryPriceProcessor(this, params,
                    QueryPriceEvent.QUERY_PRICE_TYPE.TASK);
            processor.setDialogEnabled(true);
            processor.setDialogMessage(getString(R.string.loading_query_price));
            processor.sendPostRequest();
        } else {
            String firstLaycode = mBrandEdit.getText().toString().trim();
            String seondLaycode = mCategoryEdit.getText().toString().trim();
            params.addBodyParameter("materCode", material.getMaterCode());
            params.addBodyParameter("saleOrg", mSaleOrg);
            params.addBodyParameter("channel", mChannel);
            params.addBodyParameter(MaterialConstants.MATERIAL_TYPE, mMaterType);
            params.addBodyParameter(MaterialConstants.MATERIAL_LAYER_CODE, firstLaycode);
            params.addBodyParameter(MaterialConstants.MATERIAL_CATEGORY_CODE, seondLaycode);
            params.addBodyParameter("materAssurance", mQualityAssurance );

            QueryMaterialPriceProcessor processor = new QueryMaterialPriceProcessor(this, params,
                    QueryPriceEvent.QUERY_PRICE_TYPE.TASK);
            processor.setDialogEnabled(true);
            processor.setDialogMessage(getString(R.string.loading_query_price));
            processor.sendPostRequest();

        }

    }

    /**
     * 查询价格成功回调
     */
    public void onEvent(QueryPriceEvent event) {

        if (mSelectedMaterial != null) {
            String material_price = "0";

            if (event.type == QueryPriceEvent.QUERY_PRICE_TYPE.TASK) {
                List<QueryMaterPriceData> priceDataList = event.priceDataList;
                if (priceDataList != null && priceDataList.size() > 0) {
                    QueryMaterPriceData price = priceDataList.get(0);

                    material_price = price.getMaterPrice();
                } else {
                    if(!TextUtils.isEmpty(event.price)) {
                        material_price = event.price;
                    }
                }
            }

            mSelectedMaterial.setMaterPrice(material_price);

            //查询质保信息
            requestQualityAssurance(mSelectedMaterial);
        }

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
     * 添加为收藏
     *
     * @param material 配件
     */
    public void saveFavorite(final Material material) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (SearchMaterialActivity.this));
        params.addBodyParameter("materialCode", material.getMaterCode());
        params.addBodyParameter("materialDesc", material.getMaterDesc());
        params.addBodyParameter("cmmdtyType", mMaterType);

        AddFavProcessor processor = new AddFavProcessor(this, params, material);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_submitting);
        processor.sendPostRequest();

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
     * 从收藏中删除
     *
     * @param material 配件
     */
    public void deleteFavorite(final Material material) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (SearchMaterialActivity.this));
        params.addBodyParameter("materialCode", material.getMaterCode());

        /*发送请求*/
        MaterialDelFavProcessor mProcessor = new MaterialDelFavProcessor(this, params, material);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_del_fav));
        mProcessor.sendPostRequest();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String mFirstLaycode = data.getStringExtra("FirstLaycode");
            mBrandEdit.setText(mFirstLaycode);
        }
    }

}
