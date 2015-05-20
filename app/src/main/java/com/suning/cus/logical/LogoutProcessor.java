package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.LogoutEvent;
import com.suning.cus.json.JsonLogout;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class LogoutProcessor extends BaseProcessor {

    public LogoutProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_LOGOUT, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();

        JsonLogout jsonLogout = null;

        try {
            jsonLogout = gson.fromJson(responseInfo.result, JsonLogout.class);

            if (jsonLogout != null) {
                String result = jsonLogout.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    LogoutEvent event = new LogoutEvent();
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonLogout.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
