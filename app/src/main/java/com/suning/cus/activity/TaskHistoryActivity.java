package com.suning.cus.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.adapter.TaskHistoryListAdapter;
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
import com.suning.cus.utils.DateTimeUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 历史任务
 * Created by 14110105 on 2015/3/10.
 */
public class TaskHistoryActivity extends BaseActivity {


    /**
     * 起始日期Listener
     */
    private DatePickerDialog.OnDateSetListener mDateListener1;
    /**
     * 结束日期Listener
     */
    private DatePickerDialog.OnDateSetListener mDateListener2;

    private String mStartDate = null;
    private String mEndDate = null;

    private Calendar mCalendar;

    @ViewInject(R.id.tv_date_start)
    private TextView mDateStartView;
    @ViewInject(R.id.tv_date_end)
    private TextView mDateEndView;

    @ViewInject(R.id.lv_task_history)
    private PullToRefreshListView mPullToRefresh;
    private ListView mTaskListView;


    private List<Task> mTasks;
    private TaskHistoryListAdapter mTaskListAdapter;

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
    private PullStatus mPullState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_task_history);
        setTitle(getString(R.string.title_task_history));
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        /*View 初始化*/
        ViewUtils.inject(this);

        /* 禁止下来刷新，允许加载更多*/
        mPullToRefresh.setPullRefreshEnabled(false);
        mPullToRefresh.setScrollLoadEnabled(true);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mStartDate = mDateStartView.getText().toString();
                mEndDate = mDateEndView.getText().toString();
                mCurrentPage = "1";  // 将当前页显示为第一页
                mTotalPageNum = null;
                mPullState = PullStatus.PULL_DOWN;
                searchTasks(mStartDate, mEndDate);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mStartDate = mDateStartView.getText().toString();
                mEndDate = mDateEndView.getText().toString();
                mPullState = PullStatus.PULL_UP;
                searchTasks(mStartDate, mEndDate);
            }
        });

        mTaskListView = mPullToRefresh.getRefreshableView();
        mTaskListView.setDivider(null);  // 去除分割线

        mTasks = new ArrayList<>();
        mTaskListAdapter = new TaskHistoryListAdapter(this);
        mTaskListAdapter.setTasks(mTasks);
        mTaskListView.setAdapter(mTaskListAdapter);


        mDateListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mStartDate = DateTimeUtils.formatDate(calendar.getTime());
                mDateStartView.setText(mStartDate);
            }
        };

        mDateListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mEndDate = DateTimeUtils.formatDate(calendar.getTime());
                mDateEndView.setText(mEndDate);
            }
        };

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(TaskHistoryActivity.this, TaskDetailActivity.class);
                intent.putExtra(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(TaskHistoryActivity.this));
                intent.putExtra(UserConstants.IMEI, SpCoookieUtils.getImei(TaskHistoryActivity.this));
                intent.putExtra(TaskConstants.TASK_SERVICE_ID, mTasks.get(position).getServiceId());
                startActivity(intent);
            }
        });

        mCalendar = Calendar.getInstance();

        /*设置日期格式*/
        mDateStartView.setText(DateTimeUtils.formatDate(mCalendar.getTime()));
        mDateEndView.setText(DateTimeUtils.formatDate(mCalendar.getTime()));
    }

    @Override
    protected void onStart() {
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
     * 显示时间选择框
     *
     * @param v 被点击的View
     */
    public void showDatePickDialog(View v) {
        DatePickerDialog dialog;
        if (v.getId() == R.id.tv_date_start) {
            dialog = new DatePickerDialog(this, mDateListener1, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            dialog = new DatePickerDialog(this, mDateListener2, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        dialog.show();
    }

    /**
     * Button的响应事件
     *
     * @param v 点击的View
     */
    public void onClick(View v) {

        mStartDate = mDateStartView.getText().toString();
        mEndDate = mDateEndView.getText().toString();

        /*判断起始和终止日期是否为空*/
        if (TextUtils.isEmpty(mStartDate) || TextUtils.isEmpty(mEndDate)) {
            T.showShort(this, R.string.toast_error_date_no_selected);
            return;
        }

        /*日期不能超过7天*/
        if (DateTimeUtils.getDateMargin(mEndDate, mStartDate) > 7) {
            T.showShort(this, R.string.toast_beyond_date);
            return;
        }

        /*终止日期要大于起始日期*/
        try {
            Date startDate = DateTimeUtils.parseDate(mStartDate);
            Date endDate = DateTimeUtils.parseDate(mEndDate);
            if (!startDate.equals(endDate) && !startDate.before(endDate)) {
                T.showShort(this, R.string.toast_error_date);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 刷新获取数据
        mPullToRefresh.doPullRefreshing(true, 0);
    }

    /**
     * 查询任务列表
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     */
    public void searchTasks(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            T.showShort(this, R.string.toast_error_date_required);
            return;
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
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(TaskHistoryActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(TaskHistoryActivity.this));
        params.addBodyParameter(TaskConstants.PAGE_SIZE, BaseConstants.DEFAULT_PAGE_SIZE);
        params.addBodyParameter(TaskConstants.TASK_DATE, startDate);
        params.addBodyParameter(TaskConstants.TASK_END_DATE, endDate);
        params.addBodyParameter(TaskConstants.CURRENT_PAGE, mCurrentPage);

        /**
         * 开始请求
         */
        TaskListProcessor processor = new TaskListProcessor(this, params);
        processor.sendPostRequest();
    }


    /**
     * 任务列表请求成功后回调
     *
     * @param event 任务列表Event
     */
    public void onEvent(TaskListEvent event) {

        if (mPullState == PullStatus.PULL_DOWN) {
            mPullToRefresh.onPullDownRefreshComplete();
        } else {
            mPullToRefresh.onPullUpRefreshComplete();
        }

        JsonTaskList jsonTaskList = event.mJsonTaskList;

        if (jsonTaskList == null) {
            return;
        }

        /*下拉刷新的时候需要更新任务数量*/
        if (mPullState == PullStatus.PULL_DOWN) {
            mTasks.clear();
        }

        mTotalPageNum = jsonTaskList.getTotalPageNum();

        if (Integer.valueOf(mTotalPageNum) > 1) {
            mPullToRefresh.setHasMoreData(true);
        } else {
            mPullToRefresh.setHasMoreData(false);
        }

        int taskNum = Integer.valueOf(jsonTaskList.getListNum());
        if (taskNum > 0) {
            mTasks.addAll(jsonTaskList.getList());
        }

        mTaskListAdapter.notifyDataSetChanged();
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

        T.showShort(this, event.message);
    }


}
