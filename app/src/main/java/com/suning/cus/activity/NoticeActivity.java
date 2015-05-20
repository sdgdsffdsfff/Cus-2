package com.suning.cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.MessageGeneralListAdapter;
import com.suning.cus.bean.MessageList;
import com.suning.cus.constants.SettingsConstants;
import com.suning.cus.constants.UserConstants;
import com.suning.cus.event.MessageGeneralEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.extras.pulltorefresh.PullToRefreshBase;
import com.suning.cus.extras.pulltorefresh.PullToRefreshListView;
import com.suning.cus.json.JsonMessageGeneral;
import com.suning.cus.logical.MessageGeneralProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 通知页面
 * 15010551
 */

public class NoticeActivity extends BaseActivity implements View.OnClickListener {

    // Handler Msg Code: 查看详细消息
    public static final int HANDLER_CODE_CHECK_NOTICE_DETAIL = 0x0001;

    // Handler Bundle Key：Message ID
    public static final String MESSAGE_ID = "messageId";

    // Handler Bundle Key：某条message在通知listView中的位置
    public static final String MESSAGE_POSITION = "messagePosition";

    // Activity Request Code：查看消息详细信息
    public static final int REQUEST_CODE_CHECK_NOTICE_DETAIL = 0x0001;

    /**
     * 业务相关
     */
    private MessageGeneralListAdapter mAdapter;

    private AnimationAdapter mAnimAdapter;

    public static Handler handler;

    public int messagePosition;

    private int mCurrentPage;

    /**
     * UI相关
     */

    private PullToRefreshListView noticePullListView;

    private ListView noticeListView;

    private Button loadMoreBtn;

    private View loading;

    private View footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_notice);
        setTitle(getString(R.string.title_notice));
        mCurrentPage = 1;
        initView();
        initHandler();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

//        requestMessageGeneral(String.valueOf(mCurrentPage));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 注册subscribers
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        noticePullListView.doPullRefreshing(true, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 从subscribers中移除，不再接收
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //    super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHECK_NOTICE_DETAIL:
                if (resultCode == RESULT_OK) {
                    mAdapter.removeDataList(messagePosition);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_load_more:
                setFooterViewLoading();
                requestMessageGeneral(String.valueOf(mCurrentPage));
                break;
        }
    }

    public void initView() {
        noticePullListView = (PullToRefreshListView) findViewById(R.id.lv_notice);
//        noticeListView = (ListView) findViewById(R.id.lv_notice);
        noticeListView = noticePullListView.getRefreshableView();
        noticePullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                mAdapter = null;
                requestMessageGeneral(String.valueOf(mCurrentPage));
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        footer = getLayoutInflater().inflate(R.layout.listview_footer_w_manage, null);
        loadMoreBtn = (Button) footer.findViewById(R.id.bt_load_more);
        loading = footer.findViewById(R.id.ll_loading);
        loadMoreBtn.setOnClickListener(this);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_CODE_CHECK_NOTICE_DETAIL:
                        String messageId = msg.getData().getString(MESSAGE_ID);
                        messagePosition = msg.getData().getInt(MESSAGE_POSITION);
                        Intent intent = new Intent();
                        intent.setClass(NoticeActivity.this, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailActivity.MESSAGE_ID, messageId);
                        startActivityForResult(intent, REQUEST_CODE_CHECK_NOTICE_DETAIL);
                        break;
                }
            }
        };
    }

    /**
     * 请求消息列表
     */
    private void requestMessageGeneral(String currentPage) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(UserConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(NoticeActivity.this));
        params.addBodyParameter(UserConstants.IMEI, SpCoookieUtils.getImei(NoticeActivity.this));
        params.addBodyParameter(SettingsConstants.PAGE_SIZE, "15");
        params.addBodyParameter(SettingsConstants.CURRENT_PAGE, currentPage);

        MessageGeneralProcessor mProcessor = new MessageGeneralProcessor(NoticeActivity.this);
        mProcessor.setParams(params);
/*        if (Integer.parseInt(currentPage) == 1) {
            mProcessor.setDialogEnabled(true);
        } else {
            mProcessor.setDialogEnabled(false);
        }
        mProcessor.setDialogMessage(getString(R.string.loading_notice_list));*/
        mProcessor.sendPostRequest();
    }

    public void onEvent(MessageGeneralEvent event) {
        JsonMessageGeneral jsonMessageGeneral = event.jsonMessageGeneral;
        noticePullListView.onPullDownRefreshComplete();
        // 将footerView重置
        setFooterViewNotLoading();
        List<MessageList> messageLists = jsonMessageGeneral.getMessageList();
        if (mAdapter == null) {
            mAdapter = new MessageGeneralListAdapter(NoticeActivity.this);
            mAdapter.appendMessageLists(messageLists);
            noticeListView.removeFooterView(footer);
            if (mCurrentPage < Integer.parseInt(jsonMessageGeneral.getTotalPageNum())) {
                noticeListView.addFooterView(footer);
            } else {

            }
//            if (!(mAnimAdapter instanceof AlphaInAnimationAdapter)) {
            mAnimAdapter = new SwingRightInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(noticeListView);
            noticeListView.setAdapter(mAnimAdapter);
//            } else {
//                noticeListView.setAdapter(mAnimAdapter);
//            }
        } else {
            mAdapter.appendMessageLists(messageLists);
            if (mCurrentPage >= Integer.parseInt(jsonMessageGeneral.getTotalPageNum())) {
                noticeListView.removeFooterView(footer);
                T.showShort(NoticeActivity.this, getString(R.string.no_more_data));
            } else {

            }
            mAdapter.notifyDataSetChanged();
        }

        mCurrentPage += 1;

    }

    public void onEvent(RequestFailEvent event) {
        T.showShort(NoticeActivity.this, event.message);
    }

    /**
     * 设置listview的footer状态为loading
     */
    private void setFooterViewLoading() {
        loadMoreBtn.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    /**
     * 设置listview的footer状态为非loading
     */
    private void setFooterViewNotLoading() {
        loadMoreBtn.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

}
