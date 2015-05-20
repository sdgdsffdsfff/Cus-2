package com.suning.cus.logical;

import android.content.Context;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.event.RequestFailEvent;

/**
 * 基础逻辑处理类
 * Created by 14110105 on 2015/4/13.
 */
public abstract class BaseProcessor extends ProgressRequestCallBack<String>{

    protected Context mContext;
    /**
     * 请求参数
     */
    protected RequestParams params;
    /**
     * 网络请求失败的Event
     */
    protected RequestFailEvent requestFailEvent;


    public BaseProcessor(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseProcessor(Context context, RequestParams params) {
        this(context);
        this.params = params;
    }


    public  void setParams(RequestParams params) {
        this.params = params;
    }

    /**
     * 发送http请求
     */
    public abstract void sendPostRequest();
}
