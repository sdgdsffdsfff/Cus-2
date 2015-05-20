package com.suning.cus.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.R;
import com.suning.cus.adapter.FixedMaterialListAdapter;
import com.suning.cus.bean.Material;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskFinishEvent;
import com.suning.cus.extras.ExtraListView;
import com.suning.cus.logical.TaskFinishProcessor;
import com.suning.cus.module.MaterialItem;
import com.suning.cus.utils.DateTimeUtils;
import com.suning.cus.utils.ListViewUtils;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 次日和另约 任务界面
 * Created by 14110105 on 2015/3/18.
 */
public class TaskFinishOtherDayActivity extends BaseActivity {

    /**
     * 非配件查询
     */
    public static final int REQUEST_CODE_MATERIAL = 10;

    /**
     * 是否为明日
     */
    private boolean isTomorrow;
    private boolean isCustomerReason = true;
    private TaskDetail mTaskDetail;


    @ViewInject(R.id.tr_other_date)
    private TableRow mOtherDateRow;

    @ViewInject(R.id.et_other_date)
    private EditText mOtherDateText;

    /**
     * 原因
     */
    @ViewInject(R.id.sp_reason)
    private Spinner mReason;

    @ViewInject(R.id.et_remark)
    private EditText mRemark;

    /**
     * 是否已经申请过配件
     */
    private boolean isApplied = false;
    /**
     * 配件列表
     */
    @ViewInject(R.id.lv_materials)
    private ExtraListView mMaterialsListView;
    private List<Material> mMaterials;
    private FixedMaterialListAdapter mMaterialListAdapter;

    /**
     * 配件总额相关的View
     */
    @ViewInject(R.id.ll_price_total)
    private LinearLayout mPriceLayout;
    @ViewInject(R.id.tv_price_total)
    private TextView mTotalPriceView;

    /**
     * 确定提交
     */
    @ViewInject(R.id.bt_submit)
    private Button mSubmitBtn;

