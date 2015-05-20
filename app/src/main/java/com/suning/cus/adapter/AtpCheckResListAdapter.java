package com.suning.cus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.AtpCheckResActivity;
import com.suning.cus.bean.AtpCheckResponseList;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.MyUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by 15010551 on 2015/3/30.
 */
public class AtpCheckResListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<AtpCheckResponseList> dataList = null;
    private Map<String, String> codeNameMap = null;

    private class ViewHolder {
        TextView atpCommdtyNameTextView;
        TextView atpCommdtyNumTextView;
        TextView atpCommdtyPriceTextView;
        TextView atpCommdtyResultTextView;
        Button atpDelButton;
    }

    public AtpCheckResListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCodeNameMap(Map<String, String> codeNameMap) {
        this.codeNameMap = codeNameMap;
    }

    public void setDataList(List<AtpCheckResponseList> dataList) {
        this.dataList = dataList;
    }

    public List<AtpCheckResponseList> getDataList() {
        return dataList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_atp_check_response, null);
            holder = new ViewHolder();
            holder.atpCommdtyNameTextView = (TextView) convertView.findViewById(R.id.tv_atp_cmmdty_name);
            holder.atpCommdtyNumTextView = (TextView) convertView.findViewById(R.id.tv_atp_cmmdty_num);
            holder.atpCommdtyPriceTextView = (TextView) convertView.findViewById(R.id.tv_atp_cmmdty_price);
            holder.atpCommdtyResultTextView = (TextView) convertView.findViewById(R.id.tv_atp_result);
            holder.atpDelButton = (Button) convertView.findViewById(R.id.bt_atp_del);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AtpCheckResponseList data = dataList.get(position);
        String cmmdtyCode = data.getCommodity();
        holder.atpCommdtyNameTextView.setText(codeNameMap.get(cmmdtyCode));
        //15010551 将数量转成整数
        String commodityNum = data.getCommodityNumber();
        data.setCommodityNumber(MyUtils.Double2Int(commodityNum));
        holder.atpCommdtyNumTextView.setText(data.getCommodityNumber());
        String price = data.getPrice();
        if (Double.parseDouble(price) == 0) {
            holder.atpCommdtyPriceTextView.setText(data.getPrice());
        } else {
            holder.atpCommdtyPriceTextView.setText(data.getPrice() + "/" + data.getUnit());
        }
        holder.atpCommdtyResultTextView.setText(data.getAtpResult());
        holder.atpDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getString(R.string.dialog_delete_alert));
                builder.setPositiveButton(mContext.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String delItemPrice = MathUtils.count(data.getCommodityNumber(), data.getPrice(), "*");
                        dataList.remove(position);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString(AtpCheckResActivity.DEL_ITEM_PRICE, delItemPrice);
                        msg.what = AtpCheckResActivity.HANDLER_CODE_ATP_DEL;
                        msg.setData(bundle);
                        AtpCheckResActivity.handler.sendMessage(msg);
                    }
                });
                builder.setNegativeButton(mContext.getString(R.string.cancel), null);
                builder.show();
            }
        });
        return convertView;
    }

}
