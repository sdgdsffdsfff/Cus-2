package com.suning.cus;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import android.util.Log;

import com.lidroid.xutils.http.RequestParams;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.LoginEvent;
import com.suning.cus.json.JsonUser;
import com.suning.cus.logical.LoginProcessor;
import com.suning.cus.utils.MD5;
import com.suning.cus.utils.PhoneInfo;
import com.suning.cus.utils.SpCoookieUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by 14110105 on 2015/4/24.
 */
public class LoginTestCase extends AndroidTestCase {

    public final Object mLock = new Object();
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerEvents();
    }



    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        unRegisterEvents();
    }

    public void registerEvents() {
        DebugLog.d("registerEvents");
        EventBus.getDefault().register(this);
    }

    public void unRegisterEvents() {
        DebugLog.d("unRegisterEvents");
        EventBus.getDefault().unregister(this);
    }




    public void onEvent(LoginEvent event) {
        DebugLog.d(event.toString());

        JsonUser jsonUser = event.jsonUser;

        CusServiceApplication.EMPLOYEE_ID = jsonUser.getEmployeeId();
        CusServiceApplication.IMEI = PhoneInfo.getIMEI(getContext());

        saveSP();
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }



    public void testLogin() {

        Log.d("ServerTest", "testLogin");

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, "W00000053");
        params.addBodyParameter(UserConstants.PASSWORD, MD5.getMD5Str("19900809"));
        params.addBodyParameter(UserConstants.IMEI, "294892373548458");


        /* 通过LoginProcessor进行请求 */
        LoginProcessor processor = new LoginProcessor(getContext(), params);
        //        processor.setDialogEnabled(true);
        processor.sendPostRequest();

        synchronized (mLock) {
            try {
                mLock.wait();
                assertTrue(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 保存SharedPreferences相关的数据
     */

    public void saveSP() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(BaseConstants
                .SP_NAME, Context.MODE_PRIVATE).edit();

        editor.putString(UserConstants.EMPLOYEE_ID, CusServiceApplication.EMPLOYEE_ID);
        editor.putString(UserConstants.IMEI, CusServiceApplication.IMEI);
        SpCoookieUtils.saveCookieStore(getContext(), CusServiceApplication.COOKIE_STORE);

        editor.apply();

    }



}
