package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.DeleteMessageEvent;
import com.suning.cus.json.JsonMessageDelete;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/15.
 */
public class DeleteMessageProcessor extends BaseProcessor {

    public DeleteMessageProcessor(Context mContext) {
        super(mContext);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_MESSAGE_DELETE, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);
        DebugLog.d(response.result);
        Gson gson = new Gson();

        JsonMessageDelete jsonMessageDelete = null;

        try {
            jsonMessageDelete = gson.fromJson(response.result, JsonMessageDelete.class);

            if (jsonMessageDelete != null) {
                String result = jsonMessageDelete.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    DeleteMessageEvent event = new DeleteMessageEvent();
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonMessageDelete.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
        //    progressDialog.dismiss();
    }
}
