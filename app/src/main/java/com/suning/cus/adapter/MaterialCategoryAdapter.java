package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.MaterialCategory;

import java.util.List;

/**
 * Created by 14110105 on 2015/3/19.
 */
public class MaterialCategoryAdapter extends BaseAdapter {


    private LayoutInflater mInflater;
    private Context mContext;

    List<MaterialCategory> mCategories;

    public MaterialCategoryAdapter(Context context)

    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCategories(List<MaterialCategory> list) {
        this.mCategories = list;
    }


    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder  holder;

        if (convertView == null) {
            convertView = mInflater
                    .inflate(R.layout.list_item_one_line_two_row, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView
                    .findViewById(R.id.tv_text_1);
            holder.code = (TextView) convertView
                    .findViewById(R.id.tv_text_2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MaterialCategory category = mCategories.get(position);

        holder.name.setText(category.getMaterCategoryDesc());
        holder.code.setText(category.getMaterCategoryCode());
        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView code;
    }
}
