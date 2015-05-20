package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.R;
import com.suning.cus.constants.SettingsConstants;
import com.suning.cus.event.DeleteMessageEvent;
import com.suning.cus.event.GetMessageDetailEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.DeleteMessageProcessor;
import com.suning.cus.logical.GetMessageDetailProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 通知详情页面
 * 15010551
 */
public class MessageDetailActivity extends BaseActivity {

    // Intent Bundle Key (in): 通知ID
    public static final String MESSAGE_ID = "messageId";

    /**
     * 业务相关
     */
    // 通知ID
    private String mMessageId = "";

    /**
     * UI相关
     */
    private TextView messageDetailTextView;

    private TextView messageTimeTextView;

    private TextView messageTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageId = getIntent().getStringExtra(MESSAGE_ID);
        setCustomContentView(R.layout.activity_message_detail);
        setTitle(getString(R.string.title_message_detail));
        initView();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        requestGetMessageDetail(mMessageId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 注册subscribers
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        messageDetailTextView = (TextView) findViewById(R.id.tv_message_detail);
        messageDetailTextView.setText("");
        messageTimeTextView = (TextView) findViewById(R.id.tv_notice_time);
        messageTimeTextView.setText("");
        messageTitleTextView = (TextView) findViewById(R.id.tv_notice_title);
        messageTitleTextView.setText("");
    }

    /**
     * 获取通知详细信息
     *
     * @param messageId
     */
    private void requestGetMessageDetail(String messageId) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(SettingsConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(MessageDetailActivity.this));
        params.addBodyParameter(SettingsConstants.IMEI, SpCoookieUtils.getImei(MessageDetailActivity.this));
        params.addBodyParameter(SettingsConstants.MESSAGE_ID, messageId);

        GetMessageDetailProcessor mProcessor = new GetMessageDetailProcessor(MessageDetailActivity.this);
        mProcessor.setParams(params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.dialog_waiting));
        mProcessor.sendPostRequest();
    }

    /**
     * 删除消息
     *
     * @param messageId
     */
    private void requestDeleteMessage(String messageId) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(SettingsConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(MessageDetailActivity.this));
        params.addBodyParameter(SettingsConstants.IMEI, SpCoookieUtils.getImei(MessageDetailActivity.this));
        params.addBodyParameter(SettingsConstants.MESSAGE_ID, messageId);

        DeleteMessageProcessor mProcessor = new DeleteMessageProcessor(MessageDetailActivity.this);
        mProcessor.setParams(params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_del_notice));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收消息详细信息的Event
     *
     * @param event
     */
    public void onEvent(GetMessageDetailEvent event) {
        messageDetailTextView.setText(event.jsonMessageDetail.getContent());
        messageTitleTextView.setText(event.jsonMessageDetail.getTitle());
        messageTimeTextView.setText(event.jsonMessageDetail.getTime());
    }

    /**
     * 接收删除消息的Event
     *
     * @param event
     */
    public void onEvent(DeleteMessageEvent event) {
        T.showShort(MessageDetailActivity.this, getString(R.string.del_success));
        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * 接收请求失败的Event
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(MessageDetailActivity.this, event.message);
    }

    /**
     * 删除按钮的响应方法
     *
     * @param v
     */
    public void deleteMessage(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageDetailActivity.this);
        builder.setMessage(getString(R.string.dialog_delete_alert));
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestDeleteMessage(mMessageId);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    /**
     * 返回按钮的响应方法
     *
     * @param v
     */
    public void goBack(View v) {
        finish();
    }
}
