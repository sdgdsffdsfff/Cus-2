package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.json.JsonQueryMaterPrice;
import com.suning.cus.utils.XUtilsHelper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/16.
 */
public class QueryPriceProcessor extends BaseProcessor {

    private QueryPriceEvent.QUERY_PRICE_TYPE type;

    public QueryPriceProcessor(Context context, RequestParams params, QueryPriceEvent.QUERY_PRICE_TYPE type) {
        super(context, params);
        this.type = type;
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_QUERY_MATER_PRICE_NEW, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();
        JsonQueryMaterPrice jsonQueryMaterPrice = null;

        try {
            jsonQueryMaterPrice = gson.fromJson(responseInfo.result, JsonQueryMaterPrice.class);
            if (jsonQueryMaterPrice != null) {
                String result = jsonQueryMaterPrice.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    List<QueryMaterPriceData> priceDataList = jsonQueryMaterPrice.getData();
                    QueryPriceEvent event = new QueryPriceEvent(priceDataList, true, type);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonQueryMaterPrice.getErrorDesc());
                    EventBus.getDefault().post(new QueryPriceEvent("", false, type));
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
