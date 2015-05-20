package com.suning.cus;

import android.test.AndroidTestCase;

import com.lidroid.xutils.http.RequestParams;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskSummaryEvent;
import com.suning.cus.logical.TaskSummaryProcessor;
import com.suning.cus.utils.SpCoookieUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by 14110105 on 2015/3/12.
 */

public class TaskSummaryTest extends AndroidTestCase {


    public final Object mLock = new Object();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerEvents();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        unRegisterEvents();
    }

    public void registerEvents() {
        DebugLog.d("registerEvents");
        EventBus.getDefault().register(this);
    }

    public void unRegisterEvents() {
        DebugLog.d("unRegisterEvents");
        EventBus.getDefault().unregister(this);
    }



    public void onEvent(RequestFailEvent event) {
        fail(event.message);

        synchronized (mLock) {
            mLock.notifyAll();
        }
    }


    /**
     * 从服务器获取任务列表总数
     */
    public void testGetTaskSummary() {
//        registerEvents();
        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (getContext()));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(getContext()));

        TaskSummaryProcessor processor = new TaskSummaryProcessor(getContext());
        processor.setParams(params);
        processor.sendPostRequest();

        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void onEvent(TaskSummaryEvent event) {
        assertTrue(true);
        DebugLog.d(event.toString());

        synchronized (mLock) {
            mLock.notifyAll();
        }
    }


}

