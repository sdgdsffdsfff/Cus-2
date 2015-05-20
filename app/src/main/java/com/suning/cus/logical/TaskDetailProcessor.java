package com.suning.cus.logical;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskDetailEvent;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;
import my.android.state.BaseState;


/**
 * 任务详情处理类
 * Created by 14110105 on 2015/4/16.
 */
public class TaskDetailProcessor extends BaseProcessor {

    private int mCallingId;

    public TaskDetailProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    public TaskDetailProcessor(int callingId, Context context, RequestParams params) {
        super(context, params);
        mCallingId = callingId;
    }

    @Override
    public void sendPostRequest() {

        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_TASK_DETAIL, params, this);

    }

    @Override
    public void onStart() {
        EventBus.getDefault().post(createLoadingProgressEvent(true));
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);
        DebugLog.d(response.result);

        Gson gson = new Gson();
        TaskDetail json;

        try {
            json = gson.fromJson(response.result, TaskDetail.class);

            if (json != null) {
                String result = json.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {

                    EventBus.getDefault().post(new TaskDetailEvent(mCallingId, json));
                } else {
                    postFailure(json.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }

        EventBus.getDefault().post(createLoadingProgressEvent(false));
    }

    @Override
    public void onFailure(HttpException error, String msg) {

        EventBus.getDefault().post(createLoadingProgressEvent(false));
        EventBus.getDefault().post(new RequestFailEvent(mCallingId, msg));
    }

    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowLoadingProgressEvent(getCallingId(), show);
    }

    public int getCallingId() {
        return mCallingId;
    }
}
