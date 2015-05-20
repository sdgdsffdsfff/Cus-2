package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.Material;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.MaterialDelFavEvent;
import com.suning.cus.json.JsonDeleteFavorite;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class MaterialDelFavProcessor extends BaseProcessor {

    private Material material;

    public MaterialDelFavProcessor(Context context, RequestParams params, Material material) {
        super(context, params);
        this.material = material;
}

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_DELETE_FAVORITE, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        super.onSuccess(responseInfo);
        DebugLog.d(responseInfo.result);
        Gson gson = new Gson();

        JsonDeleteFavorite jsonDeleteFavorite = null;
        try {
            jsonDeleteFavorite = gson.fromJson(responseInfo.result, JsonDeleteFavorite.class);
            if (jsonDeleteFavorite != null) {
                String result = jsonDeleteFavorite.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    DebugLog.d("获取成功！");
                    MaterialDelFavEvent event = new MaterialDelFavEvent(material);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonDeleteFavorite.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
