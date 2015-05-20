package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.suning.cus.R;
import com.suning.cus.activity.AtpCheckResActivity;
import com.suning.cus.adapter.ShopCarMaterialAdapter;
import com.suning.cus.bean.CommodityListATP;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.event.ShopcarBackupEvent;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.T;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCarMaterialFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * 15010551
     */
    public static final int HANDLER_CODE_SHOPCAR_DEL = 0x0001;

    public static final int REQUEST_CODE_ATP = 1;


    private ListView shopCarBackupListView;


    public List<ShopCarBackupData> dataList;
    public ShopCarMaterialAdapter mAdapter;

    private Button shopCarClearButton;

    private Button shopCarOkButton;

    private ProgressDialog progressDialog;


    public ShopCarMaterialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_shop_car_backup, container, false);

        shopCarBackupListView = (ListView) view.findViewById(R.id.lv_shop_car_backup);
        mAdapter = new ShopCarMaterialAdapter(getActivity());
        mAdapter.setDataList(dataList);
        shopCarBackupListView.setAdapter(mAdapter);

        shopCarClearButton = (Button) view.findViewById(R.id.bt_shopcar_backup_clear);
        shopCarClearButton.setOnClickListener(this);
        shopCarOkButton = (Button) view.findViewById(R.id.bt_shopcar_backup_ok);
        shopCarOkButton.setOnClickListener(this);
        return view;

    }


    public void setData(List<ShopCarBackupData> data) {
        this.dataList = data;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_shopcar_backup_clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确认清空购物车?");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.cleanDataList();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                break;
            case R.id.bt_shopcar_backup_ok:
                List<ShopCarBackupData> dataList = mAdapter.getDataList();
                int listSize = dataList.size();
                if (listSize > 0) {
                    StringBuffer a = new StringBuffer();
                    Map<String, String> codeNameMap = new HashMap<String, String>(); //商品编码和商品描述的map
                    Gson gson = new Gson();
                    a.append("[");
                    for (int count = 0; count < dataList.size(); count++) {
                        ShopCarBackupData shopcarData = dataList.get(count);
                        CommodityListATP atpData = new CommodityListATP();
                        String cmmdtyCode = shopcarData.getCmmdtyCode();
                        String cmmdtyName = shopcarData.getCmmdtyName();
                        String cmmdtyNum = shopcarData.getCmmdtyNum();
                        atpData.setCommodity(cmmdtyCode);
                        atpData.setCommodityNumber(cmmdtyNum);
                        if (!codeNameMap.containsKey(cmmdtyCode)) {
                            codeNameMap.put(cmmdtyCode, cmmdtyName);
                        }
                        String str = gson.toJson(atpData);
                        a.append(str);
                        if (count != dataList.size() - 1) {
                            a.append(",");
                        }
                    }
                    a.append("]");

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AtpCheckResActivity.class);
                    intent.putExtra(AtpCheckResActivity.ATP_REQUEST_TYPE, AtpCheckResActivity.ATP_REQUEST_TYPE_TASK);
                    intent.putExtra(AtpCheckResActivity.COMMDTY_LIST, a.toString());
                    intent.putExtra(AtpCheckResActivity.CODE_NAME_MAP, MyUtils.toByteArray(codeNameMap));
                    startActivityForResult(intent, REQUEST_CODE_ATP);

                } else {
                    T.showShort(getActivity(), "购物车为空");
                }

                break;

        }
    }

    public void onEvent(ShopcarBackupEvent event) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ATP && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }

    }
}
