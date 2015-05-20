package com.suning.cus.logical;

import android.app.ProgressDialog;
import android.content.Context;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.suning.cus.R;
import com.suning.cus.event.RequestFailEvent;

import de.greenrobot.event.EventBus;

/**
 * 加了ProgressDialog的RequestCallBack
 * Created by 14110105 on 2015/4/15.
 */
public abstract class ProgressRequestCallBack<T> extends RequestCallBack<T> {

    protected Context mContext;

    protected ProgressDialog mProgressDialog;
    protected String mDialogMessage;
    protected boolean isDialogEnabled = false;


    /**
     * 网络请求失败的Event
     */
    protected RequestFailEvent requestFailEvent;

    /**
     * 发送网络请求失败的event
     * @param message
     */
    protected  void postFailure(String message) {
        requestFailEvent = new RequestFailEvent(message);
        EventBus.getDefault().post(requestFailEvent);
    }


    public ProgressRequestCallBack(final Context context) {
        this.mContext = context;

    }

    /**
     * 设置Dialog的内容
     * @param message 内容
     */
    public void setDialogMessage(String message) {
        if (isDialogEnabled) {
            mProgressDialog.setMessage(message);
        }
    }

    public void setDialogMessage (int resId) {
        if (isDialogEnabled) {
            mProgressDialog.setMessage(mContext.getString(resId));
        }
    }

    /**
     * enable 等待框
     * @param enabled true or false
     * @return this
     */
    public ProgressRequestCallBack<T> setDialogEnabled(boolean enabled) {
        isDialogEnabled = enabled;

        if(isDialogEnabled && mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mDialogMessage = mContext.getString(R.string.dialog_loading);
            mProgressDialog.setMessage(mDialogMessage);
        }

        if(!isDialogEnabled && mProgressDialog != null) {
            mProgressDialog = null;
        }

        return this;
    }


    @Override
    public void onStart() {

        if(isDialogEnabled && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onSuccess(ResponseInfo<T> responseInfo) {
        if(isDialogEnabled && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onFailure(HttpException error, String msg) {
        if(isDialogEnabled && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        postNetworkError();
    }

    /**
     * 网络连接错误
     */
    protected void postNetworkError() {
        requestFailEvent = new RequestFailEvent(mContext.getString(R.string.toast_error_network));
        EventBus.getDefault().post(requestFailEvent);
    }
}
