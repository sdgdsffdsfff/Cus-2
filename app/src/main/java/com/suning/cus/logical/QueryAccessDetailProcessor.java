package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.QueryOrderDetail;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.QueryAccessDetailEvent;
import com.suning.cus.json.JsonQueryAccessoryOrderDetails;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/17.
 */
public class QueryAccessDetailProcessor extends BaseProcessor {

    public QueryAccessDetailProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_QUERY_ACCESS_ORDER_DETAILS, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        Gson gson = new Gson();
        DebugLog.d(responseInfo.result);

        JsonQueryAccessoryOrderDetails jsonQueryAccessoryOrderDetails = null;

        try {
            jsonQueryAccessoryOrderDetails = gson.fromJson(responseInfo.result, JsonQueryAccessoryOrderDetails.class);

            if (jsonQueryAccessoryOrderDetails != null) {
                String result = jsonQueryAccessoryOrderDetails.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    QueryOrderDetail queryOrderDetail = jsonQueryAccessoryOrderDetails.getOrderDetail();
                    QueryAccessDetailEvent event = new QueryAccessDetailEvent(queryOrderDetail);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonQueryAccessoryOrderDetails.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
