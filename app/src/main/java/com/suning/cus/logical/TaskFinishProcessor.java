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
import com.suning.cus.event.TaskFinishEvent;
import com.suning.cus.json.JsonBase;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 登录处理类
 * Created by 14110105 on 2015/4/15.
 */
public class TaskFinishProcessor extends BaseProcessor {

    public TaskFinishProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {

        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_DESTROY_BILL, params, this);
    }


    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();

        try {
            JsonBase json = gson.fromJson(response.result, JsonBase.class);

            if (json != null) {
                String result = json.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    EventBus.getDefault().post(new TaskFinishEvent());
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
