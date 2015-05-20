package com.suning.cus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.UserConstants;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.List;


/**
 * Created by 15010551 on 2015/4/11.
 */
public class SpCoookieUtils {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static Gson gson;

    public static String getEmployeeIdSp(Context mContext) {
        sp = mContext.getSharedPreferences(BaseConstants.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(UserConstants.EMPLOYEE_ID, "");
    }

    public static String getEmployeeId(Context mContext) {
        if (CusServiceApplication.EMPLOYEE_ID == null) {
            CusServiceApplication.EMPLOYEE_ID = getEmployeeIdSp(mContext);
        }
        return CusServiceApplication.EMPLOYEE_ID;
    }

    public static String getImeiSp(Context mContext) {
        sp = mContext.getSharedPreferences(BaseConstants.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(UserConstants.IMEI, "");
    }

    public static String getImei(Context mContext) {
        if (CusServiceApplication.IMEI == null) {
            CusServiceApplication.IMEI = getImeiSp(mContext);
        }
        return CusServiceApplication.IMEI;
    }

    public static void saveCookieStore(Context mContext, CookieStore cookieStore) {
        sp = mContext.getSharedPreferences(BaseConstants.SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        gson = new Gson();

        List<Cookie> cookieList = cookieStore.getCookies();

        String json = gson.toJson(cookieList);

        editor.remove(UserConstants.COOKIE_STORE);
        editor.putString(UserConstants.COOKIE_STORE, json);
        editor.commit();
    }

    public static BasicCookieStore getCookieStoreSp(Context mContext) {
        sp = mContext.getSharedPreferences(BaseConstants.SP_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        String cookieStr = sp.getString(UserConstants.COOKIE_STORE, "");
        List<Cookie> list = gson.fromJson(cookieStr, new TypeToken<List<BasicClientCookie>>(){}.getType());
        Cookie[] cookies = new Cookie[]{};
        cookies = list.toArray(cookies);
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        basicCookieStore.addCookies(cookies);
        return basicCookieStore;
    }

    public static CookieStore getCookieStore(Context mContext) {

        // 默认从全局变量读取，为空则从SP读取
        CookieStore mCookieStore = CusServiceApplication.COOKIE_STORE;
        if (mCookieStore == null) {
            mCookieStore = getCookieStoreSp(mContext);
            CusServiceApplication.COOKIE_STORE = mCookieStore;
        }
        return mCookieStore;

        // 强制从SharePreference读取，用于测试
        // return getCookieStoreSp(mContext);
    }

}
