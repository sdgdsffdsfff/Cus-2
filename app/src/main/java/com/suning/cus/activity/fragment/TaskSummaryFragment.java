package com.suning.cus.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.activity.TaskHistoryActivity;
import com.suning.cus.activity.TaskListActivity;
import com.suning.cus.adapter.TaskSummaryListAdapter;
import com.suning.cus.bean.TaskSummary;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskSummaryEvent;
import com.suning.cus.extras.pulltorefresh.PullToRefreshBase;
import com.suning.cus.extras.pulltorefresh.PullToRefreshListView;
import com.suning.cus.logical.TaskSummaryProcessor;
import com.suning.cus.utils.DateTimeUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TaskSummaryFragment extends Fragment {

    /*View 对象*/
    @ViewInject(R.id.lv_summary)
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private TaskSummaryListAdapter mTaskSummaryAdapter;

    /**
     * 订单查询Layout
     */
    private LinearLayout historyLayout;

    /**
     * 数据对象
     */
    private ArrayList<TaskSummary> mTasks;

    /**
     * 最后一次更新时间
     */
    private Date mLastUpdateTime;

    public TaskSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View content = inflater.inflate(R.layout.fragment_task_summary, container, false);

        ViewUtils.inject(this, content);

        initViews(inflater);

        return content;
    }

    @Override
    public void onResume() {
        super.onResume();
        DebugLog.d("onResume");

        /*如果刷新频率在30s之内就不做请求*/
        if (mLastUpdateTime == null || DateTimeUtils.now().getTime() - mLastUpdateTime
                .getTime() >= 30 * 1000) {
            mLastUpdateTime = DateTimeUtils.now();
            DebugLog.d("doPullRefreshing");
            mPullToRefreshListView.doPullRefreshing(true, 500);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        DebugLog.d("onPullUpRefreshComplete");
        /*避免在Event被unregister后，mPullToRefreshListView一直在刷新，这里强制complete*/
        mPullToRefreshListView.onPullDownRefreshComplete();
    }

    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        DebugLog.d("onHiddenChanged = " + hidden);

        if (!hidden) {

            /*如果刷新频率在30s之内就不做请求*/
            if (mLastUpdateTime == null || DateTimeUtils.now().getTime() - mLastUpdateTime
                    .getTime() >= 30 * 1000) {
                mLastUpdateTime = DateTimeUtils.now();
                DebugLog.d("doPullRefreshing");
                mPullToRefreshListView.doPullRefreshing(true, 500);
            }
        } else {
            DebugLog.d("onPullUpRefreshComplete");
            mPullToRefreshListView.onPullDownRefreshComplete();
        }
    }

    /**
     * 初始化
     *
     * @param inflater
     */
    public void initViews(LayoutInflater inflater) {

        mTaskSummaryAdapter = new TaskSummaryListAdapter(getActivity());

        mPullToRefreshListView.setPullLoadEnabled(false);
        mPullToRefreshListView.setScrollLoadEnabled(false);
        mListView = mPullToRefreshListView.getRefreshableView();

        historyLayout = (LinearLayout) inflater.inflate(R.layout.list_footer_task_summary, null,
                false);

        /**
         * 设置下来刷新事件
         */
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase
                .OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    request();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (view.getId() == R.id.ll_summary_history) {
                    onClick(view);
                    return;
                }

                TaskSummary summary = mTasks.get(position);

                if (summary.getTotalTaskNum().equals("0")) {
                    return;
                }

                Intent intent = new Intent(getActivity(), TaskListActivity.class);
                intent.putExtra("task_date", summary.getTaskDate());
                intent.putExtra("task_number", summary.getTotalTaskNum());
                intent.putExtra("task_unfinished_number", summary.getUnfinishedTaskNum());
                startActivity(intent);

            }
        });

    }

    /**
     * 从服务器获取任务列表总数
     */
    public void request() {

        /*Http请求参数*/
        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (getActivity()));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(getActivity()));

        TaskSummaryProcessor processor = new TaskSummaryProcessor(getActivity());
        processor.setParams(params);
        processor.sendPostRequest();

    }

    /**
     * 当TaskSummaryEvent 发送后，这个函数会被调用
     *
     * @param event
     */
    public void onEvent(TaskSummaryEvent event) {
        DebugLog.d("onEvent");
        mPullToRefreshListView.onPullDownRefreshComplete();

        mTasks = event.mTasks;
        if (mTasks == null) {
            return;
        }
        mTaskSummaryAdapter.setTasks(mTasks);

        // 防止FooterView 多次添加
        if (historyLayout != null && mListView.getFooterViewsCount() < 1) {
            mListView.addFooterView(historyLayout);
        }

        mListView.setAdapter(mTaskSummaryAdapter);
    }

    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {
        DebugLog.d("onEvent");
        mPullToRefreshListView.onPullDownRefreshComplete();

        T.showShort(getActivity(), event.message);
    }

    /**
     * 点击历史订单
     *
     * @param v
     */
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), TaskHistoryActivity.class);
        startActivity(intent);
    }

}
