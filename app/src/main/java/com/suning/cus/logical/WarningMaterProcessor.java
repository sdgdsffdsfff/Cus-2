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
import com.suning.cus.event.WarningMaterEvent;
import com.suning.cus.json.JsonWarningMater;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class WarningMaterProcessor extends BaseProcessor {

    public WarningMaterProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_WARNING_MATER, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);

        DebugLog.d(responseInfo.result);

        Gson gson = new Gson();

        JsonWarningMater jsonWarningMater;

        try {
            jsonWarningMater = gson.fromJson(responseInfo.result, JsonWarningMater.class);

            if (jsonWarningMater != null) {
                String result = jsonWarningMater.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    String data = jsonWarningMater.getData();
                    WarningMaterEvent event = new WarningMaterEvent(data);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonWarningMater.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
