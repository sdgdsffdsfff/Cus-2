package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.activity.AtpCheckResActivity;
import com.suning.cus.adapter.ShopCarBackupListAdapter;
import com.suning.cus.bean.CommodityListATP;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.event.ShopcarBackupEvent;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.T;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCarBackupFragment extends Fragment implements View.OnClickListener {

    /**
     * 15010551
     */

    // Handler Msg Code: 从购物车中删除
    public static final int HANDLER_CODE_SHOPCAR_DEL = 0x0001;

    // Activity Request Code: 进行ATP检查
    public static final int REQUEST_CODE_APT_CHECK = 0x0001;

    /**
     * 业务相关
     */
    private DbUtils dbUtils;

    private List<ShopCarBackupData> dataList;

    private ShopCarBackupListAdapter mAdapter;

    private AnimationAdapter mAnimAdapter;

    public static Handler handler;

    /**
     * UI相关
     */
    private ListView shopCarBackupListView;

    private Button shopCarClearButton;

    private Button shopCarOkButton;

    public ShopCarBackupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_CODE_SHOPCAR_DEL:
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };

        View view = inflater.inflate(R.layout.fragment_shop_car_backup, container, false);

        dbUtils = DbUtils.create(getActivity());
        try {
            dataList = dbUtils.findAll(ShopCarBackupData.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        shopCarBackupListView = (ListView) view.findViewById(R.id.lv_shop_car_backup);
        mAdapter = new ShopCarBackupListAdapter(getActivity());
        mAdapter.setDataList(dataList);

        mAnimAdapter = new AlphaInAnimationAdapter(mAdapter);
        mAnimAdapter.setAbsListView(shopCarBackupListView);
        shopCarBackupListView.setAdapter(mAnimAdapter);
//        shopCarBackupListView.setAdapter(mAdapter);

        shopCarClearButton = (Button) view.findViewById(R.id.bt_shopcar_backup_clear);
        shopCarClearButton.setOnClickListener(this);
        shopCarOkButton = (Button) view.findViewById(R.id.bt_shopcar_backup_ok);
        shopCarOkButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        //   super.onHiddenChanged(hidden);
        // System.out.println("ShopCar onHiddenChanged");
        if (hidden == false) {
            //    System.out.println("ShopCar onHiddenChanged false");
            try {
                dataList = dbUtils.findAll(ShopCarBackupData.class);
                if (mAdapter == null) {
                    mAdapter = new ShopCarBackupListAdapter(getActivity());
                    mAdapter.setDataList(dataList);
                    shopCarBackupListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setDataList(dataList);
                    mAdapter.notifyDataSetChanged();
                }

            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            //    System.out.println("ShopCar onHiddenChanged true");
            if (mAdapter != null) {
                mAdapter.getDataList();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_shopcar_backup_clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.sure_to_clean_shopcar));
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbUtils dbUtils = DbUtils.create(getActivity());
                        try {
                            dbUtils.deleteAll(ShopCarBackupData.class);
                            mAdapter.cleanDataList();
                            handler.sendEmptyMessage(HANDLER_CODE_SHOPCAR_DEL);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
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
                    intent.putExtra(AtpCheckResActivity.ATP_REQUEST_TYPE, AtpCheckResActivity.ATP_REQUEST_TYPE_BACKUP);
                    intent.putExtra(AtpCheckResActivity.COMMDTY_LIST, a.toString());
                    intent.putExtra(AtpCheckResActivity.CODE_NAME_MAP, MyUtils.toByteArray(codeNameMap));
                    //    startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE_APT_CHECK);
                    //    atpCheck(a.toString());
                } else {
                    T.showShort(getActivity(), getString(R.string.shopcar_is_empty));
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_APT_CHECK) {
            if (resultCode == Activity.RESULT_OK) {
                // 15010551 清空购物车
                DbUtils dbUtils = DbUtils.create(getActivity());
                try {
                    dbUtils.deleteAll(ShopCarBackupData.class);
                    mAdapter.cleanDataList();
                    handler.sendEmptyMessage(HANDLER_CODE_SHOPCAR_DEL);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onEvent(ShopcarBackupEvent event) {

    }

}
