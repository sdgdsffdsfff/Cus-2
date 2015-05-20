package com.suning.cus;

import android.app.Application;

import com.suning.cus.controllers.MainController;

import org.apache.http.client.CookieStore;


/**
 * Application
 * Created by 14110105 on 2015/3/9.
 */
public class CusServiceApplication extends Application {

    public static CusServiceApplication instance;

    private MainController mMainController;

    /**
     * 登录获取到的Cookie，在登录成功后赋值
     */
    public static CookieStore COOKIE_STORE = null;

    public static String IMEI = null;

    public static String EMPLOYEE_ID = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mMainController = new MainController();
    }

    public static CusServiceApplication getInstance() {

        if (instance == null) {
            instance = new CusServiceApplication();
        }
        return instance;
    }


    public MainController getMainController() {
        return mMainController;
    }

}
