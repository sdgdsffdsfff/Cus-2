package com.suning.cus.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.suning.cus.R;
import com.suning.cus.update.CompareUpdateApk;
import com.suning.cus.utils.NetUtils;

/**
 * 初始化界面
 */
public class InitialActivity extends Activity {

    public final static int NOT_NEED_UPDATE = 1;

    public static final int UPDATE_SERVICE = 0;

    public static final int UPDATE_NET_ERROR = -1;

    private ProgressDialog mDialog;

    Handler mHandler  = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_initial);

        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage(getString(R.string.check_update));

        // TODO: 正式版本去掉
//        mHandler.sendEmptyMessage(NOT_NEED_UPDATE);

        try {
            if (NetUtils.isConnected(this)) {
                CompareUpdateApk.CompareUpdate(this, mHandler);
            } else {
                mHandler.sendEmptyMessage(UPDATE_NET_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOT_NEED_UPDATE:
                    mDialog.dismiss();
                    // 不需要更新的时候
                    Intent intent = new Intent(InitialActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case UPDATE_SERVICE:
                    Toast.makeText(InitialActivity.this, R.string.check_update,
                            Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_NET_ERROR:
                    Toast.makeText(InitialActivity.this, R.string.toast_error_network,
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

}
