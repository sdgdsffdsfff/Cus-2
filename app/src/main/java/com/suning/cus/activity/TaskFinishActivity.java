package com.suning.cus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mustafaferhan.debuglog.DebugLog;
import com.suning.cus.R;
import com.suning.cus.adapter.TaskMaterialAdapter;
import com.suning.cus.bean.Material;
import com.suning.cus.bean.TaskDetail;
import com.suning.cus.constants.BaseConstants;
import com.suning.cus.constants.TaskConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.MaterialDeleteEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.TaskFinishEvent;
import com.suning.cus.logical.TaskFinishProcessor;
import com.suning.cus.module.MaterialItem;
import com.suning.cus.utils.MathUtils;
import com.suning.cus.utils.MyUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;
import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 任务完成界面
 * Created by 14110105 on 2015/3/15.
 */
public class TaskFinishActivity extends BaseActivity {

    /**
     * 内机号扫描
     */
    public static final int REQUEST_CODE_SCAN_INNER_NUM = 1;
    /**
     * 外机号扫描
     */
    public static final int REQUEST_CODE_SCAN_OUTER_NUM = 2;

    /**
     * 非配件查询
     */
    public static final int REQUEST_CODE_MATERIAL = 10;
    /**
     * W库查询配件
     */
    public static final int REQUEST_CODE_MATERIAL_W = 11;

    public static final int REQUEST_CODE_MAINTAIN_FAULT = 12;

    public static final int REQUEST_CODE_MAINTAIN_FIX = 13;

    /**
     * 任务详情,从Intent中传递过来
     */
    private TaskDetail mTaskDetail;

    private String mMaintainMark;

    /**
     * 服务订单
     */
    @ViewInject(R.id.tv_service_id)
    private TextView mServiceId;
    /**
     * 内机号
     */
    @ViewInject(R.id.et_inner_machine_num)
    private EditText mInnerMachineNum;
    /**
     * 外机号
     */
    @ViewInject(R.id.et_outer_machine_num)
    private EditText mOuterMachineNum;
    /**
     * 安装卡号行
     */
    @ViewInject(R.id.tr_install_card)
    private TableRow mInstallCardRow;
    /**
     * 安装卡号输入框
     */
    @ViewInject(R.id.et_install_card)
    private EditText mInstallCardText;
    /**
     * 备注行
     */
//    @ViewInject(R.id.tr_remark)
//    private TableRow mRemarkRow;
    /**
     * 备注输入框
     */
    @ViewInject(R.id.et_remark)
    private EditText mRemarkText;

    /**
     * 安装方式
     */
    @ViewInject(R.id.tr_install_prompt)
    private TableRow mInstallPromptRow;
    @ViewInject(R.id.sp_install_mode)
    private Spinner mInstallModeSpinner;

    /**
     * 故障
     */
    private String mFaultCode;
    @ViewInject(R.id.tr_fault_prompt)
    private TableRow mFaultPromptRow;
    @ViewInject(R.id.tv_fault_prompt_content)
    private TextView mFaultPrompt;


    /**
     * 维修措施
     */
    private String mFixCode;
    @ViewInject(R.id.tr_fix_measure)
    private TableRow mFixMeasureRow;
    @ViewInject(R.id.tv_fix_measure_content)
    private TextView mFixMeasure;

    /**
     * 收费
     */
    @ViewInject(R.id.et_material_real_price)
    private EditText mMaterialTotalPrice;
    @ViewInject(R.id.et_service_price)
    private EditText mServicePrice;
    @ViewInject(R.id.tv_total_payment)
    private TextView mTotalPrice;

    /**
     * 配件相关变量
     */
    @ViewInject(R.id.ll_add_material)
    private LinearLayout mAddMaterialLayout;

    private ListView mMaterialsListView;
    private List<Material> mMaterials;
    private TaskMaterialAdapter mMaterialListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomContentView(R.layout.activity_task_finish);
        setTitle(getString(R.string.title_task_finish));

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mMaterialsListView = (ListView) findViewById(R.id.lv_materials);

        View header = getLayoutInflater().inflate(R.layout.list_header_task_finish, null, false);
        View footer = getLayoutInflater().inflate(R.layout.list_footer_task_finish, null, false);

        mMaterialsListView.addHeaderView(header);
        mMaterialsListView.addFooterView(footer);

        // 注册View
        ViewUtils.inject(this);