    @ViewInject(R.id.bt_apply)
    private Button mApplyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_task_other_date);

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        ViewUtils.inject(this);
        /*初始化Intent传过来的值*/
        isTomorrow = getIntent().getBooleanExtra("isTomorrow", true);
        mTaskDetail = (TaskDetail) getIntent().getSerializableExtra("detail");

        /*根据传过来的isTomorrow参数显示Title的名字*/
        if (isTomorrow) {
            setTitle(getString(R.string.title_task_tomorrow));
            mOtherDateRow.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.title_task_other_day));
        }

        mMaterials = new ArrayList<>();
        mMaterialListAdapter = new FixedMaterialListAdapter(this);
        mMaterialListAdapter.setDataList(mMaterials);
        mMaterialsListView.setAdapter(mMaterialListAdapter);

        ArrayAdapter<CharSequence> mReasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.finish_task_reason, android.R.layout.simple_spinner_item);
        mReasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mReason.setAdapter(mReasonAdapter);

        mReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    showOpsButton(true);
                    isCustomerReason = false;
                } else {
                    showOpsButton(false);
                    isCustomerReason = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Noting to do
            }
        });
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
     * 显示或者隐藏 申请配件 按钮
     *
     * @param isShow 是否显示
     */
    private void showOpsButton(boolean isShow) {
        if (isShow) {
            if (!isApplied) {
                mApplyBtn.setVisibility(View.VISIBLE);
            }
            mPriceLayout.setVisibility(View.VISIBLE);
            mMaterialsListView.setVisibility(View.VISIBLE);
        } else {
            mApplyBtn.setVisibility(View.GONE);
            mPriceLayout.setVisibility(View.GONE);
            mMaterialsListView.setVisibility(View.GONE);
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_other_date:

                Calendar calendar = DateTimeUtils.calendar();
                DatePickerDialog dialog;
                // TODO: 这里设置了HOLO的主题，因为5.0之后的DatePickerDialog就算设置了MinDate和MaxDate，超过的日期依旧可以选择
                dialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mOtherDateText.setText(DateTimeUtils.formatDate(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH) + 2); //另约的起始日期为后天

                /*另约日期默认为空,选择时间起始为当前日期+2，范围控制在30天之内*/
                calendar.add(Calendar.DAY_OF_MONTH, 2);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, 28);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dialog.show();
                break;

            case R.id.bt_submit:
                submit();
                break;

            case R.id.bt_apply:
                Intent intent = new Intent();
                intent.setClass(TaskFinishOtherDayActivity.this, MaterialApplyActivity.class);
                intent.putExtra("product_type", "Z011");
                intent.putExtra("product_name", mTaskDetail.getProductDesc());
                intent.putExtra("product_code", mTaskDetail.getProductCode());
                startActivityForResult(intent, REQUEST_CODE_MATERIAL);

            default:
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MATERIAL && resultCode == RESULT_OK) {

            isApplied = true;
            mApplyBtn.setVisibility(View.GONE);

            ArrayList<Material> materials = (ArrayList<Material>) data.getSerializableExtra
                    ("MaterialList");

            DebugLog.d("materials size = " + materials.size());

            mMaterials.addAll(materials);

            mMaterialListAdapter.notifyDataSetChanged();
            ListViewUtils.setListViewHeightBasedOnChildren(mMaterialsListView);

            countMaterialPrice();
        }
    }


    /**
     * 计算总配件的总价格
     */
    public void countMaterialPrice() {

        String totalMaterialPrice = "0";
        for (Material material : mMaterials) {

            DebugLog.d(material.getMaterPrice() + " ,  " + material.getMaterNumber());

            if (material.getMaterPrice() != null && material.getMaterNumber() != null) {

                String p = MathUtils.count(material.getMaterPrice(), material.getMaterNumber(),
                        "*");
                totalMaterialPrice = MathUtils.count(totalMaterialPrice, p, "+");
            }
        }

        mTotalPriceView.setText(getString(R.string.material_total_price,
                String.valueOf(totalMaterialPrice)));

    }


    /**
     * 提交任务
     */
    public void submit() {

        HttpUtils http = new HttpUtils();
        http.configCookieStore(CusServiceApplication.COOKIE_STORE);

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId
                (TaskFinishOtherDayActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei
                (TaskFinishOtherDayActivity.this));
        params.addBodyParameter("serviceId", mTaskDetail.getServiceId());
        params.addBodyParameter("reasonRemark", mRemark.getText().toString());

        /*次日、另约原因： 顾客原因 = 003003、申配 = 001006*/
        String mReasonId;
        if (isCustomerReason) {
            mReasonId = "003003";
        } else {
            mReasonId = "001006";
        }
        params.addBodyParameter("reasonId", mReasonId);

        /* 销单类型，次日为“E0003”。另约为“E0004” */
        if (isTomorrow) {
            params.addBodyParameter("destoryStatus", "E0003");
        } else {

            if (mOtherDateText.getText().length() <= 0) {
                T.showShort(this, R.string.toast_error_date_required);
                return;
            }

            params.addBodyParameter("destoryStatus", "E0004");
            // TODO: 这里的日期后面要加时间，因为服务端的限制。。-_-
            params.addBodyParameter("anotherDate", mOtherDateText.getText().toString() + " " +
                    "00:00:00");
        }

        if(!isCustomerReason) {
            if (mMaterials.size() > 0) {
                List<MaterialItem> items = new ArrayList<>();

                for (Material material : mMaterials) {
                    MaterialItem item = new MaterialItem();
                    item.setUuid(MyUtils.getUUID());
                    item.setServiceId(mTaskDetail.getServiceId());  // 设置ServiceId
                    item.setMaterType("Z011");
                    item.setMaterProduct(material.getMaterCode());
                    item.setMaterdesc(material.getMaterDesc());
                    item.setBatch(material.getBatch());
                    item.setMaterPrice(material.getMaterPrice());
                    item.setMaterNumber(material.getMaterNumber());
                    item.setMaterAssurance(material.getMaterAssurance());
                    items.add(item);
                }
                Gson gson = new Gson();
                String materialString = gson.toJson(items);

                params.addBodyParameter("materList", materialString);

            } else {
                T.showShort(this, R.string.toast_material_field_required);
                return;
            }
        }

        /*请求处理*/
        TaskFinishProcessor processor = new TaskFinishProcessor(this, params);
        processor.setDialogEnabled(true);
        processor.setDialogMessage(R.string.dialog_submitting);
        processor.sendPostRequest();

    }


    /**
     * 销单成功后的回调
     *
     * @param event
     */
    public void onEvent(TaskFinishEvent event) {

        T.showShort(this, R.string.toast_task_finish_success);

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


    /**
     * 配件删除后，更新listview的界面
     */
    public void onMaterialDeleted() {
        ListViewUtils.setListViewHeightBasedOnChildren(mMaterialsListView);
    }


}
