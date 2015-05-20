package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.TaskSummaryEvent;
import com.suning.cus.json.JsonTaskSummary;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 获取任务概要处理类
 * Created by 14110105 on 2015/4/13.
 */
public class TaskSummaryProcessor extends BaseProcessor {

    private TaskSummaryEvent event;

    public TaskSummaryProcessor(Context context) {
        super(context);
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_TASKS_SUMMARY, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();
        JsonTaskSummary jsonTaskSummary;

        try {
            jsonTaskSummary = gson.fromJson(response.result, JsonTaskSummary.class);

            if (jsonTaskSummary != null) {
                String result = jsonTaskSummary.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {
                    event = new TaskSummaryEvent(jsonTaskSummary.getTaskList());
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonTaskSummary.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }
}
