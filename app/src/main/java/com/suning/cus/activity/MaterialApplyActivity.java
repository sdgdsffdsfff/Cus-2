package com.suning.cus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.suning.cus.R;
import com.suning.cus.activity.fragment.MyFavMaterialFragment;
import com.suning.cus.activity.fragment.OnFragmentInteractionListener;
import com.suning.cus.activity.fragment.ShopCarMaterialFragment;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 配件申请界面
 * Created by 14110105 on 2015/3/18.
 */
public class MaterialApplyActivity extends BaseActivity
        implements View.OnClickListener, OnFragmentInteractionListener {

    private OnFragmentInteractionListener mListener;
    private FragmentManager mFragmentManager;

    private MyFavMaterialFragment mMyFavMaterialFragment;
    private ShopCarMaterialFragment mShopCarmaterialFragment;

    /**
     * 15010551
     */
    private static final int REQUEST_CODE_ACCESS_APPLY = 0x0001;
    private static final int REQUEST_CODE_MACHINE_APPLY = 0x0002;
    private static final int REQUEST_CODE_ONE_KEY_APPLY = 0x0003;

//    private FrameLayout backupOps;
    private Button searchBtn;
    private Button myFavBtn;
    private Button shopCarBtn;


    private PopupWindow fittingSearchPopup;
    private View myFavUnderLine;
    private View shopCarUnderLine;

    private Fragment currentFragment = null;

    /**
     * Intent传递过来的值
     */
    private String mProductCode;
    private String mProductName;

    public List<ShopCarBackupData> mMaterials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.fragment_material_apply);
        setTitle(getString(R.string.title_material_apply));
        initViews();
    }

    private void initViews() {

        mMaterials = new ArrayList<>();

        mProductCode = getIntent().getStringExtra("product_code");
        mProductName = getIntent().getStringExtra("product_name");

        searchBtn = (Button) findViewById(R.id.btn_fitting_search);
        searchBtn.setOnClickListener(this);
        myFavBtn = (Button) findViewById(R.id.btn_my_fav);
        myFavBtn.setOnClickListener(this);
        shopCarBtn = (Button) findViewById(R.id.btn_shop_car);
        shopCarBtn.setOnClickListener(this);

        myFavUnderLine = findViewById(R.id.view_underline_my_fav);
        shopCarUnderLine = findViewById(R.id.view_underline_shop_car);

//        backupOps = (FrameLayout) findViewById(R.id.fl_backup_ops);
        mFragmentManager = getSupportFragmentManager();

        //设置默认的显示页
        setCurrentFlag(2, null);

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
                    intent.setClass(this, SearchMaterialActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ACCESS_APPLY);
                }
                break;
            case R.id.machine_borrow:
                if (fittingSearchPopup != null) {
                    fittingSearchPopup.dismiss();
                    intent.setClass(this, ApplianceTypeApplyActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MACHINE_APPLY);
                }
                break;
            case R.id.one_key_apply:
                if (fittingSearchPopup != null) {
                    fittingSearchPopup.dismiss();

                    intent.putExtra("product_name", mProductName);
                    intent.putExtra("product_code", mProductCode);

                    intent.setClass(this, OneKeyApplyActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ONE_KEY_APPLY);
                }
                break;
            default:
                break;

        }
    }

    private void setUnderLine() {

    }

    private void setCurrentFlag(int flag, View v) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        setUnderLine();
        switch (flag) {
            case 0:
                LayoutInflater inflater = LayoutInflater.from(this);
                // 引入窗口配置文件
                View view = inflater.inflate(R.layout.popup_search_apply, null);
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

                if(TextUtils.isEmpty(mProductCode)) {
                    view.findViewById(R.id.one_key_apply).setVisibility(View.GONE);
                    view.findViewById(R.id.one_key_apply_underline).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.one_key_apply).setOnClickListener(this);
                }

                view.findViewById(R.id.fitting_borrow).setOnClickListener(this);
                view.findViewById(R.id.machine_borrow).setOnClickListener(this);
                break;
            case 1:
                if (mMyFavMaterialFragment == null) {
                    mMyFavMaterialFragment = new MyFavMaterialFragment();
                    mMyFavMaterialFragment.setData(mMaterials);
                }
//                transaction.replace(R.id.fl_backup_ops, mMyFavMaterialFragment);
                switchFragment(mMyFavMaterialFragment);
                //设置下划线颜色
                myFavUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
                shopCarUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case 2:
                if (mShopCarmaterialFragment == null) {
                    mShopCarmaterialFragment = new ShopCarMaterialFragment();
                    mShopCarmaterialFragment.setData(mMaterials);
                }
//                transaction.replace(R.id.fl_backup_ops, mShopCarmaterialFragment);
                switchFragment(mShopCarmaterialFragment);

                if(mShopCarmaterialFragment.mAdapter != null) {
                    mShopCarmaterialFragment.mAdapter.notifyDataSetChanged();
                }
                //设置下划线颜色
                myFavUnderLine.setBackgroundColor(getResources().getColor(R.color.white));
                shopCarUnderLine.setBackgroundColor(getResources().getColor(R.color.red));
                break;
        }
        transaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(currentFragment != null && !EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().register(currentFragment);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(currentFragment != null && EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().unregister(currentFragment);
        }
    }


    private void switchFragment(Fragment to) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!to.isAdded()) {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.add(R.id.fl_backup_ops, to).commit();
            } else {
                transaction.hide(currentFragment).add(R.id.fl_backup_ops, to).commit();
                EventBus.getDefault().unregister(currentFragment);
            }
        } else {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.show(to).commit();
            } else {
                transaction.hide(currentFragment).show(to).commit();
                EventBus.getDefault().unregister(currentFragment);
            }
        }
        currentFragment = to;

        if(!EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().register(currentFragment);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACCESS_APPLY || requestCode == REQUEST_CODE_MACHINE_APPLY || requestCode == REQUEST_CODE_ONE_KEY_APPLY) {
            if (resultCode == Activity.RESULT_OK) {

                if(mMaterials.size() >= 10) {
                    T.showShort(this, "购物车数量不能超过10");
                    return;
                }

                String materCode = data.getStringExtra("material_code");
                String materDesc = data.getStringExtra("material_name");
                String materPrice = data.getStringExtra("material_price");

                if(!isMaterialExist(materCode)) {
                    ShopCarBackupData shopData = new ShopCarBackupData();
                    shopData.setCmmdtyCode(materCode);
                    shopData.setCmmdtyName(materDesc);
                    shopData.setCmmdtyPrice(materPrice);
                    shopData.setCmmdtyNum("1");
                    mMaterials.add(shopData);
                    mShopCarmaterialFragment.mAdapter.notifyDataSetChanged();
                } else {
                    T.showShort(this, "配件已经申请过了！");
                }
            }
        }

    }

    /**
     * 检查配件是否已经申请过了
     * @param code 配件编码
     * @return true-已存在  false-不存在
     */
    private boolean isMaterialExist(String code) {

        if(mMaterials != null && !TextUtils.isEmpty(code)) {
            for(ShopCarBackupData material : mMaterials) {
                if (code.equals(material.getCmmdtyCode())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
