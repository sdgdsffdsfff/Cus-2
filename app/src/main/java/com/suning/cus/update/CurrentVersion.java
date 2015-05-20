package com.suning.cus.update;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.suning.cus.R;


/**
 * 获取当前版本信息
 */
public class CurrentVersion {

//    public static final String appPackName = "com.suning.logistics";
    /**
     * 应用包名
     */
    public static String appPackName = "";

    public static int getVerCode(Context context) throws NameNotFoundException {
        int verCode = -1;
        try {
            verCode = context.getPackageManager()
                    .getPackageInfo(appPackName, 0).versionCode;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return verCode;
    }

    public static String getVerName(Context context)
            throws NameNotFoundException {
        String verName = "";
        try {
            verName = context.getPackageManager()
                    .getPackageInfo(appPackName, 0).versionName;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return verName;
    }

    public static String getAppName(Context context) {
        return context.getResources().getText(R.string.app_name).toString();
    }
}
