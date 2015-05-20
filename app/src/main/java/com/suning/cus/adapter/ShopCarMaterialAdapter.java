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
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.extras.NumEditText;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/23.
 */
public class ShopCarMaterialAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<ShopCarBackupData> dataList;

    private class ViewHolder {
        TextView cmmdtyNameTextView;
        NumEditText cmmdtyNumNED;
        Button shopcarDelButton;
        TextView cmmdtyPriceTextView;
    }

    public ShopCarMaterialAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List<ShopCarBackupData> dataList) {
        this.dataList = dataList;
    }

    public void cleanDataList() {
        if (dataList != null) {
            dataList.clear();
        }
    }

    public List<ShopCarBackupData> getDataList() {
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
            convertView = mInflater.inflate(R.layout.listview_item_shop_car_backup, null);
            holder = new ViewHolder();
            holder.cmmdtyNameTextView = (TextView) convertView.findViewById(R.id.tv_shopcar_cmmdty_name);
            holder.cmmdtyNumNED = (NumEditText) convertView.findViewById(R.id.ned_shopcar_cmmdty_num);
            holder.shopcarDelButton = (Button) convertView.findViewById(R.id.bt_shopcar_backup_del);
            holder.cmmdtyPriceTextView = (TextView) convertView.findViewById(R.id.tv_shopcar_cmmdty_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ShopCarBackupData data = dataList.get(position);
        holder.cmmdtyNameTextView.setText(data.getCmmdtyName());
        holder.cmmdtyPriceTextView.setText(data.getCmmdtyPrice() + "元");
        holder.cmmdtyNumNED.setOnNumChangedListener(new NumEditText.NumChangedListener() {
            @Override
            public void onAddClick() {
                data.setCmmdtyNum(holder.cmmdtyNumNED.getNumString());

            }

            @Override
            public void onMinusClick() {
                data.setCmmdtyNum(holder.cmmdtyNumNED.getNumString());


            }

            @Override
            public void onEditChange() {
                data.setCmmdtyNum(holder.cmmdtyNumNED.getNumString());
            }
        });
        holder.cmmdtyNumNED.setNum(data.getCmmdtyNum());
        holder.shopcarDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dataList.remove(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.dialog_delete_alert);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataList.remove(position);
                        notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        return convertView;
    }
}
