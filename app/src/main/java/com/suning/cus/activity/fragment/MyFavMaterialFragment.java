package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.R;
import com.suning.cus.adapter.MyFavMaterialListAdapter;
import com.suning.cus.bean.BackupFavData;
import com.suning.cus.bean.QueryMaterPriceData;
import com.suning.cus.bean.ShopCarBackupData;
import com.suning.cus.constants.BackupConstants;
import com.suning.cus.constants.ManageConstants;
import com.suning.cus.event.DelFavEvent;
import com.suning.cus.event.GetFavEvent;
import com.suning.cus.event.QueryPriceEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.extras.pulltorefresh.PullToRefreshBase;
import com.suning.cus.extras.pulltorefresh.PullToRefreshListView;
import com.suning.cus.json.JsonBackupFavorite;
import com.suning.cus.logical.DelFavProcessor;
import com.suning.cus.logical.GetFavProcessor;
import com.suning.cus.logical.QueryPriceProcessor;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import java.util.List;

public class MyFavMaterialFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * 15010551
     */
    public static final int HANDLER_CODE_LONG_CLICK = 0x0001;
    public static final int HANDLER_CODE_ADD_FAV = 0x0002;
    public static final int HANDLER_CODE_DEL_FAV = 0x0003;
    public static final int HANDLER_CODE_QUERY_PRICE = 0x0004;

    public static final String CMMDTY_CODE = "cmmdtyCode";
    public static final String CMMDTY_NAME = "cmmdtyName";
    public static final String CMMDTY_POSITION = "position";
    public static final String CMMDTY_PRICE = "cmmdtyPrice";

    private PullToRefreshListView myFavBackupListView;
    private ListView mListView;


    private MyFavMaterialListAdapter mAdapter = null;

    private int CURRENT_PAGE = 1;

    Button loadMoreBtn;
    View loading;
    private View footer;

    private String cmmdtyCode;
    private String cmmdtyName;
    private String cmmdtyPrice;

    public static Handler handler;

    public boolean isLoadMore = false;

    public List<ShopCarBackupData> dataList;

    public MyFavMaterialFragment() {
        // Required empty public constructor
    }

    public void setData(List<ShopCarBackupData> data) {
        this.dataList = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_fav_backup, container, false);
        myFavBackupListView = (PullToRefreshListView) view.findViewById(R.id.lv_my_fav_backup);
        mListView = myFavBackupListView.getRefreshableView();

        /**
         * 设置下来刷新事件
         */
        myFavBackupListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mAdapter = null;
                CURRENT_PAGE = 1;
                isLoadMore = false;
                getFav(CURRENT_PAGE + "");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        LinearLayout myFavOpsLinearLayout = (LinearLayout) view.findViewById(R.id.ll_my_fav_ops);

        myFavOpsLinearLayout.setVisibility(View.GONE);
        //ListFooter
        footer = inflater.inflate(R.layout.listview_footer_w_manage, null);
        loadMoreBtn = (Button) footer.findViewById(R.id.bt_load_more);
        loading = footer.findViewById(R.id.ll_loading);
        loadMoreBtn.setOnClickListener(this);
        CURRENT_PAGE = 1;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle;
                switch (msg.what) {
                    case HANDLER_CODE_LONG_CLICK:
                        //myFavOpsLinearLayout.setVisibility(View.VISIBLE);
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case HANDLER_CODE_ADD_FAV:
                        bundle = msg.getData();
                        cmmdtyCode = bundle.getString(MyFavMaterialFragment.CMMDTY_CODE);
                        cmmdtyName = bundle.getString(MyFavMaterialFragment.CMMDTY_NAME);

                        if (dataList.size() >= 10) {
                            T.showShort(getActivity(), "购物车数量不能超过10");
                            return;
                        }

                        if (!isMaterialExist(cmmdtyCode)) {
                            queryPrice(cmmdtyCode, cmmdtyName);
                        } else {
                            T.showShort(getActivity(), "购物车中已存在该商品");
                        }

                        break;
                    case HANDLER_CODE_DEL_FAV:
                        int position = msg.getData().getInt(MyFavMaterialFragment.CMMDTY_POSITION);
                        String materCode = msg.getData().getString(MyFavMaterialFragment.CMMDTY_CODE);
                        delFav(materCode, position);
                        break;
                    case HANDLER_CODE_QUERY_PRICE:
                        bundle = msg.getData();
                        cmmdtyCode = bundle.getString(MyFavMaterialFragment.CMMDTY_CODE);
                        cmmdtyName = bundle.getString(MyFavMaterialFragment.CMMDTY_NAME);
                        cmmdtyPrice = bundle.getString(MyFavMaterialFragment.CMMDTY_PRICE);
                        ShopCarBackupData data = new ShopCarBackupData();
                        data.setCmmdtyCode(cmmdtyCode);
                        data.setCmmdtyName(cmmdtyName);
                        data.setCmmdtyPrice(cmmdtyPrice);
                        data.setCmmdtyNum("1");
                        dataList.add(data);
                        T.showShort(getActivity(), "加入购物车成功");
                        break;
                }
            }
        };

        /**
         * 第一次进入页面刷新
         */
        myFavBackupListView.doPullRefreshing(true, 500);
        return view;
    }


    /**
     * 检查配件是否已经申请过了
     *
     * @param code 配件编码
     * @return true-已存在  false-不存在
     */
    private boolean isMaterialExist(String code) {

        if (dataList != null && !TextUtils.isEmpty(code)) {
            for (ShopCarBackupData material : dataList) {
                if (code.equals(material.getCmmdtyCode())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 获取收藏配件
     *
     * @param currentPage 当前页
     */
    private void getFav(String currentPage) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(BackupConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(BackupConstants.PAGE_SIZE, "10");
        params.addBodyParameter(BackupConstants.CURRENT_PAGE, currentPage);

        GetFavProcessor processor = new GetFavProcessor(getActivity(), params);
        processor.sendPostRequest();

    }

    /**
     * 获取收藏配件成功后回调
     *
     * @param event 收藏配件Event
     */
    public void onEvent(GetFavEvent event) {
        JsonBackupFavorite jsonBackupFavorite = event.jsonBackupFavorite;
        myFavBackupListView.onPullDownRefreshComplete();
        List<BackupFavData> backupFavDataList = jsonBackupFavorite.getData();
        // 将footerView重置
        setFooterViewNotLoading();
        if (mAdapter == null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            mAdapter = new MyFavMaterialListAdapter(getActivity(), width);
            mAdapter.appendDataList(backupFavDataList);
            mListView.removeFooterView(footer);
            if (CURRENT_PAGE < Integer.parseInt(jsonBackupFavorite.getTotalPageNum())) {
                mListView.addFooterView(footer);
            } else {
                //    T.showShort(getActivity(), getString(R.string.no_more_data));
            }
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.appendDataList(backupFavDataList);
            if (CURRENT_PAGE >= Integer.parseInt(jsonBackupFavorite.getTotalPageNum())) {
                mListView.removeFooterView(footer);
                T.showShort(getActivity(), getString(R.string.no_more_data));
            } else {
                //    mListView.addFooterView(footer);
            }
            mAdapter.notifyDataSetChanged();
        }

        // 获取成功CURRENT_PAGE才加1
        CURRENT_PAGE += 1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setFooterViewLoading() {
        loadMoreBtn.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void setFooterViewNotLoading() {
        loadMoreBtn.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_load_more:
                setFooterViewLoading();
                isLoadMore = true;
                getFav(CURRENT_PAGE + "");
                break;
        }
    }

    /**
     * 删除收藏
     * @param materialCode 材配编码
     * @param position 在数组中的位置
     */
    private void delFav(String materialCode, final int position) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(BackupConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(BackupConstants.MATERIAL_CODE, materialCode);

        /*发送请求*/
        DelFavProcessor mProcessor = new DelFavProcessor(getActivity(), params, position);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_del_fav));
        mProcessor.sendPostRequest();
    }

    /**
     * 删除收藏成功回调
     *
     * @param event 删除收藏的Event
     */
    public void onEvent(DelFavEvent event) {
        if (event.isSuccess) {
            int position = event.position;
            if (mAdapter != null) {
                mAdapter.getDataList().remove(position);
                mAdapter.notifyDataSetChanged();
            }
            T.showShort(getActivity(), getString(R.string.del_fav_success));
        } else {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 查询价格
     * @param mantrStr 材配编码
     * @param cmmdtyName  材配名字
     */
    private void queryPrice(final String mantrStr, final String cmmdtyName) {

        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(ManageConstants.MATER_CODES, mantrStr);

        QueryPriceProcessor mProcessor = new QueryPriceProcessor(getActivity(), params, QueryPriceEvent.QUERY_PRICE_TYPE.MY_FAV_BACKUP);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_price));
        mProcessor.sendPostRequest();
    }

    /**
     * 查询价格成功后回调
     *
     * @param event 查询价格Event
     */
    public void onEvent(QueryPriceEvent event) {
        if (event.type == QueryPriceEvent.QUERY_PRICE_TYPE.MY_FAV_BACKUP) {
            String materPrice = "0.0";
            if (event.isSuccess) {
                List<QueryMaterPriceData> priceDataList = event.priceDataList;
                if (priceDataList != null) {
                    materPrice = priceDataList.get(0).getMaterPrice();
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString(MyFavMaterialFragment.CMMDTY_NAME, cmmdtyName);
            bundle.putString(MyFavMaterialFragment.CMMDTY_CODE, cmmdtyCode);
            bundle.putString(MyFavMaterialFragment.CMMDTY_PRICE, materPrice);
            Message msg = new Message();
            msg.what = HANDLER_CODE_QUERY_PRICE;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    /**
     * 请求失败的回调
     * @param event 请求失败Event
     */
    public void onEvent(RequestFailEvent event) {
        myFavBackupListView.onPullDownRefreshComplete();
        if (isLoadMore == true) {
            setFooterViewNotLoading();
        }
        T.showShort(getActivity(), event.message);
    }
}
