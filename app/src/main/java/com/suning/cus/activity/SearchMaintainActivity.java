package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.bean.TaskMaintain;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.SearchMaintainEvent;
import com.suning.cus.logical.SearchMaintainProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 故障维修查询界面
 * Created by 14110105 on 2015/3/23.
 */
public class SearchMaintainActivity extends BaseActivity {

    private String mKind;
    private String mCategoryCode;

    private int mClickCount = 0;


    @ViewInject(R.id.et_search)
    private TextView mSearchView;
    @ViewInject(R.id.lv_list)
    private ListView mListView;

    private ArrayAdapter<String> mAdapter;
    private List<TaskMaintain> mTaskMaintains;
    private List<String> mSources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_task_maintain);
        setTitle(getString(R.string.title_task_maintain));
        getIntentDate();

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

    public void getIntentDate() {
        mKind = getIntent().getStringExtra("kind");
        if ("GZYY".equals(mKind)) { // GZYY:故障原因
            setTitle("故障原因");
        }
        if ("WXCS".equals(mKind)) { // WXCS:维修措施
            setTitle("维修措施");
        }
    }

    public void initViews() {
        ViewUtils.inject(this);

        mSources = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, R.layout.list_item_one_row, R.id.tv_text, mSources);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentId = mTaskMaintains.get(position).getCurrentId();
                String desc = mTaskMaintains.get(position).getDesc();

                mCategoryCode = currentId;

                if (mClickCount < 2) {
                    getServerData();
                    mClickCount++;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("kind", mKind);
                    intent.putExtra("currentId", currentId);
                    intent.putExtra("desc", desc);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        getServerData();

    }

    public void getServerData() {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(SearchMaintainActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(SearchMaintainActivity.this));
        params.addBodyParameter("kind", mKind);
        params.addBodyParameter("categoryCode", mCategoryCode);
        params.addBodyParameter("pageSize", "100");

        SearchMaintainProcessor processor = new SearchMaintainProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.sendPostRequest();

    }


    /**
     * 查询成功后的回调
     *
     * @param event
     */
    public void onEvent(SearchMaintainEvent event) {

        mTaskMaintains = event.taskMaintains;
        mSources.clear();
        if (mTaskMaintains != null && mTaskMaintains.size() > 0) {

            for (TaskMaintain t : mTaskMaintains) {
                mSources.add(t.getCurrentId() + t.getDesc());
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


}
