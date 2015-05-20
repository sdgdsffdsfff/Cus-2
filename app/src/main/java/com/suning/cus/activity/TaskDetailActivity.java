package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.suning.cus.R;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.constants.TaskConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.BespokeTimeEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.logical.BespokeTimeProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

/**
 * 任务详情
 * Created by 14110105 on 2015/3/14.
 */
public class TaskDetailActivity extends BaseTaskActivity {

    /**
     * Start activity request code
     */
    public static final int REQUEST_CODE_PROCESS_TASK = 1;

    /**
     * Intent 传递过来
     */
    private String mServiceId;


    private TaskDetail mTaskDetail;

    /*用户信息相关*/
    @ViewInject(R.id.tv_client_name)
    private TextView mNameView;
    @ViewInject(R.id.tv_client_tele)
    private TextView mTelephoneView;
    @ViewInject(R.id.ib_client_tele)
    private ImageButton mTelephoneDialer;
    @ViewInject(R.id.tv_client_phone)
    private TextView mPhoneView;
    @ViewInject(R.id.ib_client_phone)
    private ImageButton mPhoneDialer;
    @ViewInject(R.id.tv_client_address)
    private TextView mAddressView;
    @ViewInject(R.id.tv_order_priority_label)
    private TextView mPriorityLabel;
    @ViewInject(R.id.tv_order_priority)
    private TextView mPriorityView;


    @ViewInject(R.id.tv_service_id)
    private TextView mServiceIdView;
    @ViewInject(R.id.tv_product_desc)
    private TextView mProductDescView;
    @ViewInject(R.id.tv_service_product)
    private TextView mServiceProductView;
    @ViewInject(R.id.tv_service_order_type)
    private TextView mServiceOrderTypeView;
    @ViewInject(R.id.tv_send_time)
    private TextView mSendTimeView;
    @ViewInject(R.id.tv_book_time)
    private TextView mBookTimeView;
    @ViewInject(R.id.tv_bespoke_time)
    private TextView mBespokeTimeView;
    private String mBespokeTime;

    @ViewInject(R.id.tv_complete_time_label)
    private TextView mCompleteTimeLabel;
    @ViewInject(R.id.tv_complete_time)
    private TextView mCompleteTimeView;

    @ViewInject(R.id.tv_remark)
    private TextView mRemarkView;
    @ViewInject(R.id.tv_is_nc_medium)
    private TextView mIsNCMediumView; // 是否新冷媒
    @ViewInject(R.id.tv_quality_assurance)
    private TextView mQualityAssuranceView;// 质保标识
    @ViewInject(R.id.tv_previous_desc)
    private TextView mPreviousDescView;// 上次派工信息
    /**
     * 故障描述
     */
    @ViewInject(R.id.tv_desc)
    private TextView mDescView;

    /**
     * 收费
     */
    @ViewInject(R.id.rl_price)
    private RelativeLayout mPriceLayout;
    @ViewInject(R.id.tv_material_price_label)
    private TextView mMaterialPriceLabel;
    @ViewInject(R.id.tv_material_price)
    private TextView mMaterialPriceView;
    @ViewInject(R.id.tv_service_price_label)
    private TextView mServicePriceLabel;
    @ViewInject(R.id.tv_service_price)
    private TextView mServicePriceView;
    @ViewInject(R.id.tv_amount_price_label)
    private TextView mAmountPriceLabel;
    @ViewInject(R.id.tv_amount_price)
    private TextView mAmountPriceView;


