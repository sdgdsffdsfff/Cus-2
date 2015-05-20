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
import com.suning.cus.event.AccessoryReturnEvent;
import com.suning.cus.json.JsonAccessoryReturn;
import com.suning.cus.utils.T;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/16.
 */
public class AccessoryReturnProcessor extends BaseProcessor {

    public AccessoryReturnProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_ACCESS_RETURN, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        Gson gson = new Gson();
        DebugLog.d(responseInfo.result);

        JsonAccessoryReturn jsonAccessoryReturn = null;
        try {
            jsonAccessoryReturn = gson.fromJson(responseInfo.result, JsonAccessoryReturn.class);
            if (jsonAccessoryReturn != null) {
                String result = jsonAccessoryReturn.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    DebugLog.d("获取成功！");
                    T.showShort(mContext, jsonAccessoryReturn.getSuccessDesc());
                    AccessoryReturnEvent event = new AccessoryReturnEvent();
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonAccessoryReturn.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
