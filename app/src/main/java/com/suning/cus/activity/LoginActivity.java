package com.suning.cus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.LoginEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.json.JsonUser;
import com.suning.cus.logical.LoginProcessor;
import com.suning.cus.utils.MD5;
import com.suning.cus.utils.PhoneInfo;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 登录Activity
 */
public class LoginActivity extends Activity {

    // View 注入.
    @ViewInject(R.id.et_name)
    private EditText mNameView;
    @ViewInject(R.id.et_password)
    private EditText mPasswordView;
    @ViewInject(R.id.bt_login)
    private Button mLoginBtn;
    @ViewInject(R.id.cb_remember_password)
    private CheckBox mRememberPassword;
    @ViewInject(R.id.cb_auto_login)
    private CheckBox mAudioLogin;

    /**
     * String 用户名
     */
    private String mName;
    /**
     * String 用户密码
     */
    private String mPassword;

    /**
     * 是否记住密码
     */
    private boolean isRememberPassword = false;

    private boolean isAutoLogin = false;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 注入View，初始化
        ViewUtils.inject(this);

        sp = getSharedPreferences(BaseConstants.SP_NAME, Context.MODE_PRIVATE);
    }


    @Override
    public void onStart() {
        super.onStart();
        /*注册subscribers*/
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*把自己从subscribers中移除，不再接收*/
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isRememberPassword = sp.getBoolean(UserConstants.IS_REMEMBER_PASSWORD, false);

        if (isRememberPassword) {
            mRememberPassword.setChecked(true);
            mName = sp.getString(UserConstants.EMPLOYEE_ID, "");
            mPassword = sp.getString(UserConstants.PASSWORD, "");
            mNameView.setText(mName);
            mNameView.setSelection(mNameView.length());
            mPasswordView.setText(mPassword);
        } else {
            mRememberPassword.setChecked(false);
        }

        isAutoLogin = sp.getBoolean(UserConstants.IS_AUTO_LOGIN, false);

        if (isAutoLogin) {
            mAudioLogin.setChecked(true);
        } else {
            mAudioLogin.setChecked(false);
        }
    }

    /**
     * 保存SharedPreferences相关的数据
     */
    public void saveSP() {

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(UserConstants.IS_REMEMBER_PASSWORD, isRememberPassword);
        editor.putBoolean(UserConstants.IS_AUTO_LOGIN, isAutoLogin);

        if (isRememberPassword) {
            editor.putString(UserConstants.EMPLOYEE_ID, mName);
            editor.putString(UserConstants.PASSWORD, mPassword);
        }

        editor.putString(UserConstants.EMPLOYEE_ID, CusServiceApplication.EMPLOYEE_ID);
        editor.putString(UserConstants.IMEI, CusServiceApplication.IMEI);
        SpCoookieUtils.saveCookieStore(LoginActivity.this, CusServiceApplication.COOKIE_STORE);

        editor.commit();

    }

    /**
     * CheckBox的响应事件
     *
     * @param v 点击的CheckBox
     */
    public void onCheck(View v) {

        switch (v.getId()) {
            case R.id.cb_remember_password:
                isRememberPassword = mRememberPassword.isChecked();
                break;
            case R.id.cb_auto_login:
                isAutoLogin = mAudioLogin.isChecked();
                break;
            default:
                break;
        }

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View v) {

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        mName = mNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mName)) {
            mNameView.setError(getString(R.string.error_field_required_name));
            focusView = mNameView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(mName) && TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid mPassword, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login(mName, mPassword);
        }
    }


    /**
     * 校验密码长度
     *
     * @param password 密码
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }


    /**
     * 请求服务器登录
     *
     * @param name     用户名
     * @param password 密码
     */
    public void login(String name, String password) {

        RequestParams params = new RequestParams();

        // TODO: 正式环境使用
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, name);
        params.addBodyParameter(UserConstants.PASSWORD, MD5.getMD5Str(password));
        params.addBodyParameter(UserConstants.IMEI, PhoneInfo.getIMEI(this));

        // sit 固定账号
        //        params.addBodyParameter(UserConstants.EMPLOYEE_ID, "W00000053");
        //        params.addBodyParameter(UserConstants.PASSWORD, MD5.getMD5Str("19900809"));
        //        params.addBodyParameter(UserConstants.IMEI, "294892373548458");

        // pre 固定账号
        //        params.addBodyParameter(UserConstants.EMPLOYEE_ID, "W03040438");
        //        params.addBodyParameter(UserConstants.PASSWORD, MD5.getMD5Str("236959"));
        //        params.addBodyParameter(UserConstants.IMEI, "864601020618815");

        /* 通过LoginProcessor进行请求 */
        LoginProcessor processor = new LoginProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.sendPostRequest();

    }

    /**
     * 登录成功后，LoginEvent的回调函数
     *
     * @param event 登录成功Event
     */
    public void onEvent(LoginEvent event) {

        JsonUser jsonUser = event.jsonUser;

        CusServiceApplication.EMPLOYEE_ID = jsonUser.getEmployeeId();
        CusServiceApplication.IMEI = PhoneInfo.getIMEI(this);

        // 记录SharedPreferences状态
        saveSP();

        //15010551 刷新cookie
        XUtilsHelper http = new XUtilsHelper(LoginActivity.this);
        http.configCookieStore(SpCoookieUtils.getCookieStore(LoginActivity.this));

        Intent intent = new Intent();
        //15010551 如果用户第一次登录强制修改密码
        if ("Y".equals(jsonUser.getFirstLogin())) {
            intent.setClassName(LoginActivity.this, ModifyPasswordActivity.class.getName());
            intent.putExtra(ModifyPasswordActivity.MODIFY_PWD_TYPE,
                    ModifyPasswordActivity.MODIFY_INIT_PWD);
        } else {
            intent.setClassName(LoginActivity.this, MainActivity.class.getName());
        }
        startActivity(intent);
        finish();
    }

    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {

        String message = event.message;

        if (!TextUtils.isEmpty(message)) {
            if (message.contains("密码") || message.contains("网络")) {
                T.showShort(this, message);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(message + "\n"
                        + "请检查账号或IMEI码是否正确，如果有误请联系分公司服务经理。本机IMEI码："
                        + PhoneInfo.getIMEI(this));
                builder.setPositiveButton(R.string.confirm, null);
                builder.show();
            }
        }
    }
}



