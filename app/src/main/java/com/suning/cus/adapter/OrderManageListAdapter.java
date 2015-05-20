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
import com.suning.cus.activity.OrderManageActivity;
import com.suning.cus.bean.QueryOrderData;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/18.
 */
public class OrderManageListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<QueryOrderData> dataList = null;

    private int orderCheckedStatus;

    private class ViewHolder {
        TextView vbelnTextView;
        //       TextView materialTextView;
        //       TextView targetQtyTextView;
        TextView orderStatusTextView;
        TextView createDateTextView;
        TextView codeAndNumTextView;
        LinearLayout orderDetailLinearLayout;
    }

    public OrderManageListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        orderCheckedStatus = OrderManageActivity.ORDER_CHECKED_STATUS_MY_ORDER;
    }

    public void setDataList(List<QueryOrderData> dataList) {
        this.dataList = dataList;
    }

    public void clearDataList() {
        this.dataList.clear();
    }

    public void setOrderCheckedStatus(int orderCheckedStatus) {
        this.orderCheckedStatus = orderCheckedStatus;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_order_manage, null);
            holder = new ViewHolder();
            holder.vbelnTextView = (TextView) convertView.findViewById(R.id.tv_service_id);
            //           holder.materialTextView = (TextView) convertView.findViewById(R.id.tv_ware_matnr);
            //           holder.targetQtyTextView = (TextView) convertView.findViewById(R.id.tv_ware_num);
            holder.orderStatusTextView = (TextView) convertView.findViewById(R.id.tv_order_status);
            holder.createDateTextView = (TextView) convertView.findViewById(R.id.tv_order_date);
            holder.orderDetailLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_order_detail);
            holder.codeAndNumTextView = (TextView) convertView.findViewById(R.id.tv_code_and_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QueryOrderData data = dataList.get(position);
        if (orderCheckedStatus == OrderManageActivity.ORDER_CHECKED_STATUS_MY_ORDER) {  // 我的订单
            holder.vbelnTextView.setText(data.getVbeln());
        } else {    // 未完成订单
            holder.vbelnTextView.setText(data.getServiceOrder());
        }
        holder.codeAndNumTextView.setText(data.getMaterial() + "*" + data.getTargetQty());
//        holder.materialTextView.setText(data.getMaterial());
//        holder.targetQtyTextView.setText(data.getTargetQty());
        holder.orderStatusTextView.setText(data.getOrderStatus());
        holder.createDateTextView.setText(data.getCreateDate());
        holder.orderDetailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OrderManageActivity.MATERIAL, data.getMaterial());
                bundle.putString(OrderManageActivity.SERVICE_ORDER, data.getServiceOrder());
                bundle.putString(OrderManageActivity.VBELN, data.getVbeln());
                Message msg = new Message();
                msg.what = OrderManageActivity.HANDLER_CODE_ORDER_DETAIL;
                msg.setData(bundle);
                OrderManageActivity.handler.sendMessage(msg);
            }
        });

        return convertView;
    }
}
