package com.suning.cus.logical;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.QualityAssuranceEvent;
import com.suning.cus.json.JsonQualityAssurance;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class QualityAssuranceProcessor extends BaseProcessor {

    public QualityAssuranceProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_QUERY_PROLONG_INSURANCE, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();
        JsonQualityAssurance jsonQualityAssurance = null;
        try {
            jsonQualityAssurance = gson.fromJson(responseInfo.result, JsonQualityAssurance.class);

            if(jsonQualityAssurance.getIsSuccess().equals("S")) {

                QualityAssuranceEvent event = new QualityAssuranceEvent(jsonQualityAssurance);
                EventBus.getDefault().post(event);

            } else {
                postFailure(jsonQualityAssurance.getErrorDesc());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
