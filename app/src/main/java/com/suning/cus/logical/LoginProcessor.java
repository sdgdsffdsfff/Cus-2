package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.LoginEvent;
import com.suning.cus.json.JsonUser;

import org.apache.http.impl.client.DefaultHttpClient;

import de.greenrobot.event.EventBus;

/**
 * 登录处理类
 * Created by 14110105 on 2015/4/15.
 */
public class LoginProcessor extends BaseProcessor {

//    private  XUtilsHelper helper;
    private  HttpUtils helper;

    public LoginProcessor(Context mContext) {
        super(mContext);
    }

    public LoginProcessor(Context context, RequestParams params) {
        super(context, params);
    }


    @Override
    public void sendPostRequest() {

//        helper = new XUtilsHelper(mContext);
//        helper.sendPost(ServerConfig.URL_LOGIN, params, this);

        // Todo: 暂时先用HttpUtils, 后面要用XUtilsHelper来替代
        helper = new HttpUtils();
        helper.send(HttpRequest.HttpMethod.POST, ServerConfig.URL_LOGIN, params, this);

    }


    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();
        JsonUser jsonUser;

        try {
            jsonUser = gson.fromJson(response.result, JsonUser.class);

            if (jsonUser != null) {
                String result = jsonUser.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {

                    //记录Cookies
                    DefaultHttpClient client = (DefaultHttpClient) helper.getHttpClient();
                    CusServiceApplication.COOKIE_STORE = client.getCookieStore();

                    EventBus.getDefault().post(new LoginEvent(jsonUser));
                } else {
                    postFailure(jsonUser.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
