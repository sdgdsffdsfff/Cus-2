package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.BespokeTimeEvent;
import com.suning.cus.json.JsonBase;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 预约排程处理类
 * Created by 14110105 on 2015/4/16.
 */
public class BespokeTimeProcessor extends BaseProcessor {


    public BespokeTimeProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_BESPOKE_TIME, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);
        Gson gson = new Gson();

        JsonBase json;

        try {
            json = gson.fromJson(response.result, TaskDetail.class);

            if (json != null) {
                String result = json.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    EventBus.getDefault().post(new BespokeTimeEvent());
                } else {
                    postFailure(json.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }

    }
}