    @ViewInject(R.id.bt_complete)
    private Button mCompleteBtn;
    @ViewInject(R.id.bt_next_day)
    private Button mNextDayBtn;
    @ViewInject(R.id.bt_covenant)
    private Button mCovenantBtn;
    @ViewInject(R.id.bt_cancel)
    private Button mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_task_detail;
    }

    /**
     * 初始化
     */
    private void init() {

//        setTitle(getString(R.string.title_task_detail));

        ViewUtils.inject(this);

        mServiceId = getIntent().getStringExtra("serviceId");
    }

    /**
     * 按钮响应事件
     *
     * @param v 点击的Button
     */
    public void onClick(View v) {

        Intent intent = new Intent();

        switch (v.getId()) {

            case R.id.tv_bespoke_time:
                showTimeDialog();
                break;
            case R.id.bt_complete:
                intent.setClass(TaskDetailActivity.this, TaskFinishActivity.class);
                intent.putExtra("detail", mTaskDetail);
                startActivityForResult(intent, REQUEST_CODE_PROCESS_TASK);
                break;
            case R.id.bt_next_day:
                intent.setClass(TaskDetailActivity.this, TaskFinishOtherDayActivity.class);
                intent.putExtra("detail", mTaskDetail);
                intent.putExtra("isTomorrow", true);
                startActivityForResult(intent, REQUEST_CODE_PROCESS_TASK);
                break;
            case R.id.bt_covenant:
                intent.setClass(TaskDetailActivity.this, TaskFinishOtherDayActivity.class);
                intent.putExtra("detail", mTaskDetail);
                intent.putExtra("isTomorrow", false);
                startActivityForResult(intent, REQUEST_CODE_PROCESS_TASK);
                break;
            case R.id.bt_cancel:
                intent.setClass(TaskDetailActivity.this, TaskCancelActivity.class);
                intent.putExtra("detail", mTaskDetail);
                startActivityForResult(intent, REQUEST_CODE_PROCESS_TASK);

                break;
            default:
                break;
        }

    }


    /**
     * 显示整点时间选择框
     */
    private void showTimeDialog() {

        final String[] times = getResources().getStringArray(R.array.array_time);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.time_choose_dialog_title).setItems(times,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBespokeTime(mServiceId, times[which]);
            }
        });
        builder.show();
    }

    /**
     * 发送预约排程到服务端
     *
     * @param id          服务订单ID
     * @param bespokeTime 预约时间
     */
    public void sendBespokeTime(final String id, final String bespokeTime) {

        mBespokeTime = bespokeTime;

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (TaskDetailActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(TaskDetailActivity
                .this));
        params.addBodyParameter(TaskConstants.TASK_SERVICE_ID, id);
        params.addBodyParameter("bespokeTime", bespokeTime.replace(":00", ""));

        /**
         * 发送请求
         */
        BespokeTimeProcessor processor = new BespokeTimeProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_submitting);
        processor.sendPostRequest();

    }


    @Override
    public RequestParams getRequestParameter() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (TaskDetailActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(TaskDetailActivity
                .this));
        params.addBodyParameter(TaskConstants.TASK_SERVICE_ID, mServiceId);
        return params;
    }

    /**
     * 预约排程成功回调
     *
     * @param event 预约排程event
     */
    public void onEvent(BespokeTimeEvent event) {
        mBespokeTimeView.setText(mBespokeTime);
        mBespokeTimeView.setClickable(false);
    }

    /**
     * 请求出错的回调函数
     *
     * @param event 请求出错Event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(this, event.message);
    }

    /**
     * 给UI赋值
     */
    public void populateUi() {
        /*如果任务已处理并且是取消类型就不显示用户的信息*/
        if (TextUtils.isEmpty(mTaskDetail.getStatus()) || !mTaskDetail.getStatus().equals("3") ||
                !mTaskDetail.getServiceOrderStatus().equals("E0008")) {

            mNameView.setText(mTaskDetail.getClientName());

            if (!TextUtils.isEmpty(mTaskDetail.getClientTele())) {
                mTelephoneView.setText(mTaskDetail.getClientTele());
                mTelephoneDialer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:" + mTaskDetail.getClientTele()));
                        startActivity(intent);
                    }
                });
            } else {
                mTelephoneDialer.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mTaskDetail.getClientPhone())) {
                mPhoneView.setText(mTaskDetail.getClientPhone());
                mPhoneDialer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:" + mTaskDetail.getClientPhone()));
                        startActivity(intent);
                    }
                });
            } else {
                mPhoneDialer.setVisibility(View.GONE);
            }

            mAddressView.setText(mTaskDetail.getClientAddress());
        } else {
            mNameView.setVisibility(View.GONE);
            mTelephoneView.setVisibility(View.GONE);
            mPhoneView.setVisibility(View.GONE);
            mTelephoneDialer.setVisibility(View.GONE);
            mPhoneDialer.setVisibility(View.GONE);
            mAddressView.setVisibility(View.GONE);
        }

        /*优先级别*/
        if (!TextUtils.isEmpty(mTaskDetail.getOrderPrior())) {
            mPriorityLabel.setVisibility(View.VISIBLE);
            mPriorityView.setVisibility(View.VISIBLE);
            mPriorityView.setText(mTaskDetail.getOrderPrior());
        } else {
            mPriorityLabel.setVisibility(View.GONE);
            mPriorityView.setVisibility(View.GONE);
        }

        /*送货完成时间*/
        if (!TextUtils.isEmpty(mTaskDetail.getOperateTime())) {
            mCompleteTimeLabel.setVisibility(View.VISIBLE);
            mCompleteTimeView.setVisibility(View.VISIBLE);
            mCompleteTimeView.setText(mTaskDetail.getOperateTime());
        } else {
            mCompleteTimeLabel.setVisibility(View.GONE);
            mCompleteTimeView.setVisibility(View.GONE);
        }

        mServiceIdView.setText(mTaskDetail.getServiceId());
        mProductDescView.setText(mTaskDetail.getProductDesc());
        mServiceProductView.setText(mTaskDetail.getServiceProduct());
        mServiceOrderTypeView.setText(mTaskDetail.getServiceOrderType());
        mSendTimeView.setText(mTaskDetail.getSendTime());
        mBookTimeView.setText(mTaskDetail.getBookTime());

        /*如果预约排程的时间不为空则直接显示并且不让点击*/
        if (!TextUtils.isEmpty(mTaskDetail.getBespokeTime())) {
            mBespokeTimeView.setText(mTaskDetail.getBespokeTime());
            mBespokeTimeView.setClickable(false);
        }

        mRemarkView.setText(mTaskDetail.getRemark() + "     " + mTaskDetail.getReasonRemark());
        mIsNCMediumView.setText(mTaskDetail.getIsNCMedium()); // 是否新冷媒

        // 质保标识 - 任务相关，跟配件有区分
        if ("0".equals(mTaskDetail.getQualityAssurance())) {
            mQualityAssuranceView.setText("保内");
        } else if ("1".equals(mTaskDetail.getQualityAssurance())) {
            mQualityAssuranceView.setText("保外");
        } else if ("2".equals(mTaskDetail.getQualityAssurance())) {
            mQualityAssuranceView.setText("延保");
        } else if ("3".equals(mTaskDetail.getQualityAssurance())) {
            mQualityAssuranceView.setText("意外保");
        }

        mPreviousDescView.setText(mTaskDetail.getPreviousDesc());// 上次派工信息
        mDescView.setText(mTaskDetail.getDesc());// 故障描述

        // 如果单子已经处理过，把button都隐藏, 显示相关的收费
        if (!TextUtils.isEmpty(mTaskDetail.getStatus()) && mTaskDetail.getStatus().equals("3")) {
             /*Disable预约排程，如果内容为空就清空提示*/
            mBespokeTimeView.setClickable(false);
            if (mBespokeTimeView.getText().length() <= 0) {
                mBespokeTimeView.setHint("");
            }

            mCompleteBtn.setVisibility(View.GONE);
            mNextDayBtn.setVisibility(View.GONE);
            mCovenantBtn.setVisibility(View.GONE);
            mCancelBtn.setVisibility(View.GONE);

            mPriceLayout.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(mTaskDetail.getMaterTotalAmount())) {
                mMaterialPriceView.setText(getString(R.string.price, mTaskDetail
                        .getMaterTotalAmount()));
            } else {
                mMaterialPriceView.setText(getString(R.string.price,"0.00"));
            }

            if (!TextUtils.isEmpty(mTaskDetail.getServiceAmount())) {
                mServicePriceView.setText(getString(R.string.price, mTaskDetail.getServiceAmount()));
            } else {
                mServicePriceView.setText(getString(R.string.price,"0.00"));
            }

            if (!TextUtils.isEmpty(mTaskDetail.getAmount())) {
                mAmountPriceView.setText(getString(R.string.price, mTaskDetail.getAmount()));
            } else {
                mAmountPriceView.setText(getString(R.string.price, "0.00"));
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PROCESS_TASK && resultCode == RESULT_OK) {
            // 重新获取刷新任务详情
//            getTaskDetail(mServiceId);
        }
    }


    @Override
    public void setDetail(TaskDetail taskDetail) {
        mTaskDetail = taskDetail;
        populateUi();
    }
}