        // 获取传递过来的Intent参数
        mTaskDetail = (TaskDetail) getIntent().getSerializableExtra("detail");

        // 任务的质保标识
        String qualityAssurance = mTaskDetail.getQualityAssurance();

        /*如果服务订单质保标识为 2-延保、3-意外保 时，无添加材配按钮*/
        if (!TextUtils.isEmpty(qualityAssurance) && (qualityAssurance.equals("2")
                || qualityAssurance.equals("3"))) {
            mAddMaterialLayout.setVisibility(View.GONE);
        }

        // 配件相关初始化
        mMaterials = new ArrayList<>();
        mMaterialListAdapter = new TaskMaterialAdapter(this);
        mMaterialListAdapter.setMaterials(mMaterials);
        mMaterialsListView.setAdapter(mMaterialListAdapter);

        // 服务订单
        mServiceId.setText(mTaskDetail.getServiceId());

        // 安装模式
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.install_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInstallModeSpinner.setAdapter(adapter);

        // maintainMark
        // “maintainMark”: (安维表示值分为1,2,3,4
        // 1空调安装，出现空调安装销单界面(安装卡号)，
        // 2黑电安装，出现黑电安装销单界面（安装方式），
        // 3其他安装，出现其他安装销单界面，
        // 4维修，出现维修销单界面。
        mMaintainMark = mTaskDetail.getMaintainMark();
        if (mMaintainMark.equals("1")) {
            mInstallCardRow.setVisibility(View.VISIBLE);
//            mRemarkRow.setVisibility(View.VISIBLE);
        } else if (mMaintainMark.equals("2")) {
            mInstallPromptRow.setVisibility(View.VISIBLE);
        } else if (mMaintainMark.equals("4")) {
            mFaultPromptRow.setVisibility(View.VISIBLE);
            mFixMeasureRow.setVisibility(View.VISIBLE);
        }

