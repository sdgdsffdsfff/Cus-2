package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.activity.fragment.BackUpFragment;
import com.suning.cus.activity.fragment.ManageFragment;
import com.suning.cus.activity.fragment.OnFragmentInteractionListener;
import com.suning.cus.activity.fragment.SettingsFragment;
import com.suning.cus.activity.fragment.TaskSummaryFragment;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.WarningMaterEvent;
import com.suning.cus.logical.WarningMaterProcessor;
import com.suning.cus.utils.SPUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 主界面
 * Created by 14110105 on 2015/3/9.
 */
public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {

    private TaskSummaryFragment mTaskFrag;
    private BackUpFragment mBackUpFrag;
    private ManageFragment mManageFrag;
    private SettingsFragment mSettingsFrag;

    /**
     * 15010551
     */
    private Fragment currentFragment = null; //15010551
    private long exitTime = 0;  //15010551


    private FragmentManager mFragmentManager;

    public static int currentIndex = -1;

    @ViewInject(R.id.ll_tab_bottom_task)
    private LinearLayout mTaskLayout;

    @ViewInject(R.id.ll_tab_bottom_backup)
    private LinearLayout mBackUpLayout;

    @ViewInject(R.id.ll_tab_bottom_manager)
    private LinearLayout mManageLayout;

    @ViewInject(R.id.ll_tab_bottom_settings)
    private LinearLayout mSettingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setCustomContentView(R.layout.activity_main);
        setBackVisiable(View.GONE);

        ViewUtils.inject(this);

        mFragmentManager = getSupportFragmentManager();

        setTitle(getString(R.string.title_task_summary));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        requestWarningMater();

        // 15010551 程序启动默认显示第一页
        setCurrentFrag(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mTaskFrag != null && !EventBus.getDefault().isRegistered(mTaskFrag)) {
            EventBus.getDefault().register(mTaskFrag);
        }
        if (mBackUpFrag != null && !EventBus.getDefault().isRegistered(mBackUpFrag)) {
            EventBus.getDefault().register(mBackUpFrag);
        }
        if (mManageFrag != null && !EventBus.getDefault().isRegistered(mManageFrag)) {
            EventBus.getDefault().register(mManageFrag);
        }
        if (mSettingsFrag != null && !EventBus.getDefault().isRegistered(mSettingsFrag)) {
            EventBus.getDefault().register(mSettingsFrag);
        }

//        if(currentFragment != null && !EventBus.getDefault().isRegistered(currentFragment)) {
//            EventBus.getDefault().register(currentFragment);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

/*        if(currentFragment != null && EventBus.getDefault().isRegistered(currentFragment)) {
            EventBus.getDefault().unregister(currentFragment);
        }*/

        if (mTaskFrag != null) {
            EventBus.getDefault().unregister(mTaskFrag);
        }
        if (mBackUpFrag != null) {
            EventBus.getDefault().unregister(mBackUpFrag);
        }
        if (mManageFrag != null) {
            EventBus.getDefault().unregister(mManageFrag);
        }
        if (mSettingsFrag != null) {
            EventBus.getDefault().unregister(mSettingsFrag);
        }

        EventBus.getDefault().unregister(this);
    }

    private void requestWarningMater() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(MainActivity.this));

        WarningMaterProcessor processor = new WarningMaterProcessor(MainActivity.this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage("获取库龄预警信息...");
        processor.sendPostRequest();
    }

    public void onEvent(WarningMaterEvent event) {
        String data = event.data;
        if (!TextUtils.isEmpty(data)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(data);
            builder.setPositiveButton("知道了", null);
            builder.show();
        }
    }

    public void onEvent(RequestFailEvent event) {
        T.showShort(MainActivity.this, event.message);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //15010551 初始数据库建表
        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        try {
            dbUtils.createTableIfNotExist(ShopCarBackupData.class);
        } catch (DbException e) {
            T.showShort(MainActivity.this, getString(R.string.create_db_error));
            e.printStackTrace();
        }
    }


    private void resetBtn() {
        ((ImageButton)mTaskLayout.findViewById(R.id.btn_tab_bottom_task)).setImageResource(R.drawable.ic_tab_task_nor);
        ((ImageButton)mBackUpLayout.findViewById(R.id.btn_tab_bottom_backup)).setImageResource(R.drawable.ic_tab_backup_nor);
        ((ImageButton)mManageLayout.findViewById(R.id.btn_tab_bottom_manager)).setImageResource(R.drawable.ic_tab_manage_nor);
        ((ImageButton)mSettingsLayout.findViewById(R.id.btn_tab_bottom_settings)).setImageResource(R.drawable.ic_tab_account_nor);

        ((TextView)mTaskLayout.findViewById(R.id.tv_tab_bottom_task)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView)mBackUpLayout.findViewById(R.id.tv_tab_bottom_backup)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView)mManageLayout.findViewById(R.id.tv_tab_bottom_manager)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView)mSettingsLayout.findViewById(R.id.tv_tab_bottom_settings)).setTextColor(getResources().getColor(R.color.gray));

    }

    private void setCurrentFrag(int index) {

        /*如果是相同的index就不切换*/
        if (currentIndex == index) {
            return;
        }

        currentIndex = index;

        resetBtn();

        switch (index) {
            case 0:
                if(mTaskFrag == null) {
                    mTaskFrag = new TaskSummaryFragment();
                }
                switchFragment(mTaskFrag);
                ((ImageButton)mTaskLayout.findViewById(R.id.btn_tab_bottom_task)).setImageResource(R.drawable.ic_tab_task_press);
                ((TextView)mTaskLayout.findViewById(R.id.tv_tab_bottom_task)).setTextColor(getResources().getColor(R.color.cl_text_press));
                setTitle(getString(R.string.title_task_summary));
                break;

            case 1:
                if(mBackUpFrag == null) {
                    mBackUpFrag = new BackUpFragment();
                }
                switchFragment(mBackUpFrag);

                ((ImageButton)mBackUpLayout.findViewById(R.id.btn_tab_bottom_backup)).setImageResource(R.drawable.ic_tab_backup_press);
                ((TextView)mBackUpLayout.findViewById(R.id.tv_tab_bottom_backup)).setTextColor(getResources().getColor(R.color.cl_text_press));
                setTitle(getString(R.string.title_backup));
                break;

            case 2:
                if(mManageFrag == null) {
                    mManageFrag = new ManageFragment();
                }
                switchFragment(mManageFrag);

                ((ImageButton)mManageLayout.findViewById(R.id.btn_tab_bottom_manager)).setImageResource(R.drawable.ic_tab_manage_press);
                ((TextView)mManageLayout.findViewById(R.id.tv_tab_bottom_manager)).setTextColor(getResources().getColor(R.color.cl_text_press));
                setTitle(getString(R.string.title_manage));
                break;

            case 3:
                if(mSettingsFrag == null) {
                    mSettingsFrag = new SettingsFragment();
                }
                switchFragment(mSettingsFrag);

                ((ImageButton)mSettingsLayout.findViewById(R.id.btn_tab_bottom_settings)).setImageResource(R.drawable.ic_tab_account_press);
                ((TextView)mSettingsLayout.findViewById(R.id.tv_tab_bottom_settings)).setTextColor(getResources().getColor(R.color.cl_text_press));
                setTitle(getString(R.string.title_settings));
                break;

            default:
                break;
        }


    }

    private void switchFragment(Fragment to) {

        /*如果是相同的Fragment就不切换*/
        if(currentFragment == to) {
            return;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!to.isAdded()) {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.add(R.id.id_content, to).commit();
            } else {
                transaction.hide(currentFragment).add(R.id.id_content, to).commit();
                DebugLog.d("unregister" + currentFragment.getClass().getName());
        //        EventBus.getDefault().unregister(currentFragment);
            }
        } else {
            if (currentFragment == null) {  //进程序后的第一次加载
                transaction.show(to).commit();
            } else {
                transaction.hide(currentFragment).show(to).commit();
                DebugLog.d("unregister" + currentFragment.getClass().getName());
        //        EventBus.getDefault().unregister(currentFragment);
            }
        }
        currentFragment = to;
/*
        if(!EventBus.getDefault().isRegistered(currentFragment)) {
            DebugLog.d("register" + currentFragment.getClass().getName());
            EventBus.getDefault().register(currentFragment);
        }*/

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_tab_bottom_task:
                setCurrentFrag(0);
                break;
            case R.id.ll_tab_bottom_backup:
                setCurrentFrag(1);
                break;
            case R.id.ll_tab_bottom_manager:
                setCurrentFrag(2);
                break;
            case R.id.ll_tab_bottom_settings:
                setCurrentFrag(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                T.showShort(MainActivity.this, "再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                createShortcut();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建桌面快捷图标
     */
    public void createShortcut() {

        boolean isFirstStart = (boolean) SPUtils.get(this, UserConstants.IS_FIRST_START, true);

        if(isFirstStart) {
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            // 不允许重复创建
            shortcut.putExtra("duplicate", false);
            // 指定快捷方式的启动对象
            ComponentName comp = new ComponentName(this, InitialActivity.class);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
            // 快捷方式的图标
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            // 发出广播
            sendBroadcast(shortcut);
            // 将第一次启动的标识设置为false
            SPUtils.put(this, UserConstants.IS_FIRST_START, false);
        }
    }

}
