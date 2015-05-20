package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.Task;

import java.util.List;

/**
 * TaskListAdapter
 * Created by 14110105 on 2015/3/11.
 */
public class TaskListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<Task> mTasks;

    public TaskListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setTasks(List<Task> tasks) {
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
            convertView = mInflater.inflate(R.layout.list_item_task, null, false);
            holder = new ViewHolder();
            holder.orderIdTv = (TextView) convertView.findViewById(R.id.tv_task_item_order);
            holder.addressTv = (TextView) convertView.findViewById(R.id.tv_task_item_address);
            holder.goodsTv = (TextView) convertView.findViewById(R.id.tv_task_item_goods);
            holder.timeTv = (TextView) convertView.findViewById(R.id.tv_task_item_time);
            holder.scheduleLabelTv = (TextView) convertView.findViewById(R.id
                    .tv_task_item_schedule_label);
            holder.scheduleTv = (TextView) convertView.findViewById(R.id.tv_task_item_schedule);
            holder.importantTv = (TextView) convertView.findViewById(R.id.tv_important);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = mTasks.get(position);

        if (task != null) {

            holder.orderIdTv.setText(task.getServiceId());
            holder.addressTv.setText(task.getClientAddress());
            holder.goodsTv.setText(task.getServiceProduct());
            holder.timeTv.setText(task.getBookTime());

            /*服务订单状态：E0006 服务完成、E0007 服务未完成、E0008 服务取消*/
            if("E0006".equals(task.getServiceOrderStatus())) {
                holder.scheduleLabelTv.setText(R.string.task_item_destroy_status);
                holder.scheduleTv.setText(R.string.task_destroy_status_finish);
            } else if ("E0007".equals(task.getServiceOrderStatus())) {
                holder.scheduleLabelTv.setText(R.string.task_item_destroy_status);
                holder.scheduleTv.setText(R.string.task_destroy_status_unfinish);
            } else if ("E0008".equals(task.getServiceOrderStatus())) {
                holder.scheduleLabelTv.setText(R.string.task_item_destroy_status);
                holder.scheduleTv.setText(R.string.task_destroy_status_cancel);
            }  else {
                holder.scheduleLabelTv.setText(R.string.task_item_schedule);
                holder.scheduleTv.setText(task.getBespokeTime());
            }
            /*是否显示重要图标*/
            if ("X".equals(task.getRedBpFlag())) {
                holder.importantTv.setVisibility(View.VISIBLE);
            } else {
                holder.importantTv.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    public static class ViewHolder {
        // 自定义控件集合
        public TextView orderIdTv;
        public TextView addressTv;
        public TextView goodsTv;
        public TextView timeTv;
        public TextView scheduleLabelTv;
        public TextView scheduleTv;
        public TextView importantTv;
    }

}
