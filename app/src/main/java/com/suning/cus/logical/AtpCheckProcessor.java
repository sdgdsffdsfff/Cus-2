package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.AtpCheckResponseList;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.AtpCheckEvent;
import com.suning.cus.json.JsonAtpCheckRes;
import com.suning.cus.utils.XUtilsHelper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/16.
 */
public class AtpCheckProcessor extends BaseProcessor{

    public AtpCheckProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_ATP_CHECK, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();
        JsonAtpCheckRes jsonAtpCheckRes = null;

        try {
            jsonAtpCheckRes = gson.fromJson(responseInfo.result, JsonAtpCheckRes.class);

            if (jsonAtpCheckRes != null) {
                String result = jsonAtpCheckRes.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    List<AtpCheckResponseList> atpCheckResponseList = jsonAtpCheckRes.getAtpCheckResponseList();
                    AtpCheckEvent event = new AtpCheckEvent(atpCheckResponseList, true);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonAtpCheckRes.getErrorDesc());
                    //返回不成功
                    EventBus.getDefault().post(new AtpCheckEvent(null, false));
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
            EventBus.getDefault().post(new AtpCheckEvent(null, false));
        }
    }

    @Override
    public void onFailure(HttpException error, String msg) {
        super.onFailure(error, msg);
        EventBus.getDefault().post(new AtpCheckEvent(null, false));
    }
}