        // 设置服务价格的监听事件
        mServicePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String changed = s.toString();
                String total = MathUtils.count(mMaterialTotalPrice.getText().toString(), changed, "+");
                mTotalPrice.setText(total);
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
     * 点击按钮时的响应事件
     *
     * @param v
     */
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_inner_machine_get:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN_INNER_NUM);
                break;
            case R.id.iv_outer_machine_get:
                Intent intent2 = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent2, REQUEST_CODE_SCAN_OUTER_NUM);
                break;
            case R.id.btn_add_material:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                String serviceOrderType = mTaskDetail.getServiceOrderType();

                // TODO: 写死的代码，后面需修改
                if (serviceOrderType.equals("ZS06") || serviceOrderType.equals("ZS07")
                        || serviceOrderType.equals("ZS08")
                        || serviceOrderType.equals("ZS11")
                        || serviceOrderType.equals("ZS12")
                        || serviceOrderType.equals("ZGS6")
                        || serviceOrderType.equals("ZGS8")) {

                    builder.setItems(R.array.material_apply_types,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which != 0) {
                                        final Intent intent = new Intent(TaskFinishActivity.this,
                                                SearchMaterialActivity.class);
                                        intent.putExtra(TaskConstants.SERVICE_ORDER_TYPE,
                                                mTaskDetail.getServiceOrderType());
                                        intent.putExtra("qualityAssurance",
                                                mTaskDetail.getQualityAssurance()); // Task的质保标识
                                        intent.putExtra("serviceOrg", mTaskDetail.getServiceOrg()); // Task的服务组织
                                        intent.putExtra("saleOrg", mTaskDetail.getSaleOrg());
                                        intent.putExtra("channel", mTaskDetail.getChannel());
                                        // 标记是否从TaskFinishActivity跳转过去，用于区分Title
                                        intent.putExtra("isTaskFinish", true);
                                        startActivityForResult(intent, REQUEST_CODE_MATERIAL);
                                    } else {
                                        final Intent intent = new Intent(TaskFinishActivity.this,
                                                WManageActivity.class);
                                        intent.putExtra(TaskConstants.SERVICE_ORDER_TYPE,
                                                mTaskDetail.getServiceOrderType());
                                        intent.putExtra("qualityAssurance",
                                                mTaskDetail.getQualityAssurance()); // Task的质保标识
                                        intent.putExtra("serviceOrg", mTaskDetail.getServiceOrg()); // Task的服务组织
                                        intent.putExtra("saleOrg", mTaskDetail.getSaleOrg());
                                        intent.putExtra("channel", mTaskDetail.getChannel());
                                        intent.putExtra(WManageActivity.TYPE, WManageActivity.JUMP);
                                        startActivityForResult(intent, REQUEST_CODE_MATERIAL_W);
                                    }
                                }
                            });

                } else if (serviceOrderType.equals("ZS01")
                        || serviceOrderType.equals("ZS02")
                        || serviceOrderType.equals("ZS03")
                        || serviceOrderType.equals("ZS04")
                        || serviceOrderType.equals("ZGS5")) {

                    builder.setItems(R.array.material_apply_types_2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent intent = new Intent(TaskFinishActivity.this,
                                    SearchMaterialActivity.class);
                            intent.putExtra(TaskConstants.SERVICE_ORDER_TYPE,
                                    mTaskDetail.getServiceOrderType());
                            intent.putExtra("qualityAssurance", mTaskDetail.getQualityAssurance()); // Task的质保标识
                            intent.putExtra("serviceOrg", mTaskDetail.getServiceOrg()); // Task的服务组织
                            intent.putExtra("isTaskFinish", true);
                            startActivityForResult(intent, REQUEST_CODE_MATERIAL);
                        }
                    });
                }

                builder.show();
                break;

            case R.id.tv_fault_prompt_content:
                intent = new Intent(this, SearchMaintainActivity.class);
                intent.putExtra("kind", "GZYY");
                startActivityForResult(intent, REQUEST_CODE_MAINTAIN_FAULT);
                break;

            case R.id.tv_fix_measure_content:
                intent = new Intent(this, SearchMaintainActivity.class);
                intent.putExtra("kind", "WXCS");
                startActivityForResult(intent, REQUEST_CODE_MAINTAIN_FIX);
                break;

            case R.id.bt_finish_destroy_order:
                submit();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_INNER_NUM && resultCode == RESULT_OK) {
            mInnerMachineNum.setText(data.getStringExtra(CaptureActivity.FIELD_SCAN_RESULT));
        } else if (requestCode == REQUEST_CODE_SCAN_OUTER_NUM && resultCode == RESULT_OK) {
            mOuterMachineNum.setText(data.getStringExtra(CaptureActivity.FIELD_SCAN_RESULT));
        }

        /*从W库查询返回*/
        if ((requestCode == REQUEST_CODE_MATERIAL_W && resultCode == BaseConstants.W_MANAGE_RESULT_CODE)
                || (requestCode == REQUEST_CODE_MATERIAL && resultCode == RESULT_OK)) {

            String material_code = data.getStringExtra("material_code");
            String material_name = data.getStringExtra("material_name");
            String material_batch = data.getStringExtra("material_batch");
            String material_price = data.getStringExtra("material_price");
            String material_allow_count = data.getStringExtra("material_allow_count");
            ArrayList<String> material_assurance = data.getStringArrayListExtra("material_assurance");

            /**
             * 如果已经申请过，直接返回
             */
            if (isMaterialExist(material_code)) {
                T.showShort(this, R.string.error_material_exists);
                return;
            }

            Material material = new Material();

            /*设置配件类型，W库查询过来的配件类型都为Z011*/
            if (requestCode == REQUEST_CODE_MATERIAL_W) {
                material.setMaterType("Z011");
                material.setStore(material_allow_count);
            } else {
                String material_type = data.getStringExtra("material_type");
                material.setMaterType(material_type);
            }

            material.setMaterCode(material_code);
            material.setMaterDesc(material_name);
            material.setBatch(material_batch);
            material.setMaterPrice(material_price);

            if (material_assurance != null && material_assurance.size() > 0) {
                material.setAssuranceList(material_assurance);
            }

            material.setMaterNumber("1");

            mMaterials.add(material);
            mMaterialListAdapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CODE_MAINTAIN_FAULT && resultCode == RESULT_OK) {
            mFaultCode = data.getStringExtra("currentId");
            mFaultPrompt.setText(data.getStringExtra("desc"));
        }

        if (requestCode == REQUEST_CODE_MAINTAIN_FIX && resultCode == RESULT_OK) {
            mFixCode = data.getStringExtra("currentId");
            mFixMeasure.setText(data.getStringExtra("desc"));
        }

    }

    /**
     * 检查配件是否已经申请过了
     *
     * @param code 配件编码
     * @return true-已存在  false-不存在
     */
    private boolean isMaterialExist(String code) {

        if (mMaterials != null && !TextUtils.isEmpty(code)) {
            for (Material material : mMaterials) {
                if (code.equals(material.getMaterCode())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }


    /**
     * 提交
     */
    public void submit() {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(TaskFinishActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(TaskFinishActivity.this));
        params.addBodyParameter("serviceId", mTaskDetail.getServiceId());
        /*设置销单状态*/
        params.addBodyParameter("destoryStatus", "E0002");
        params.addBodyParameter("maintainMark", mMaintainMark);

        params.addBodyParameter("innerNO", mInnerMachineNum.getText().toString());
        params.addBodyParameter("outerNO", mOuterMachineNum.getText().toString());
        /*备注*/
        params.addBodyParameter("reasonRemark", mRemarkText.getText().toString());
        // maintainMark
        // “maintainMark”: (安维表示值分为1,2,3,4
        // 1空调安装，出现空调安装销单界面(安装卡号)，
        // 2黑电安装，出现黑电安装销单界面（安装方式），
        // 3其他安装，出现其他安装销单界面，
        // 4维修，出现维修销单界面。
        if (mMaintainMark.equals("1")) {
            // 安装卡号
            if(mInstallCardText.getText().length() <= 0) {
                T.showShort(this, R.string.error_required_install_number);
                return;
            }

            params.addBodyParameter("installNO", mInstallCardText.getText().toString());

        } else if (mMaintainMark.equals("2")) {
            // 黑电安装类型
            params.addBodyParameter("installType", String.valueOf(mInstallModeSpinner
                    .getSelectedItemId() + 1));
        } else if (mMaintainMark.equals("4")) {

            if (TextUtils.isEmpty(mFaultCode) || TextUtils.isEmpty(mFixCode)) {
                T.showShort(this, getString(R.string.error_required_fault));
                return;
            }
            params.addBodyParameter("faultCode", mFaultCode);
            params.addBodyParameter("maintenCode", mFixCode);
        }

        // 材配价格/服务价格/总价格
        params.addBodyParameter("serviceAmount", mServicePrice.getText().toString());
        params.addBodyParameter("amount", mTotalPrice.getText().toString());

        // 添加配件
        if (mMaterials.size() > 0) {
            List<MaterialItem> items = new ArrayList<>();

            for (Material material : mMaterials) {
                MaterialItem item = new MaterialItem();
                item.setUuid(MyUtils.getUUID());
                item.setServiceId(mTaskDetail.getServiceId());  // 设置ServiceId
                item.setMaterType(material.getMaterType());
                item.setMaterProduct(material.getMaterCode());
                item.setMaterdesc(material.getMaterDesc());
                item.setBatch(material.getBatch());
                item.setMaterPrice(material.getMaterPrice());
                item.setMaterNumber(material.getMaterNumber());
                item.setMaterAssurance(material.getMaterAssurance());  // 质保标识
                items.add(item);
            }
            Gson gson = new Gson();
            /*转换为json 传给服务端*/
            String materialString = gson.toJson(items);

            params.addBodyParameter("materList", materialString);
            params.addBodyParameter("materTotalAmount", mMaterialTotalPrice.getText().toString());
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
     * 材配删除后的回调
     *
     * @param event
     */
    public void onEvent(MaterialDeleteEvent event) {
        // 重新计算价格
        countMaterialPrice();
    }


    public void onMaterialNumChanged() {
        countMaterialPrice();
    }

    /**
     * 计算配件的总价格
     */
    public void countMaterialPrice() {

        String totalMaterialPrice = "0";
        for (Material material : mMaterials) {

            /* 配件为保外类型才计算价格*/
            if ("02".equals(material.getMaterAssurance())) {

                DebugLog.d(material.getMaterPrice() + " ,  " + material.getMaterNumber());

                if (material.getMaterPrice() != null && material.getMaterNumber() != null) {
                    String p = MathUtils.count(material.getMaterPrice(), material.getMaterNumber(), "*");
                    totalMaterialPrice = MathUtils.count(totalMaterialPrice, p, "+");
                }
            }
        }

        mMaterialTotalPrice.setText(String.valueOf(totalMaterialPrice));

        String total = MathUtils.count(mMaterialTotalPrice.getText().toString(), mServicePrice.getText().toString(), "+");

        mTotalPrice.setText(total);
    }


}
