package com.suning.cus.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.controllers.MainController;
import com.suning.cus.controllers.TaskController;
import com.suning.cus.utils.T;

/**
 * 15010551
 * 需继承BaseActivity，并调用setCustomContentView才能显示自定义ActionBar
 */
public abstract class BaseTaskActivity extends BaseCusActivity implements MainController
        .HostCallbacks, TaskController.TaskDetailUi{

    private TaskController.TaskCallbacks mCallbacks;

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getString(R.string.dialog_waiting));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getController().attachUi(this);
    }


    @Override
    protected void onPause() {
        getController().detachUi(this);
        super.onPause();
    }

    private TaskController getController() {
        return CusServiceApplication.getInstance().getMainController().getTaskController();
    }


    @Override
    public void showError(String error) {
        T.showShort(this, error);
    }

    @Override
    public void showLoadingProgress(boolean visible) {

        if(visible) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setCallbacks(TaskController.TaskCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public boolean hasCallbacks(){
        return mCallbacks != null;
    }

    public TaskController.TaskCallbacks getCallbacks() {
        return mCallbacks;
    }

}
