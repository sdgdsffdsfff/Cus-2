package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.GetMessageDetailEvent;
import com.suning.cus.json.JsonMessageDetail;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/15.
 */
public class GetMessageDetailProcessor extends BaseProcessor {

    public GetMessageDetailProcessor(Context mContext) {
        super(mContext);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_MESSAGE_DETAIL, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);
        DebugLog.d(response.result);
        Gson gson = new Gson();

        JsonMessageDetail jsonMessageDetail = null;

        try {
            jsonMessageDetail = gson.fromJson(response.result, JsonMessageDetail.class);

            if (jsonMessageDetail != null) {
                String result = jsonMessageDetail.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    GetMessageDetailEvent event = new GetMessageDetailEvent(jsonMessageDetail);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonMessageDetail.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
