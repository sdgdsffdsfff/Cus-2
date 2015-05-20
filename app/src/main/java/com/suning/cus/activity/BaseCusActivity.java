package com.suning.cus.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.controllers.MainController;
import com.suning.cus.controllers.TaskController;

/**
 * 15010551
 * 需继承BaseActivity，并调用setCustomContentView才能显示自定义ActionBar
 */
public abstract   class BaseCusActivity extends ActionBarActivity implements MainController
        .HostCallbacks, TaskController.TaskDetailUi{

    private MainController mMainController;

    /**
     * UI相关
     */
    // ActionBar的title
    TextView titleTextView;

    // ActionBar左侧的返回按钮
    ImageView backIconImageView;

    // ActionBar右侧的操作按钮
    Button opsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*start*/
        setContentView(getContentViewLayoutId());
        mMainController = CusServiceApplication.instance.getMainController();

        /*end*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainController.setHostCallbacks(this);
        mMainController.init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainController.suspend();
    }

    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    protected final MainController getMainController() {
        return mMainController;
    }


    /**
     * 设置title
     */
    public void setCustomTitle(String title) {
        titleTextView.setText(title);
    }

    /**
     * 设置title
     */
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    /**
     * 设置返回按钮是否可见
     */
    protected void setBackVisiable(int visibility) {
        backIconImageView.setVisibility(visibility);
    }

    /**
     * 设置操作按钮是否可见
     */
    protected void setOpsButtonVisiable(int visibility) {
        opsButton.setVisibility(visibility);
    }

    /**
     * 设置操作按钮的文本
     */
    protected void setOpsButtonText(String text) {
        opsButton.setText(text);
    }

    /**
     * 设置操作按钮的监听
     */
    protected void setOpsButtonClickListener(View.OnClickListener l) {
        opsButton.setOnClickListener(l);
    }

    /**
     * 设置contentView
     */
    protected void setCustomContentView(int layoutResID) {
        LinearLayout llContent = (LinearLayout) findViewById(R.id.ll_activity_base);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutResID, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llContent.addView(v, layoutParams);
    }



    @Override
    public RequestParams getRequestParameter() {
        return null;
    }


    @Override
    public boolean isModal() {
        return false;
    }
}
