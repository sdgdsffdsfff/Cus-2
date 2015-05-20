package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.R;
import com.suning.cus.constants.SettingsConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.UpdatePwdEvent;
import com.suning.cus.logical.UpdatePwdProcessor;
import com.suning.cus.utils.MD5;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import de.greenrobot.event.EventBus;


public class ModifyPasswordActivity extends BaseActivity {

    public static final String MODIFY_PWD_TYPE = "type";

    public static final String MODIFY_INIT_PWD = "modifyInitPwd";

    public static final String MODIFY_PWD = "modifyPwd";

    /**
     * 业务相关
     */
    private String modifyPwdType;

    private String oldPwdStr;

    private String newPwdStr;

    private String confirmPwdStr;

    private long exitTime = 0;

    /**
     * UI相关
     */
    private EditText oldPwd;

    private EditText newPwd;

    private EditText confirmPwd;

    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modifyPwdType = getIntent().getStringExtra(MODIFY_PWD_TYPE);
        setCustomContentView(R.layout.activity_modify_psw);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (modifyPwdType.equals(MODIFY_INIT_PWD)) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    T.showShort(ModifyPasswordActivity.this, "再按一次退出");
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initView() {
        oldPwd = (EditText) findViewById(R.id.ed_old_pwd);
        newPwd = (EditText) findViewById(R.id.ed_new_pwd);
        confirmPwd = (EditText) findViewById(R.id.ed_confirm_pwd);
        cancelBtn = (Button) findViewById(R.id.bt_cancel);
        if (modifyPwdType.equals(MODIFY_INIT_PWD)) {
            cancelBtn.setVisibility(View.GONE);
            setBackVisiable(View.INVISIBLE);
            setTitle(getString(R.string.title_modify_init_pwd));
        } else {
            setTitle(getString(R.string.title_modify_pwd));
        }
    }

    /**
     * 更改密码
     *
     * @param oldPwdStr
     * @param newPwdStr
     */
    private void requestUpdatePwd(String oldPwdStr, String newPwdStr) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(SettingsConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(ModifyPasswordActivity.this));
        params.addBodyParameter(SettingsConstants.OLD_PWD, MD5.getMD5Str(oldPwdStr));
        params.addBodyParameter(SettingsConstants.NEW_PWD, MD5.getMD5Str(newPwdStr));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(ModifyPasswordActivity.this));
//        params.addBodyParameter(UserConstants.IMEI, "294892373548458");

        UpdatePwdProcessor mProcessor = new UpdatePwdProcessor(ModifyPasswordActivity.this, params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_modify_pwd));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收更改密码的Event
     *
     * @param event
     */
    public void onEvent(UpdatePwdEvent event) {
        T.showShort(ModifyPasswordActivity.this, getString(R.string.modify_pwd_success));
        if (modifyPwdType.equals(MODIFY_INIT_PWD)) {
            Intent intent = new Intent();
            intent.setClass(ModifyPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 接收请求失败的Event
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(ModifyPasswordActivity.this, event.message);
    }

    /**
     * 点击确定按钮的响应方法
     *
     * @param v
     */
    public void sureToChangePwd(View v) {
        checkPwd();
    }

    private void checkPwd() {
        oldPwd.setError(null);
        newPwd.setError(null);
        confirmPwd.setError(null);

        oldPwdStr = oldPwd.getText().toString();
        newPwdStr = newPwd.getText().toString();
        confirmPwdStr = confirmPwd.getText().toString();

        View focusView = null;

        if (TextUtils.isEmpty(oldPwdStr)) {
            oldPwd.setError(getString(R.string.input_old_pwd_alert));
            focusView = oldPwd;
            focusView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPwdStr)) {
            newPwd.setError(getString(R.string.input_new_pwd_alert));
            focusView = newPwd;
            focusView.requestFocus();
            return;
        }

        if (newPwdStr.length() < 6) {
            newPwd.setError(getString(R.string.pwd_less_than_six));
            focusView = newPwd;
            focusView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPwdStr)) {
            confirmPwd.setError(getString(R.string.input_new_pwd_alert));
            focusView = confirmPwd;
            focusView.requestFocus();
            return;
        }

        if (!newPwdStr.equals(confirmPwdStr)) {
            confirmPwd.setError(getString(R.string.pwd_not_match));
            focusView = confirmPwd;
            focusView.requestFocus();
            return;
        }

        requestUpdatePwd(oldPwdStr, newPwdStr);
    }

    /**
     * 点击取消按钮的响应方法
     *
     * @param v
     */
    public void cancelChangePwd(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPasswordActivity.this);
        builder.setMessage(getString(R.string.dialog_sure_to_back));
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }
}
