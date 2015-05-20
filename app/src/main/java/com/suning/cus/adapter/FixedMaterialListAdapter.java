package com.suning.cus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.TaskFinishOtherDayActivity;
import com.suning.cus.bean.Material;

import java.util.List;

/**
 * 次日，另约界面用到的配件adapter
 * Created by 15010551 on 2015/3/30.
 */
public class FixedMaterialListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Material> dataList = null;

    private static class ViewHolder {
        TextView nameView;
        TextView numberView;
        TextView priceView;
        Button deleteBtn;
    }

    public FixedMaterialListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List<Material> dataList) {
        this.dataList = dataList;
    }

    public List<Material> getDataList() {
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
            convertView = mInflater.inflate(R.layout.list_item_fixed_material, null);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.numberView = (TextView) convertView.findViewById(R.id.tv_number);
            holder.priceView = (TextView) convertView.findViewById(R.id.tv_price);
            holder.deleteBtn = (Button) convertView.findViewById(R.id.bt_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Material data = dataList.get(position);
        holder.nameView.setText(data.getMaterCode() + "  " + data.getMaterDesc() );
        holder.numberView.setText(data.getMaterNumber());
        holder.priceView.setText(data.getMaterPrice());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.dialog_delete_alert);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataList.remove(data);
                        notifyDataSetChanged();

                        ((TaskFinishOtherDayActivity) mContext).onMaterialDeleted();
                        ((TaskFinishOtherDayActivity) mContext).countMaterialPrice();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });
        return convertView;
    }

}
