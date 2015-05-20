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
import com.suning.cus.event.GetWDetailEvent;
import com.suning.cus.json.JsonWDetail;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class GetWDetailProcessor extends BaseProcessor {

    public GetWDetailProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_GET_W_INFO, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        Gson gson = new Gson();
        DebugLog.d(responseInfo.result);

        JsonWDetail jsonWDetail = null;

        try {
            jsonWDetail = gson.fromJson(responseInfo.result, JsonWDetail.class);

            if (jsonWDetail != null) {
                String result = jsonWDetail.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    DebugLog.d("获取成功！");
                    GetWDetailEvent event = new GetWDetailEvent(jsonWDetail);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonWDetail.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
