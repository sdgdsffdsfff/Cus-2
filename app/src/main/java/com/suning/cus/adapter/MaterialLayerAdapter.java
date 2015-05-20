package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.MaterialLayer;

import java.util.List;


public class MaterialLayerAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    List<MaterialLayer> mLayers;

    public MaterialLayerAdapter(Context context)

    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void setLayers(List<MaterialLayer> layers) {
        this.mLayers = layers;
    }


    @Override
    public int getCount() {
        return mLayers.size();
    }

    @Override
    public Object getItem(int position) {
        return mLayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_one_line_two_row, null);

            holder = new ViewHolder();
            holder.searchNameText = (TextView) convertView.findViewById(R.id.tv_text_2);
            holder.searchCodeText = (TextView) convertView.findViewById(R.id.tv_text_1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MaterialLayer layer = mLayers.get(position);

        holder.searchNameText.setText(layer.getMaterLayerDesc());
        holder.searchCodeText.setText(layer.getMaterLayerCode());
        return convertView;
    }

    private static class ViewHolder {
        private TextView searchNameText;
        private TextView searchCodeText;
    }

}
