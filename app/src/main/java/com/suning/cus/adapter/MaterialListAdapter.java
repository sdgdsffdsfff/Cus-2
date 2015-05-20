package com.suning.cus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.bean.Material;
import com.suning.cus.event.ClickFavEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 查询配件时的列表adapter
 * Created by 14110105 on 2015/3/17.
 */
public class MaterialListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isShowFavBtn = true;
    private List<Material> mMaterials;


    public MaterialListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 是否显示收藏按钮
     */
    public void showFavBtn(boolean shown) {
        isShowFavBtn = shown;
    }

    public void setmMaterials(List<Material> m) {
        mMaterials = m;
    }

    @Override
    public int getCount() {
        return mMaterials.size();
    }

    @Override
    public Object getItem(int position) {
        return mMaterials.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_one_line_three_row, null, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.tv_text_1);
            holder.code = (TextView) convertView.findViewById(R.id.tv_text_2);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Material material = mMaterials.get(position);

        if (material != null) {
            holder.name.setText(material.getMaterDesc());
            holder.code.setText(material.getMaterCode());

            if (isShowFavBtn) {

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!material.isFavorite()) {

                            EventBus.getDefault().post(new ClickFavEvent(true, material));
                            holder.image.setImageResource(R.drawable.ic_favorite_selected);
                        } else {
                            EventBus.getDefault().post(new ClickFavEvent(false, material));
                            holder.image.setImageResource(R.drawable.ic_favorite_nor);
                        }

                    }
                });

            } else {
                holder.image.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        private TextView name;
        private TextView code;
        private ImageView image;
    }
}
