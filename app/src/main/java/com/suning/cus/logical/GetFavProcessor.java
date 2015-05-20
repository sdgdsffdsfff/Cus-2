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
import com.suning.cus.event.GetFavEvent;
import com.suning.cus.json.JsonBackupFavorite;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class GetFavProcessor extends BaseProcessor {

    public GetFavProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_FAVORITE, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();

        JsonBackupFavorite jsonBackupFavorite = null;
        try {
            jsonBackupFavorite = gson.fromJson(responseInfo.result, JsonBackupFavorite.class);
            if (jsonBackupFavorite != null) {
                String result = jsonBackupFavorite.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    DebugLog.d("获取成功！");
                    GetFavEvent event = new GetFavEvent(jsonBackupFavorite);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonBackupFavorite.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
