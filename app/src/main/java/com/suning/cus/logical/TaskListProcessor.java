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
import com.suning.cus.event.TaskListEvent;
import com.suning.cus.json.JsonTaskList;
import com.suning.cus.utils.XUtilsHelper;

import de.greenrobot.event.EventBus;

/**
 * 获取任务列表处理类
 * Created by 14110105 on 2015/4/13.
 */
public class TaskListProcessor extends BaseProcessor {


    public TaskListProcessor(Context context, RequestParams params) {
        super(context, params);
    }

    private QueryFilter mQueryFilter = QueryFilter.ALL;


    public void setQueryFilter(QueryFilter filter) {
        mQueryFilter = filter;
    }

    @Override
    public void sendPostRequest() {
        XUtilsHelper helper = new XUtilsHelper(mContext);
        helper.sendPost(ServerConfig.URL_TASKS_LIST, params, this);
    }

    @Override
    public void onSuccess(ResponseInfo<String> response) {
        super.onSuccess(response);

        DebugLog.d(response.result);

        Gson gson = new Gson();
        JsonTaskList jsonTaskList;

        try {
            jsonTaskList = gson.fromJson(response.result, JsonTaskList.class);

            if (jsonTaskList != null) {
                String result = jsonTaskList.getIsSuccess();
                if (!TextUtils.isEmpty(result) && result.equals("S")) {

                    TaskListEvent event;
                    event = new TaskListEvent(jsonTaskList);
                    event.setFilter(mQueryFilter);
                    EventBus.getDefault().post(event);
                } else {
                    postFailure(jsonTaskList.getErrorDesc());
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            postFailure(mContext.getString(R.string.toast_error_json));
        }
    }


    /**
     * 任务查询类型
     */
    public enum QueryFilter {
        ALL(' '),        /*所有的任务*/
        PROCESSED('1'),  /*已处理的任务*/
        UNPROCESSED('2');/*未处理的任务*/

        private final char mFilter;

        private QueryFilter(char filter) {
            this.mFilter = filter;
        }

        public char value() {
            return mFilter;
        }

        public static QueryFilter valueOf(char filter) {

            switch (filter) {
                case ' ':
                    return ALL;
                case '1':
                    return PROCESSED;
                case '2':
                    return UNPROCESSED;
                default:
                    return null;
            }
        }

    }

}
