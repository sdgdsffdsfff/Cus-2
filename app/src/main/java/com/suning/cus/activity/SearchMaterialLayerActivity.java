package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.adapter.MaterialLayerAdapter;
import com.suning.cus.bean.MaterialLayer;
import com.suning.cus.constants.ArrayData;
import com.suning.cus.constants.MaterialConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.SearchMaterialLayerEvent;
import com.suning.cus.logical.SearchMaterialLayerProcessor;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 配件品类选择
 * Created by 14110105 on 2015/3/17.
 */
public class SearchMaterialLayerActivity extends BaseActivity implements AdapterView
        .OnItemClickListener, android.view.View.OnClickListener {

    // data component
    private String mCategoryId[] = ArrayData.CATEGORY_IDS;
    private String mCategoryName[] = ArrayData.CATEGORY_NAMES;
    private String mCategoryCodes[] = ArrayData.CATEGORY_CODES;
    private String mCategoryCode1;
    private List<Map<String, String>> mDatalist = new ArrayList();
    //    private MaterialLayerAdapter mSearchLayerAdapter;

    private String mFirstLaycode;
    private String mMaterType;
    private boolean flag = false; // false为刷新页面，true为跳转

    // View component
    @ViewInject(R.id.search_edit_layout)
    private LinearLayout searchLayout;
    @ViewInject(R.id.search_btn)
    private Button mSearchBtn;
    @ViewInject(R.id.search_edit)
    private EditText mSearchEditText;
    @ViewInject(R.id.accessories_search_list)
    private ListView mLayerListView;

    private SimpleAdapter mCategoryAdapter;
    private MaterialLayerAdapter mLayerAdapter;
    private List<MaterialLayer> mLayers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_searchlayer_list);
        setCustomTitle(getString(R.string.title_material_type_layer));
        initView();
        initDataList();
        initCategory();
        mLayerListView.setOnItemClickListener(this);
        mSearchBtn.setOnClickListener(this);
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

    public void initView() {

        ViewUtils.inject(this);

        mMaterType = getIntent().getStringExtra("materType");
        searchLayout.setVisibility(View.GONE);

        mLayerAdapter = new MaterialLayerAdapter(this);

    }

    /**
     * 初始化数据
     */
    public void initDataList() {
        for (int i = 0; i < mCategoryId.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("categoryId", mCategoryCodes[i]);
            map.put("categoryName", mCategoryName[i]);
            mDatalist.add(map);
        }
    }

    public void initCategory() {
        mCategoryAdapter = new SimpleAdapter(this, mDatalist,
                R.layout.list_item_one_line_two_row, new String[]{"categoryId", "categoryName"},
                new int[]{R.id.tv_text_1, R.id.tv_text_2});
        mLayerListView.setAdapter(mCategoryAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn: // 点击搜索按钮，按搜索框关键字查询并显示数据在listview上
                String searchKey = mSearchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(searchKey)) {

                    RequestParams params = new RequestParams("utf-8");

                    params.addBodyParameter(MaterialConstants.MATERIAL_LAYER_FIRST5,
                            mCategoryCode1);
                    params.addBodyParameter(MaterialConstants.MATERIAL_LAYER_DESC, searchKey);
                    params.addBodyParameter("currentPage", "1");
                    params.addBodyParameter("pageSize", "100");

                    SearchMaterialLayerProcessor processor = new SearchMaterialLayerProcessor
                            (this, params);
                    processor.setDialogEnabled(true);
                    processor.setDialogMessage(R.string.dialog_searching);
                    processor.sendPostRequest();

                } else {
                    T.showShort(this, R.string.toast_field_required);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 查询成功后的回调
     *
     * @param event
     */
    public void onEvent(SearchMaterialLayerEvent event) {

        mLayers = event.layers;

        if (mLayers != null) {
            mLayerAdapter.setLayers(mLayers);
            mLayerListView.setAdapter(mLayerAdapter);
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
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        // false为点击刷新界面，并显示搜索框，true为点击进入另外一个页面
        if (!flag && !mMaterType.equals("Z010") && !mMaterType.equals("Z022")) {
            setCustomTitle(getString(R.string.title_material_layer));
            searchLayout.setVisibility(View.VISIBLE);
            clearList(mDatalist); // 清空品牌品类第一级listview数据
            mCategoryCode1 = mCategoryCodes[position]; // 保存第一级Code,以便传递给后台查询
            flag = true;
        } else {
            TextView mTextView = (TextView) view.findViewById(R.id.tv_text_1);
            mFirstLaycode = mTextView.getText().toString().trim();
            Intent intent = new Intent();
            intent.putExtra("FirstLaycode", mFirstLaycode);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void clearList(List<Map<String, String>> dataList) {
        int size = dataList.size();
        if (size > 0) {
            dataList.clear();
            mCategoryAdapter.notifyDataSetChanged();
        }
    }
}
