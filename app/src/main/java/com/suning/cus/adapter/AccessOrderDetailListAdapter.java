package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.QueryOrderDetailItemList;

import java.util.List;
import java.util.Map;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class AccessOrderDetailListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<QueryOrderDetailItemList> dataList = null;

    /**
     * 装运条件的map
     */
    private Map<String, String> shipmentMap = null;

    /**
     * 订单状态的map
     */
    private Map<String, String> orderStatusMap = null;

    public Map<String, String> getShipmentMap() {
        return shipmentMap;
    }

    public void setShipmentMap(Map<String, String> shipmentMap) {
        this.shipmentMap = shipmentMap;
    }

    public Map<String, String> getOrderStatusMap() {
        return orderStatusMap;
    }

    public void setOrderStatusMap(Map<String, String> orderStatusMap) {
        this.orderStatusMap = orderStatusMap;
    }

    private class ViewHolder {
        TextView buzeiTextView;
        TextView targetQtyTextView;
        TextView targetQuTextView;
        TextView cmmdtyPriceTextView;
        TextView brandCodeTextView;
        TextView batchTextView;
        TextView shipmentTextView;
        TextView statusTextView;
        //    TextView descTextView;
        TextView updateDaTextView;
        TextView wuliuStatusTextView;
    }

    public AccessOrderDetailListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List<QueryOrderDetailItemList> dataList) {
        this.dataList = dataList;
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
            convertView = mInflater.inflate(R.layout.listview_item_access_order_details, null);
            holder = new ViewHolder();
            holder.buzeiTextView = (TextView) convertView.findViewById(R.id.tv_order_buzei);
            holder.targetQtyTextView = (TextView) convertView.findViewById(R.id.tv_order_target_qty);
            holder.targetQuTextView = (TextView) convertView.findViewById(R.id.tv_order_target_qu);
            holder.cmmdtyPriceTextView = (TextView) convertView.findViewById(R.id.tv_order_cmmdty_price);
            holder.brandCodeTextView = (TextView) convertView.findViewById(R.id.tv_order_brand_code);
            holder.batchTextView = (TextView) convertView.findViewById(R.id.tv_order_batch);
            holder.shipmentTextView = (TextView) convertView.findViewById(R.id.tv_order_shipment);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.tv_order_status);
            //        holder.descTextView = (TextView) convertView.findViewById(R.id.tv_order_desc);
            holder.updateDaTextView = (TextView) convertView.findViewById(R.id.tv_order_update_da);
            holder.wuliuStatusTextView = (TextView) convertView.findViewById(R.id.tv_wuliu_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryOrderDetailItemList item = dataList.get(position);
        holder.buzeiTextView.setText(item.getBuzei());
        holder.targetQtyTextView.setText(item.getTargetQty());
        holder.targetQuTextView.setText(item.getTargetQu());
        holder.cmmdtyPriceTextView.setText(item.getCmmdtyPrice());
        holder.brandCodeTextView.setText(item.getBrandCode());
        holder.batchTextView.setText(item.getBatch());
        if (shipmentMap != null) {
            holder.shipmentTextView.setText(shipmentMap.get(item.getShipment()));
        } else {
            holder.shipmentTextView.setText(item.getShipment());
        }
        if (orderStatusMap != null) {
            String status = item.getOrderStatus();
            if (orderStatusMap.containsKey(status)) {
                holder.statusTextView.setText(orderStatusMap.get(item.getOrderStatus()));
            } else {
                holder.statusTextView.setText(R.string.access_ops_apply);
            }
        } else {
            holder.statusTextView.setText(item.getOrderStatus());
        }
//        holder.descTextView.setText(item.getMessageDesc());
        holder.updateDaTextView.setText(item.getUpdateDa());
        holder.wuliuStatusTextView.setText(item.getDescription());
        return convertView;
    }
}
