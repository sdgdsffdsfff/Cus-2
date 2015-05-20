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
import com.suning.cus.event.AddFavEvent;
import com.suning.cus.json.JsonBase;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 添加收藏Processor
 * Created by 14110105 on 2015/4/18.
 */
public class AddFavProcessor extends BaseProcessor {

    private Material material;

    public AddFavProcessor(Context context, RequestParams params, Material material) {
        super(context, params);
        this.material = material;
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper http = new XUtilsHelper(mContext);
        http.sendPost(ServerConfig.URL_ADD_FAVORITE, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);
        DebugLog.d(response.result);

        try {
            Gson gson = new Gson();
            JsonBase json = gson.fromJson(response.result, JsonBase.class);
            if (json != null) {
                String result = json.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    AddFavEvent event = new AddFavEvent(material);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(json.getErrorDesc());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
