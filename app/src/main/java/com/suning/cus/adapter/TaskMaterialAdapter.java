package com.suning.cus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.suning.cus.R;
import com.suning.cus.activity.TaskFinishActivity;
import com.suning.cus.bean.Material;
import com.suning.cus.constants.ArrayData;
import com.suning.cus.event.MaterialDeleteEvent;
import com.suning.cus.extras.NumEditText;
import com.suning.cus.utils.T;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 任务完成界面里的材料Adapter
 * Created by 14110105 on 2015/3/18.
 */
public class TaskMaterialAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Material> mMaterials;

    public TaskMaterialAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setMaterials(List<Material> materials) {
        mMaterials = materials;
    }

    @Override
    public int getCount() {
        return mMaterials == null ? 0 : mMaterials.size();
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

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_task_material, null, false);

            holder.materialName = (TextView) convertView.findViewById(R.id.tv_material_name);
            holder.materialNum = (NumEditText) convertView.findViewById(R.id.net_material_num);
            holder.materialAssurance = (Spinner) convertView.findViewById(R.id
                    .sp_material_assurance);
            holder.materialPrice = (TextView) convertView.findViewById(R.id.tv_material_price);
            holder.delete = (Button) convertView.findViewById(R.id.bt_delete);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Material material = mMaterials.get(position);

        // 如果质保标识有值，就初始化质保标识的spinner.

        if (material.getAssuranceList() != null && material.getAssuranceList().size() > 0) {
            ArrayList<String> assuranceNames = new ArrayList<>();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_spinner_item);

            for (String assuranceCode : material.getAssuranceList()) {
                assuranceNames.add(ArrayData.getAssuranceName(assuranceCode));
            }

            adapter.addAll(assuranceNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.materialAssurance.setAdapter(adapter);

            holder.materialAssurance.setOnItemSelectedListener(new AdapterView
                    .OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
                    String selected = parent.getSelectedItem().toString();
                    material.setMaterAssurance(ArrayData.getAssuranceCode(selected));
                    ((TaskFinishActivity) mContext).countMaterialPrice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            /*如果之前已经选择过，则默认加载……*/
            if (!TextUtils.isEmpty(material.getMaterAssurance())) {
                String assuranceName = ArrayData.getAssuranceName(material.getMaterAssurance());
                if (assuranceName != null) {
                    int index = assuranceNames.indexOf(assuranceName);
                    holder.materialAssurance.setSelection(index);
                }
            }
        } else {
            holder.materialAssurance.setVisibility(View.GONE);
            material.setMaterAssurance(null);
        }

        holder.materialName.setText(material.getMaterCode() + "  " + material.getMaterDesc());
        holder.materialPrice.setText(material.getMaterPrice());
        holder.materialNum.setNum(material.getMaterNumber());

        final NumEditText numText = holder.materialNum;

        /* 如果不是配件就允许输入小数，如果是配件就不允许输入小数 */
        if (!material.getMaterType().equals("Z011")) {
            holder.materialNum.enableInputDecimal(true);
        }

        if (material.getStore() != null) {
            holder.materialNum.setMaxNum(material.getStore());
        }

        holder.materialNum.setOnNumChangedListener(new NumEditText.NumChangedListener() {
            @Override
            public void onAddClick() {
                /* 判断是否有库存限制 */
                if (material.getStore() != null) {
                    Double store = Double.valueOf(material.getStore());
                    Double number = Double.valueOf(numText.getNumString());

                    //如果相加后大于库存数则取最大库存数
                    if (number > store) {
                        T.showShort(mContext, R.string.toast_beyond_store);
                        numText.setShake();
                        numText.setNum(material.getStore());
                    }
                }

                material.setMaterNumber(numText.getNumString());

                ((TaskFinishActivity) mContext).onMaterialNumChanged();
            }

            @Override
            public void onMinusClick() {

                /* 判断是否有库存限制, 规避bug*/
                if (material.getStore() != null) {
                    Double store = Double.valueOf(material.getStore());
                    Double number = Double.valueOf(numText.getNumString());

                    //如果相加后大于库存数则取最大库存数
                    if (number > store) {
                        T.showShort(mContext, R.string.toast_beyond_store);
                        numText.setShake();
                        numText.setNum(material.getStore());
                    }
                }

                material.setMaterNumber(numText.getNumString());
                ((TaskFinishActivity) mContext).onMaterialNumChanged();
            }

            @Override
            public void onEditChange() {

                Double number = Double.valueOf(numText.getNumString());
                /* 判断是否有库存限制*/
                if (material.getStore() != null) {  // 配件的情况
                    Double store = Double.valueOf(material.getStore());

                    // 如果输入大于库存数则取最大库存数
                    if (number > store) {
                        T.showShort(mContext, R.string.toast_beyond_store);
                        numText.setNum(material.getStore());
                    }
                } else {  // 材料的情况
                    /*取小数点后三位*/
                    DecimalFormat df = new DecimalFormat("#.###");
                    numText.setNum(df.format(number));
                }
                material.setMaterNumber(numText.getNumString());

                ((TaskFinishActivity) mContext).onMaterialNumChanged();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.dialog_delete_alert);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mMaterials.remove(material);
                                notifyDataSetChanged();

                                EventBus.getDefault().post(new MaterialDeleteEvent(material));

                            }
                        }

                );
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView materialName;
        public NumEditText materialNum;
        public Spinner materialAssurance;
        public TextView materialPrice;
        public Button delete;
    }


}
