package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.QueryOrderData;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.OrderActiveEvent;
import com.suning.cus.json.JsonOrderActive;
import com.suning.cus.utils.XUtilsHelper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class QueryOrderActiveProcessor extends BaseProcessor {

    public QueryOrderActiveProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_W_DETAIL_ORDER, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();
        JsonOrderActive jsonOrderActive = null;

        try {
            jsonOrderActive = gson.fromJson(responseInfo.result, JsonOrderActive.class);

            if (jsonOrderActive != null) {
                String result = jsonOrderActive.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    List<QueryOrderData> data = jsonOrderActive.getData();
                    OrderActiveEvent event = new OrderActiveEvent(data);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonOrderActive.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
