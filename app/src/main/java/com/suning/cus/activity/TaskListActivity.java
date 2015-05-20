package com.suning.cus.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.activity.fragment.TaskListFragment;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.logical.TaskListProcessor.QueryFilter;

/**
 * 任务列表
 * Created by 14110105 on 2015/3/11.
 */
public class TaskListActivity extends BaseActivity implements TaskListFragment
        .OnFragmentInteractionListener {


    //    @ViewInject(R.id.view_pager)
    private ViewPager mPager;

    @ViewInject(R.id.tabs)
    private PagerSlidingTabStrip mTabs;


    @ViewInject(R.id.tv_summary_date)
    private TextView mDate;
    @ViewInject(R.id.tv_summary_number)
    private TextView mNumber;

    /**
     * Intent 传递过来的任务日期
     */
    public String mTaskDate;


    private TaskListFragment mProcessedFrag;
    private TaskListFragment mUnProcessedFrag;

    private String[] mTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_task_list);
        setTitle(getString(R.string.title_task_list));

        mPager = (ViewPager) findViewById(R.id.view_pager);

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        //初始化控件变量
        ViewUtils.inject(this);

        // 设置Title上面的任务日期和个数
        mTaskDate = getIntent().getStringExtra("task_date");
        /*Intent 传递过来的总任务数量*/
        String mTaskNumber = getIntent().getStringExtra("task_number");
        /*Intent 传递过来的未完成任务数量*/
        String mTaskUnfinishedNumber = getIntent().getStringExtra("task_unfinished_number");
        String mTaskFinishedNumber = MathUtils.count(mTaskNumber, mTaskUnfinishedNumber, "-");

        mTitles = new String[]{getString(R.string.task_list_tab_unprocessed,
                mTaskUnfinishedNumber), getString(R.string.task_list_tab_processed,
                mTaskFinishedNumber)};

        mDate.setText(mTaskDate);
        mNumber.setText(getString(R.string.task_list_summary_number, mTaskNumber));

        mPager.setAdapter(new MyAdapter(getSupportFragmentManager(), mTitles));

        mTabs.setViewPager(mPager);

    }

    @Override
    public void updateTaskNumber(QueryFilter filter, String number) {
        if (filter == QueryFilter.UNPROCESSED) {
            mTitles[0] = getString(R.string.task_list_tab_unprocessed, number);
        } else {
            mTitles[1] = getString(R.string.task_list_tab_processed, number);
        }
        mTabs.notifyDataSetChanged();
    }


    public class MyAdapter extends FragmentPagerAdapter {

        String[] _titles;

        public MyAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            _titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles[position];
        }

        @Override
        public int getCount() {
            return _titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mProcessedFrag == null) {
                        mProcessedFrag = TaskListFragment.newInstance(QueryFilter.UNPROCESSED,
                                mTaskDate);
                    }
                    return mProcessedFrag;
                case 1:
                    if (mUnProcessedFrag == null) {
                        mUnProcessedFrag = TaskListFragment.newInstance(QueryFilter.PROCESSED,
                                mTaskDate);
                    }
                    return mUnProcessedFrag;

                default:
                    return null;
            }
        }

    }
}
