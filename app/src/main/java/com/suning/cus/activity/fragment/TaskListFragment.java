package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.activity.TaskDetailActivity;
import com.suning.cus.adapter.TaskListAdapter;
import com.suning.cus.bean.Task;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.PullStatus;
import com.suning.cus.constants.TaskConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskListEvent;
import com.suning.cus.extras.pulltorefresh.PullToRefreshBase;
import com.suning.cus.extras.pulltorefresh.PullToRefreshListView;
import com.suning.cus.json.JsonTaskList;
import com.suning.cus.logical.TaskListProcessor;
import com.suning.cus.logical.TaskListProcessor.QueryFilter;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 任务列表Fragment
 * Created by 14110105 on 2015/5/11.
 */
public class TaskListFragment extends Fragment {

    private static final String QUERY_STATUS = "query_status";
    private static final String QUERY_DATE = "query_date";

    /**
     * 标注任务状态  1-已处理，2-未处理
     */
    private QueryFilter mQueryFilter;

    /**
     * 服务端获取来的任务列表
     */
    private List<Task> mTasks;
    /**
     * Intent 传递过来的任务日期
     */
    private String mTaskDate;
    /**
     * 当前页
     */
    private String mCurrentPage = "1";

    /**
     * 任务总页数
     */
    private String mTotalPageNum;
    /**
     * 下拉 or 上提
     */
    private PullStatus mPullState = PullStatus.NONE;

    @ViewInject(R.id.lv_task_list)
    private PullToRefreshListView mPullToRefresh;
    private TaskListAdapter mTaskAdapter;

    private OnFragmentInteractionListener mListener;

    public TaskListFragment() {
    }


    public static TaskListFragment newInstance(QueryFilter filter, String date) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putChar(QUERY_STATUS, filter.value());
        args.putString(QUERY_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQueryFilter = QueryFilter.valueOf(getArguments().getChar(QUERY_STATUS));
            mTaskDate = getArguments().getString(QUERY_DATE);
        }
    }

    /**
     * Called when a fragment is first attached to its activity.
     * {@link #onCreate(android.os.Bundle)} will be called after this.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
/*注册subscribers*/
        EventBus.getDefault().register(this);
    }


    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        /*把自己从subscribers中移除，不再接收*/
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        //初始化控件变量
        ViewUtils.inject(this, view);

        init();

        return view;
    }


    protected void init() {

        // 设置下拉刷新和上拉加载更多
        mPullToRefresh.setPullRefreshEnabled(true);
        mPullToRefresh.setScrollLoadEnabled(true);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPullState = PullStatus.PULL_DOWN;
                mCurrentPage = "1";
                mTotalPageNum = null;
                getTasks();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPullState = PullStatus.PULL_UP;
                getTasks();
            }
        });

        ListView mTaskListView = mPullToRefresh.getRefreshableView();
        mTaskListView.setDivider(null);  // 去掉item直接的分割线

        mTasks = new ArrayList<>();
        mTaskAdapter = new TaskListAdapter(getActivity());

        mTaskAdapter.setTasks(mTasks);
        mTaskListView.setAdapter(mTaskAdapter);

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), TaskDetailActivity.class);
                intent.putExtra(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                        (getActivity()));
                intent.putExtra(UserConstants.IMEI, SpCoookieUtils.getImei(getActivity()));
                intent.putExtra(TaskConstants.TASK_SERVICE_ID, mTasks.get(position).getServiceId());
                startActivity(intent);
            }
        });

        mPullToRefresh.doPullRefreshing(true, 500);
    }

    /**
     * 获取任务数据
     */
    private void getTasks() {

        int currentPage = Integer.valueOf(mCurrentPage);

        if (mPullState == PullStatus.PULL_UP && mTotalPageNum != null) {
            int totalPageNum = Integer.valueOf(mTotalPageNum);
            if (currentPage >= totalPageNum) { /*如果是最后一页则直接返回*/
                T.showShort(getActivity(), R.string.toast_no_more_data);
                mPullToRefresh.onPullUpRefreshComplete();
                mPullToRefresh.setHasMoreData(false);
                return;
            }
            mCurrentPage = String.valueOf(++currentPage);
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (getActivity()));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(getActivity()));
        params.addBodyParameter(TaskConstants.TASK_DATE, mTaskDate);
        params.addBodyParameter(TaskConstants.PAGE_SIZE, BaseConstants.DEFAULT_PAGE_SIZE);
        params.addBodyParameter(TaskConstants.CURRENT_PAGE, mCurrentPage);
        params.addBodyParameter(TaskConstants.QUERY_STATUS, String.valueOf(mQueryFilter.value()));

        /**
         * 开始请求
         */
        TaskListProcessor processor = new TaskListProcessor(getActivity(), params);
        processor.setQueryFilter(mQueryFilter);
        processor.sendPostRequest();

    }

    /**
     * 任务列表请求成功后回调
     *
     * @param event 任务列表Event
     */
    public void onEvent(TaskListEvent event) {

        JsonTaskList jsonTaskList = event.mJsonTaskList;

        /*查询类型不一致则不处理*/
        if (mQueryFilter != event.getFilter() || jsonTaskList == null) {
            return;
        }

        mTotalPageNum = jsonTaskList.getTotalPageNum();

        if (mPullState == PullStatus.PULL_DOWN) {
            mPullToRefresh.onPullDownRefreshComplete();

            /*清空数据*/
            mTasks.clear();

            /*下拉刷新的时候需要更新任务数量*/
            if (mListener != null) {
                mListener.updateTaskNumber(mQueryFilter, jsonTaskList.getTotalTaskNum());
            }

            /*判断是否有更多的数据需要加载*/
            if (Integer.valueOf(mTotalPageNum) > 1) {
                mPullToRefresh.setHasMoreData(true);
            } else {
                mPullToRefresh.setHasMoreData(false);
            }

        } else {
            mPullToRefresh.onPullUpRefreshComplete();
        }

        int taskNum = Integer.valueOf(jsonTaskList.getListNum());
        if (taskNum > 0) {
            mTasks.addAll(jsonTaskList.getList());
        }

        mTaskAdapter.notifyDataSetChanged();
    }


    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {

        if (mPullState == PullStatus.PULL_DOWN) {
            mPullToRefresh.onPullDownRefreshComplete();
        } else {
            mPullToRefresh.onPullUpRefreshComplete();
        }

        T.showShort(getActivity(), event.message);
    }


    public static interface OnFragmentInteractionListener {
        public void updateTaskNumber(QueryFilter filter, String number);
    }

}
