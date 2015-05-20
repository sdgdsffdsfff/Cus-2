package com.suning.cus.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskFinishEvent;
import com.suning.cus.logical.TaskFinishProcessor;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 任务取消
 * Created by 14110105 on 2015/4/3.
 */
public class TaskCancelActivity extends BaseActivity {

    /**
     * 取消备注
     */
    @ViewInject(R.id.et_remark)
    private EditText mEditRemark;


    private TaskDetail mTaskDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_task_cancel);

        setTitle(getString(R.string.title_task_cancel));

        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*注册subscribers*/
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*把自己从subscribers中移除，不再接收*/
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化
     */
    public void init() {

        ViewUtils.inject(this);

        mTaskDetail = (TaskDetail) getIntent().getSerializableExtra("detail");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                String reasonRemark = mEditRemark.getText().toString().trim();
                String guid = MyUtils.getUUID();

                RequestParams params = new RequestParams();
                params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(TaskCancelActivity.this));
                params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(TaskCancelActivity.this));
                params.addBodyParameter("serviceId", mTaskDetail.getServiceId());
                params.addBodyParameter("reasonId", "");
                params.addBodyParameter("reasonRemark", reasonRemark);
                params.addBodyParameter("destoryStatus", "E0005");
                params.addBodyParameter("guid", guid);

                /*请求处理*/
                TaskFinishProcessor processor = new TaskFinishProcessor(this, params);
                processor.setDialogEnabled(true);
                processor.setDialogMessage(R.string.dialog_submitting);
                processor.sendPostRequest();

                break;
            case R.id.bt_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 销单成功后的回调
     *
     * @param event 任务完成event
     */
    public void onEvent(TaskFinishEvent event) {

        T.showShort(this, R.string.toast_cancel_success);

        /*设置返回结果为OK，finish self*/
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(this, event.message);
    }


}
