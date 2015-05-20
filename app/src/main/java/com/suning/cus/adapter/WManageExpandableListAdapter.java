package com.suning.cus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.WManageActivity;
import com.suning.cus.bean.ManageWDetailData;
import com.suning.cus.bean.ManageWDetailItem;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 15010551 on 2015/3/16.
 */
public class WManageExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    private String WManageActivityType;

    private List<ManageWDetailData> dataList;

    private Map<String, Boolean> isCheckedMap = new HashMap<String, Boolean>();

    private Map<String, ManageWDetailItem> selectedItemMap = new HashMap<String, ManageWDetailItem>();

    public static class GroupViewHolder {
        LinearLayout wDetailOrderLinearLayout; // W库管理（订单占用查询）
        ImageView groupExpandImageView;
        TextView materialDescTextView;
        TextView materialCodeTextView;
        TextView allowCountTextView;
        TextView occupyCountTextView;
        TextView materialAgeTextView;
    }

    public static class ChildViewHolder {
        LinearLayout itemChildLinearLayout;  //材配查询时才会用到
        TextView supplierTextView;
        CheckBox supplierCheckBox;
        TextView materialCodeTextView;
        TextView allowCountTextView;
        TextView occupyCountTextView;
        TextView materialAgeTextView;
    }

    public WManageExpandableListAdapter(Context context, String WManageActivityType) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.WManageActivityType = WManageActivityType;
    }

    public void setDataList(List<ManageWDetailData> dataList) {
        this.dataList = dataList;
    }

    public void appendDataList(List<ManageWDetailData> dataList) {
        int size = 0;
        if (this.dataList == null) {
            this.dataList = new ArrayList<ManageWDetailData>();
            size = 0;
        } else {
            size = this.dataList.size();
        }

        if (dataList != null) {
            this.dataList.addAll(dataList);
        }
    }

    public Collection<ManageWDetailItem> getSelectedItems() {
        Collection<ManageWDetailItem> c = selectedItemMap.values();
        return c;
    }

    @Override
    public int getGroupCount() {
        //return wManageGroupInfos.size();
        return this.dataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return map.get(groupPosition).size();
        return this.dataList.get(groupPosition).getItem().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return wManageGroupInfos.get(groupPosition);
        return this.dataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //return map.get(groupPosition).get(childPosition);
        return this.dataList.get(groupPosition).getItem().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        /*
        View groupView = getLayoutInflater().inflate(R.layout.listview_item_group_w_manage, null);
        ((TextView)groupView.findViewById(R.id.tv_ware_desc)).setText(wManageGroupInfos.get(groupPosition).wareDesc);
        ((TextView)groupView.findViewById(R.id.tv_ware_model)).setText(wManageGroupInfos.get(groupPosition).wareModel);
        ((TextView)groupView.findViewById(R.id.tv_ware_matnr)).setText(wManageGroupInfos.get(groupPosition).wareMatnr);
        ((TextView)groupView.findViewById(R.id.tv_num_avai)).setText(wManageGroupInfos.get(groupPosition).numAvai);
        ((TextView)groupView.findViewById(R.id.tv_num_employ)).setText(wManageGroupInfos.get(groupPosition).numEmploy);
        ((TextView)groupView.findViewById(R.id.tv_day_avai)).setText(wManageGroupInfos.get(groupPosition).dayAvai);
        if (isExpanded) {
            ((ImageView)groupView.findViewById(R.id.imageview_group_expand)).setImageResource(R.drawable.ic_global_expand);
        } else {
            ((ImageView)groupView.findViewById(R.id.imageview_group_expand)).setImageResource(R.drawable.ic_global_no_expand);
        }
        return groupView;
        */

        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            if (this.WManageActivityType.equals(WManageActivity.NORMAL)) {  /* W库管理 */
                convertView = mInflater.inflate(R.layout.listview_item_group_w_manage, null, false);
                holder.materialAgeTextView = (TextView) convertView.findViewById(R.id.tv_material_age);
            } else if (this.WManageActivityType.equals(WManageActivity.JUMP)) {  /* 材配查询 */
                convertView = mInflater.inflate(R.layout.listview_item_group_w_manage_task, null, false);
            }
//            convertView = mInflater.inflate(R.layout.listview_item_group_w_manage_task, null, false);
//            holder.materialAgeTextView = (TextView) convertView.findViewById(R.id.tv_material_age);
            holder.groupExpandImageView = (ImageView) convertView.findViewById(R.id.imageview_group_expand);
            holder.materialDescTextView = (TextView) convertView.findViewById(R.id.tv_material_desc);
            holder.materialCodeTextView = (TextView) convertView.findViewById(R.id.tv_material_code);
            holder.allowCountTextView = (TextView) convertView.findViewById(R.id.tv_allow_count);
            holder.occupyCountTextView = (TextView) convertView.findViewById(R.id.tv_occupy_count);
            holder.wDetailOrderLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_w_detail_order);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        final ManageWDetailData data = dataList.get(groupPosition);
        if (data != null) {
            holder.materialDescTextView.setText(data.getMaterialDesc());
            holder.materialCodeTextView.setText(data.getMaterialCode());

            //15010551 让可用数量和占用数量显示整数，使用MathUtils
            data.setAllowCount(MathUtils.count(data.getAllowCount(), "", "+"));
            data.setOccupyCount(MathUtils.count(data.getOccupyCount(), "", "+"));
            holder.allowCountTextView.setText(data.getAllowCount());
            holder.occupyCountTextView.setText(data.getOccupyCount());

            if (!this.WManageActivityType.equals(WManageActivity.JUMP)) {  /* W库管理 或 默认*/
                holder.occupyCountTextView.setTextColor(mContext.getResources().getColor(R.color.blue));
                holder.materialAgeTextView.setText(data.getMaterialAge());
                if (data.getHighlight().equals("Y")) {
                    holder.materialAgeTextView.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    holder.materialAgeTextView.setTextColor(mContext.getResources().getColor(R.color.black));
                }
            }
            if (isExpanded) {
                holder.groupExpandImageView.setImageResource(R.drawable.ic_global_expand);
            } else {
                holder.groupExpandImageView.setImageResource(R.drawable.ic_global_no_expand);
            }
            if (this.WManageActivityType.equals(WManageActivity.JUMP)) {  /* 材配查询 */

            } else {  /* W库管理 */
                holder.wDetailOrderLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.what = WManageActivity.HANDLER_CODE_W_DETAIL_ORDER;
                        Bundle bundle = new Bundle();
                        bundle.putString(WManageActivity.MATNR, data.getMaterialCode());
                        msg.setData(bundle);
                        WManageActivity.handler.sendMessage(msg);
                    }
                });
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            if (this.WManageActivityType.equals(WManageActivity.NORMAL)) {  /* W库管理 */
                convertView = mInflater.inflate(R.layout.listview_item_child_w_manage, null, false);
                holder.supplierCheckBox = (CheckBox) convertView.findViewById(R.id.cb_supplier);

            } else if (this.WManageActivityType.equals(WManageActivity.JUMP)) {  /* 材配查询 */
                convertView = mInflater.inflate(R.layout.listview_item_child_w_manage_task, null, false);
                holder.itemChildLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_item_child);

            } else {    /* 默认为W库管理 */
                convertView = mInflater.inflate(R.layout.listview_item_child_w_manage, null, false);
            }
            //holder = new ChildViewHolder();
            holder.supplierTextView = (TextView) convertView.findViewById(R.id.tv_supplier);
            holder.materialCodeTextView = (TextView) convertView.findViewById(R.id.tv_material_code);
            holder.allowCountTextView = (TextView) convertView.findViewById(R.id.tv_allow_count);
            holder.occupyCountTextView = (TextView) convertView.findViewById(R.id.tv_occupy_count);
            holder.materialAgeTextView = (TextView) convertView.findViewById(R.id.tv_material_age);

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final ManageWDetailItem item = dataList.get(groupPosition).getItem().get(childPosition);
        if (item != null) {
            holder.supplierTextView.setText(item.getSupplier());
            holder.materialCodeTextView.setText(item.getMaterialCode());

            //15010551 让可用数量和占用数量显示整数，使用MathUtils
            item.setAllowCount(MathUtils.count(item.getAllowCount(), "", "+"));
            item.setOccupyCount(MathUtils.count(item.getOccupyCount(), "", "+"));
            holder.allowCountTextView.setText(item.getAllowCount());
            holder.occupyCountTextView.setText(item.getOccupyCount());

            holder.materialAgeTextView.setText(item.getMaterialAge());
            if (this.WManageActivityType.equals(WManageActivity.JUMP)) {  //材配申请
                holder.itemChildLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Double.parseDouble(item.getAllowCount()) == 0) {
                            T.showShort(mContext, mContext.getString(R.string.alert_access_max_num_zero));
                        } else {
                            //T.showShort(mContext, "点击了" + groupPosition + "," + childPosition);
                            Message msg = new Message();
                            msg.what = WManageActivity.HANDLER_CODE_W_DETAIL_ITEM;
                            Bundle bundle = new Bundle();
                            //bundle.putSerializable(BaseConstants.W_DETAIL_ITEM_BUNDLE_KEY, item);
                            bundle.putString("material_code", item.getMaterialCode());
                            bundle.putString("material_name", item.getMaterDesc());
                            bundle.putString("material_allow_count", item.getAllowCount());
                            bundle.putString("material_batch", item.getSupplier());
                            msg.setData(bundle);
                            WManageActivity.handler.sendMessage(msg);
                        }
                    }
                });
            } else {    //W库管理或默认
                if (Double.parseDouble(item.getAllowCount()) == 0) {    //如果可用数量为0，直接设置为不可勾选
                    holder.supplierCheckBox.setEnabled(false);
                } else {
                    holder.supplierCheckBox.setEnabled(true);
                }
                holder.supplierCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isCheckedMap.put(groupPosition + "+" + childPosition, true);
                            item.setMaterDesc(dataList.get(groupPosition).getMaterialDesc());
                            selectedItemMap.put(groupPosition + "+" + childPosition, item);
                        } else {
                            isCheckedMap.remove(groupPosition + "+" + childPosition);
                            selectedItemMap.remove(groupPosition + "+" + childPosition);
                        }
                    }
                });
                holder.supplierCheckBox.setChecked(isCheckedMap.containsKey(groupPosition + "+" + childPosition));
            }
        }

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
