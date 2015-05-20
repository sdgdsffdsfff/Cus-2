package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.suning.cus.R;
import com.suning.cus.activity.ApplianceTypeApplyActivity;
import com.suning.cus.activity.SearchMaterialActivity;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.event.BackupEvent;
import com.suning.cus.utils.T;

import java.util.List;

import de.greenrobot.event.EventBus;

public class BackUpFragment extends Fragment implements View.OnClickListener {

    private FragmentManager mFragmentManager;

    private MyFavBackupFragment mMyFavBackupFragment;
    private ShopCarBackupFragment mShopCarBackupFragment;

    /**
     * 15010551
     */

    // Activity Request Code: 配件申请
    private int REQUEST_CODE_ACCESS_APPLY = 0x0001;

    // Activity Request Code: 电器型号申配
    private int REQUEST_CODE_MACHINE_APPLE = 0x0002;

    /**
     * UI相关
     */
//    private FrameLayout backupOps;

    private LinearLayout searchBtn;

    private LinearLayout myFavBtn;

    private LinearLayout shopCarBtn;

    private PopupWindow fittingSearchPopup;

    private View myFavUnderLine, shopCarUnderLine;

    private Fragment currentFragment = null;

    public BackUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        searchBtn = (LinearLayout) view.findViewById(R.id.btn_fitting_search);
        searchBtn.setOnClickListener(this);
        myFavBtn = (LinearLayout) view.findViewById(R.id.btn_my_fav);
        myFavBtn.setOnClickListener(this);
        shopCarBtn = (LinearLayout) view.findViewById(R.id.btn_shop_car);
        shopCarBtn.setOnClickListener(this);

        myFavUnderLine = view.findViewById(R.id.view_underline_my_fav);
        shopCarUnderLine = view.findViewById(R.id.view_underline_shop_car);

        mFragmentManager = getActivity().getSupportFragmentManager();

        //设置默认的显示页
        setCurrentFlag(1, null);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().register(currentFragment);
        }
    }

    @Override
    public void onStop() {
        if (mMyFavBackupFragment != null) {
            EventBus.getDefault().unregister(mMyFavBackupFragment);
        }
        if (mShopCarBackupFragment != null) {
            EventBus.getDefault().unregister(mShopCarBackupFragment);
        }
//        EventBus.getDefault().unregister(currentFragment);
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACCESS_APPLY || requestCode == REQUEST_CODE_MACHINE_APPLE) {
            if (resultCode == Activity.RESULT_OK) {
                String materCode = data.getStringExtra("material_code");
                String materDesc = data.getStringExtra("material_name");
                String materPrice = data.getStringExtra("material_price");
                ShopCarBackupData shopData = new ShopCarBackupData();
                shopData.setCmmdtyCode(materCode);
                shopData.setCmmdtyName(materDesc);
                shopData.setCmmdtyPrice(materPrice);
                shopData.setCmmdtyNum("1");
                DbUtils dbUtils = DbUtils.create(getActivity());
                try {
                    List<ShopCarBackupData> dataList = dbUtils.findAll(Selector.from(ShopCarBackupData.class).where("cmmdtyCode", "=", materCode));
                    if (dataList.size() == 0) {
                        List<ShopCarBackupData> allShopCarData = dbUtils.findAll(ShopCarBackupData.class);
                        if (allShopCarData.size() >= 10) {
                            T.showShort(getActivity(), getString(R.string.shopcar_is_full));
                        } else {
                            dbUtils.save(shopData);
                            T.showShort(getActivity(), getString(R.string.add_shopcar_success));
                        }
                    } else {
                        ShopCarBackupData mData = dataList.get(0);
                        mData.setCmmdtyPrice(shopData.getCmmdtyPrice());
                        mData.setCmmdtyName(shopData.getCmmdtyName());
                        dbUtils.update(mData);
                        T.showShort(getActivity(), getString(R.string.add_shopcar_success));
                    }

                    //切换至购物车的fragment
                    setCurrentFlag(2, null);

                } catch (DbException e) {
                    e.printStackTrace();
                    T.showShort(getActivity(), getString(R.string.add_shopcar_fail));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_fitting_search:
                setCurrentFlag(0, v);
                break;
            case R.id.btn_my_fav:
                setCurrentFlag(1, v);
                break;
            case R.id.btn_shop_car:
                setCurrentFlag(2, v);
                break;
            case R.id.fitting_borrow:
                if (fittingSearchPopup != null) {
                    fittingSearchPopup.dismiss();
                    intent.setClass(getActivity(), SearchMaterialActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ACCESS_APPLY);
                }
                break;
            case R.id.machine_borrow:
                if (fittingSearchPopup != null) {
                    fittingSearchPopup.dismiss();
                    intent.setClass(getActivity(), ApplianceTypeApplyActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MACHINE_APPLE);
                }
                break;
        }
    }

    private void setCurrentFlag(int flag, View v) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (flag) {
            case 0:
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                // 引入窗口配置文件
                View view = inflater.inflate(R.layout.popup_search_backup, null);
                // 创建PopupWindow对象
                if (fittingSearchPopup == null) {
                    fittingSearchPopup = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                }
                // 需要设置一下此参数，点击外边可消失
                fittingSearchPopup.setBackgroundDrawable(new BitmapDrawable());
                //设置点击窗口外边窗口消失
                fittingSearchPopup.setOutsideTouchable(true);
                // 设置此参数获得焦点，否则无法点击
                fittingSearchPopup.setFocusable(true);
                fittingSearchPopup.showAsDropDown(v);
                view.findViewById(R.id.fitting_borrow).setOnClickListener(this);
                view.findViewById(R.id.machine_borrow).setOnClickListener(this);
                break;
            case 1:
                if (mMyFavBackupFragment == null) {
                    mMyFavBackupFragment = new MyFavBackupFragment();
                }
//                transaction.replace(R.id.fl_backup_ops, mMyFavBackupFragment);
                switchFragment(mMyFavBackupFragment);
                //设置下划线颜色
                myFavUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
                shopCarUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case 2:
                if (mShopCarBackupFragment == null) {
                    mShopCarBackupFragment = new ShopCarBackupFragment();
                }
//                transaction.replace(R.id.fl_backup_ops, mShopCarBackupFragment);
                switchFragment(mShopCarBackupFragment);
                //设置下划线颜色
                myFavUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
                shopCarUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
                break;
        }
        transaction.commit();
    }

    private void switchFragment(Fragment to) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!to.isAdded()) {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.add(R.id.fl_backup_ops, to).commit();
            } else {
                transaction.hide(currentFragment).add(R.id.fl_backup_ops, to).commit();
//                EventBus.getDefault().unregister(currentFragment);
            }
        } else {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.show(to).commit();
            } else {
                transaction.hide(currentFragment).show(to).commit();
//                EventBus.getDefault().unregister(currentFragment);
            }
        }
        currentFragment = to;
        if (!EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().register(currentFragment);
        }
    }

    /**
     * 接收BackUpFragment Event
     *
     * @param event
     */
    public void onEvent(BackupEvent event) {

    }
}
