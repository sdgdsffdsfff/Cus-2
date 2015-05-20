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
import com.suning.cus.event.SearchMaterialCategoryEvent;
import com.suning.cus.json.JsonMaterialCategory;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 查询商品类目处理类
 * Created by 14110105 on 2015/4/17.
 */
public class SearchMaterialCategoryProcessor extends BaseProcessor {

    public SearchMaterialCategoryProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_QUERY_MATER_CATEGORY, params, this);
    }


    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();

        try {
            JsonMaterialCategory jsonMaterialCategory = gson.fromJson(response.result,
                    JsonMaterialCategory.class);

            if (jsonMaterialCategory != null) {
                String result = jsonMaterialCategory.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    EventBus.getDefault().post(
                            new SearchMaterialCategoryEvent(jsonMaterialCategory.getMaterialList(),
                                    jsonMaterialCategory.getTotalPageNum()));
                } else {
                    postFailure(jsonMaterialCategory.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
