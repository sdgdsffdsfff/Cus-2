package com.suning.cus.controllers;


import com.lidroid.xutils.http.RequestParams;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskDetailEvent;
import com.suning.cus.logical.TaskDetailProcessor;

import my.android.controllers.BaseUiController;
import my.android.state.ApplicationState;
import my.android.state.BaseState;

/**
 * 任务控制类，
 * Created by 14110105 on 2015/5/11.
 */
public class TaskController extends BaseUiController<TaskController.TaskUi,
        TaskController.TaskCallbacks> {

    private final ApplicationState mTaskState;

    public interface TaskUi extends BaseUiController.Ui<TaskCallbacks> {

        RequestParams getRequestParameter();

        void showError(String error);

        void showLoadingProgress(boolean visible);

    }

    public interface TaskDetailUi extends TaskUi {
        public void setDetail(TaskDetail taskDetail);
    }

    public interface TaskCallbacks {

        public void showDetails(TaskDetail detail);
    }

    public TaskController(ApplicationState taskState) {
        this.mTaskState = taskState;
    }

    @Override
    protected void onInited() {
        super.onInited();
        mTaskState.registerForEvents(this);
        DebugLog.d("EventBus register: " + this.getClass().getSimpleName());
    }


    @Override
    protected void onSuspended() {
        super.onSuspended();
        mTaskState.unregisterForEvents(this);
        DebugLog.d("EventBus unRegister: " + this.getClass().getSimpleName());
    }

    @Override
    protected void onUiAttached(TaskUi ui) {
        super.onUiAttached(ui);
        DebugLog.d(ui.getClass().getSimpleName());
        final int callingId = getId(ui);

        fetchTaskDetail(callingId, ui.getRequestParameter());

    }

    @Override
    protected void onUiDetached(TaskUi ui) {
        super.onUiDetached(ui);
        DebugLog.d(ui.getClass().getSimpleName());
    }

    @Override
    protected void populateUi(TaskUi ui) {
        super.populateUi(ui);

        if (ui instanceof TaskDetailUi) {
//            populateDetailUi((TaskDetailUi) ui);
        }

    }

    @Override
    protected void populateUi(TaskUi ui, BaseState.UiCausedEvent event) {
        super.populateUi(ui, event);

        if (ui instanceof TaskDetailUi) {
            populateDetailUi((TaskDetailUi) ui, (TaskDetailEvent)event);
        }
    }

    private void fetchTaskDetail(int callingId, RequestParams params) {
        /*开始请求*/
        TaskDetailProcessor processor = new TaskDetailProcessor(callingId, CusServiceApplication.instance
                .getApplicationContext(), params);
//        processor.setDialogEnabled(true);
        processor.sendPostRequest();
    }


    /**
     * 任务详情获取成功回调
     *
     * @param event 任务详情event
     */
    public void onEvent(TaskDetailEvent event) {
        populateUiFromEvent(event);

    }

    public void onEvent(RequestFailEvent event) {



        TaskUi ui = findUi(event.callingId);
        if (ui != null && null != event.message) {
            ui.showError(event.message);
        }
    }

    public void onEvent(BaseState.ShowLoadingProgressEvent event) {

        int callingId = event.callingId;

        TaskUi ui = findUi(callingId);
        if(ui != null) {
            ui.showLoadingProgress(event.show);
        }

    }


    private void populateDetailUi(TaskDetailUi ui, TaskDetailEvent event) {
        ui.setDetail(event.data);
    }


    @Override
    protected TaskCallbacks createUiCallbacks(TaskUi ui) {

        return new TaskCallbacks() {
            @Override
            public void showDetails(TaskDetail detail) {

            }
        };
    }


}
