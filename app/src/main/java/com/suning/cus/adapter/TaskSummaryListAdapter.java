package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.TaskSummary;

import java.util.List;

/**
 * TaskSummaryListAdapter
 * Created by 14110105 on 2015/3/11.
 */
public class TaskSummaryListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<TaskSummary> mTasks;


    public TaskSummaryListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setTasks(List<TaskSummary> tasks) {
        mTasks = tasks;
    }

    @Override
    public int getCount() {
        return mTasks == null ? 0 : mTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_task_summary, null, false);
            holder = new ViewHolder();

            holder.date = (TextView) convertView.findViewById(R.id.tv_item_date);
            holder.taskNum = (TextView) convertView.findViewById(R.id.tv_item_task_num);
            holder.unFinishedTaskNum = (TextView) convertView.findViewById(R.id
                    .tv_item_unfinished_task_num);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskSummary task = mTasks.get(position);

        if (task != null) {
            holder.date.setText(task.getTaskDate());
            holder.taskNum.setText(task.getTotalTaskNum());
            holder.unFinishedTaskNum.setText(task.getUnfinishedTaskNum());
        }

        return convertView;
    }


    public static class ViewHolder {
        // 自定义控件集合
        public TextView date;
        public TextView taskNum;
        public TextView unFinishedTaskNum;
    }

}
