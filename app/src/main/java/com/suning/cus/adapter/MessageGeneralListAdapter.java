package com.suning.cus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.NoticeActivity;
import com.suning.cus.bean.MessageList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15010551 on 2015/3/18.
 */
public class MessageGeneralListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<MessageList> messageLists;

    private static class ViewHolder {
        LinearLayout noticeLinearLayout;
        TextView noticeTimeTextView;
        TextView noticeTitleTextView;
    }

    public MessageGeneralListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMessageLists(List<MessageList> messageLists) {
        this.messageLists = messageLists;
    }

    public void appendMessageLists(List<MessageList> messageLists) {
        int size = 0;
        if (this.messageLists == null) {
            this.messageLists = new ArrayList<MessageList>();
            size = 0;
        } else {
            size = this.messageLists.size();
        }

        this.messageLists.addAll(messageLists);
    }

    public void removeDataList(int position) {
        this.messageLists.remove(position);
    }

    @Override
    public int getCount() {
        return messageLists.size();
    }

    @Override
    public Object getItem(int position) {
        return messageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_settings_notice, null);
            holder = new ViewHolder();
            holder.noticeLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_item_settings_notice);
            holder.noticeTimeTextView = (TextView) convertView.findViewById(R.id.tv_notice_time);
            holder.noticeTitleTextView = (TextView) convertView.findViewById(R.id.tv_notice_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MessageList messageList = messageLists.get(position);
        holder.noticeTimeTextView.setText(messageList.getTime());
        holder.noticeTitleTextView.setText(messageList.getTitle());
        if (messageList.getStatus().equals("Y")) {
            holder.noticeTimeTextView.setTextColor(mContext.getResources().getColor(R.color.lightgray));
            holder.noticeTitleTextView.setTextColor(mContext.getResources().getColor(R.color.lightgray));
        } else {
            holder.noticeTimeTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.noticeTitleTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        holder.noticeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = NoticeActivity.HANDLER_CODE_CHECK_NOTICE_DETAIL;
                Bundle bundle = new Bundle();
                bundle.putString(NoticeActivity.MESSAGE_ID, messageList.getId());
                bundle.putInt(NoticeActivity.MESSAGE_POSITION, position);
                msg.setData(bundle);
                NoticeActivity.handler.sendMessage(msg);
            }
        });
        return convertView;
    }
}
