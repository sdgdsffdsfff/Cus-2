package com.suning.cus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.fragment.MyFavMaterialFragment;
import com.suning.cus.bean.BackupFavData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15010551 on 2015/3/19.
 */
public class MyFavMaterialListAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context mContext;

    private List<BackupFavData> dataList = null;

    private boolean checkStatus = false;

    private int mScreentWidth;

    //15010551 判断是否处在操作状态，即删除是否可见
    boolean isOps;

    //15010551 记录上一次操作的位置
    int lastPosition;

    private class ViewHolder {
        TextView materNameTextView;
        TextView materCodeTextView;
        ImageView addShopCarImageView;
    //    CheckBox delFavCheckBox;
        LinearLayout myFavItemLinearLayout;
        HorizontalScrollView hSView;
        View action;
        Button delFavButtonSlide;
        View contentView;
    }

    public MyFavMaterialListAdapter(Context context, int screenWidth) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mScreentWidth = screenWidth;
        isOps = false;
        lastPosition = -1;
    }

    public void setDataList(List<BackupFavData> dataList) {
        this.dataList = dataList;
    }

    public void appendDataList(List<BackupFavData> dataList) {
        int size = 0;
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
            size = 0;
        } else {
            size = this.dataList.size();
        }

        this.dataList.addAll(dataList);
    }

    public List<BackupFavData> getDataList() {
        return dataList;
    }

    public void setCheckStatus(boolean isCheck) {
        checkStatus = isCheck;
    }

    public boolean getCheckStatus() {
        return checkStatus;
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
            convertView = mInflater.inflate(R.layout.listview_item_my_fav_backup_slide, null);
            holder = new ViewHolder();
            holder.hSView = (HorizontalScrollView) convertView.findViewById(R.id.hsv);
            holder.action = convertView.findViewById(R.id.ll_action);
            holder.materCodeTextView = (TextView) convertView.findViewById(R.id.tv_backup_cmmdty_code);
            holder.materNameTextView = (TextView) convertView.findViewById(R.id.tv_backup_cmmdty_name);
            holder.addShopCarImageView = (ImageView) convertView.findViewById(R.id.iv_backup_add_shopcar);
            holder.delFavButtonSlide = (Button) convertView.findViewById(R.id.bt_my_fav_del_slide);
            holder.myFavItemLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_backup_my_fav_item);
            holder.contentView = convertView.findViewById(R.id.ll_content);
            holder.addShopCarImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isOps) {
                        isOps = false;
                        notifyDataSetChanged();
                    }
                    return false;
                }
            });

            ViewGroup.LayoutParams lp = holder.contentView.getLayoutParams();
            lp.width = mScreentWidth;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BackupFavData data = dataList.get(position);
        holder.addShopCarImageView.setEnabled(true);
        holder.materNameTextView.setText(data.getCmmdtyName());
        holder.materCodeTextView.setText(data.getCmmdtyCode());
        if (checkStatus) {
            holder.addShopCarImageView.setVisibility(View.GONE);
        } else {
            holder.addShopCarImageView.setVisibility(View.VISIBLE);
            holder.addShopCarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MyFavMaterialFragment.CMMDTY_CODE, data.getCmmdtyCode());
                    bundle.putString(MyFavMaterialFragment.CMMDTY_NAME, data.getCmmdtyName());
                    Message msg = new Message();
                    msg.what = MyFavMaterialFragment.HANDLER_CODE_ADD_FAV;
                    msg.setData(bundle);
                    MyFavMaterialFragment.handler.sendMessage(msg);
                }
            });
        }

        convertView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        ViewHolder viewHolder = (ViewHolder) v.getTag();

                        int scrollX = viewHolder.hSView.getScrollX();

                        int actionW = viewHolder.action.getWidth();

                        if (scrollX < actionW / 2)
                        {
                            viewHolder.hSView.smoothScrollTo(0, 0);
                            isOps = false;
                        }
                        else
                        {
                            viewHolder.hSView.smoothScrollTo(actionW, 0);
                            isOps = true;
                            holder.addShopCarImageView.setEnabled(false);
                        }
                        lastPosition = position;
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        // if (lastPosition != position) {
                        if (isOps) {
                            isOps = false;
                            notifyDataSetChanged();
                        }
                        lastPosition = position;
                        return true;
                }
                return false;
            }
        });

        if (holder.hSView.getScrollX() != 0)
        {
            holder.hSView.smoothScrollTo(0, 0);
        }

        holder.delFavButtonSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(MyFavMaterialFragment.CMMDTY_CODE, data.getCmmdtyCode());
                bundle.putInt(MyFavMaterialFragment.CMMDTY_POSITION, position);
                Message msg = new Message();
                msg.what = MyFavMaterialFragment.HANDLER_CODE_DEL_FAV;
                msg.setData(bundle);
                MyFavMaterialFragment.handler.sendMessage(msg);
            }
        });

        return convertView;
    }
}
