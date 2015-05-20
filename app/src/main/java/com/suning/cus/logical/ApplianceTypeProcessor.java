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
import com.suning.cus.event.ApplianceTypeEvent;
import com.suning.cus.json.JsonAppliance;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 14110105 on 2015/4/17.
 */
public class ApplianceTypeProcessor extends BaseProcessor {

    public ApplianceTypeProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_QUERY_MODEL, params, this);
    }


    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();

        try {
            JsonAppliance jsonAppliance = gson.fromJson(response.result, JsonAppliance.class);

            if (jsonAppliance != null) {
                String result = jsonAppliance.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    EventBus.getDefault().post(new ApplianceTypeEvent(jsonAppliance.getData()));
                } else {
                    postFailure(jsonAppliance.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
