package com.suning.cus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.FittingBackActivity;
import com.suning.cus.bean.ManageWDetailItem;
import com.suning.cus.extras.NumEditText;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 15010551 on 2015/3/20.
 */
public class FittingBackListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<ManageWDetailItem> dataList = null;
    private Map<String, String> materPriceMap = null;

    private List<String> countList = new ArrayList<String>();

    private class ViewHolder {
        TextView commdityNameTextView;
        TextView commdityAllowCountTextView;
        NumEditText numEditText;
        TextView materialAgeTextView;
        TextView batchTextView;
        TextView priceTextView;
        Button delButton;
    }

    public FittingBackListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List<ManageWDetailItem> dataList) {
        this.dataList = dataList;
        initCountList();
    }

    private void initCountList() {
        for (int count = 0; count < dataList.size(); count++) {
            countList.add("1");
        }
    }

    public void setMap(Map<String, String> materPriceMap) {
        this.materPriceMap = materPriceMap;
    }

    public List<String> getCountList() {
        return countList;
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
            convertView = mInflater.inflate(R.layout.listview_item_fitting_back, null);
            holder = new ViewHolder();
            holder.commdityNameTextView = (TextView) convertView.findViewById(R.id.tv_commdity_name);
            holder.commdityAllowCountTextView = (TextView) convertView.findViewById(R.id.tv_commdity_allow_count);
            holder.numEditText = (NumEditText) convertView.findViewById(R.id.ned_num);
            holder.materialAgeTextView = (TextView) convertView.findViewById(R.id.tv_material_age);
            holder.batchTextView = (TextView) convertView.findViewById(R.id.tv_batch);
            holder.priceTextView = (TextView) convertView.findViewById(R.id.tv_price);
            holder.delButton = (Button) convertView.findViewById(R.id.btn_del);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ManageWDetailItem item = dataList.get(position);
        holder.commdityNameTextView.setText(item.getMaterDesc());
        holder.commdityAllowCountTextView.setText(item.getAllowCount() + item.getUnit());
        holder.materialAgeTextView.setText(item.getMaterialAge());
        holder.batchTextView.setText(item.getSupplier());
        holder.priceTextView.setText(materPriceMap.get(item.getMaterialCode()));

        // 15010551 设置numEditText的最大值
        holder.numEditText.setMaxNum(item.getAllowCount());

        holder.numEditText.setOnNumChangedListener(new NumEditText.NumChangedListener() {
            @Override
            public void onAddClick() {
                String count = holder.numEditText.getNumString();
                if (Double.parseDouble(count) > Double.parseDouble(item.getAllowCount())) {
                    T.showShort(mContext, mContext.getString(R.string.alert_max_ava_num) + item.getAllowCount());
                    holder.numEditText.setNum(item.getAllowCount());
                    holder.numEditText.setShake();
                    countList.set(position, item.getAllowCount());
                } else {
                    countList.set(position, count);
                }
            }

            @Override
            public void onMinusClick() {
                String count = holder.numEditText.getNumString();
                if (Double.parseDouble(count) > Double.parseDouble(item.getAllowCount())) {
                    T.showShort(mContext, mContext.getString(R.string.alert_max_ava_num) + item.getAllowCount());
                    holder.numEditText.setNum(item.getAllowCount());
                    holder.numEditText.setShake();
                    countList.set(position, item.getAllowCount());
                } else {
                    countList.set(position, count);
                }
            }

            @Override
            public void onEditChange() {
                String count = holder.numEditText.getNumString();
                if (Double.parseDouble(count) > Double.parseDouble(item.getAllowCount())) {
                    T.showShort(mContext, mContext.getString(R.string.alert_max_ava_num) + item.getAllowCount());
                    holder.numEditText.setNum(countList.get(position));
                    holder.numEditText.setShake();
                    countList.set(position, item.getAllowCount());
                } else {
                    countList.set(position, count);
                }
            }
        });

        holder.numEditText.setNum(countList.get(position));

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getString(R.string.dialog_delete_alert));
                builder.setPositiveButton(mContext.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message msg = new Message();
                        msg.what = FittingBackActivity.HANDLER_CODE_DEL_ITEM;
                        msg.arg1 = position;
                        dataList.remove(position);
                        countList.remove(position);
                        FittingBackActivity.handler.sendMessage(msg);

                    }
                });
                builder.setNegativeButton(mContext.getString(R.string.cancel), null);
                builder.show();
            }
        });

        return convertView;
    }
}
