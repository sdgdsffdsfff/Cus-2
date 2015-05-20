package com.suning.cus.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.suning.cus.R;
import com.suning.cus.adapter.BackupMyFavListAdapter;
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

public class MyFavBackupFragment extends Fragment implements View.OnClickListener {

    /**
     * 15010551
     */

    // Handler Msg Code: 将收藏的物品添加到购物车
    public static final int HANDLER_CODE_ADD_FAV = 0x0001;

    // Handler Msg Code: 从收藏列表中删除物品
    public static final int HANDLER_CODE_DEL_FAV = 0x0002;

    // Handler Msg Code: 获取到物品的价格
    public static final int HANDLER_CODE_QUERY_PRICE = 0x0003;

    // Handler Bundler Key: 商品编码
    public static final String CMMDTY_CODE = "cmmdtyCode";

    // Handler Bundler Key: 商品名称
    public static final String CMMDTY_NAME = "cmmdtyName";

    // Handler Bundler Key: 商品在listView中的位置
    public static final String CMMDTY_POSITION = "position";

    // Handler Bundler Key: 商品价格
    public static final String CMMDTY_PRICE = "cmmdtyPrice";

    /**
     * 业务相关
     */
    private BackupMyFavListAdapter mAdapter = null;

    private AnimationAdapter mAnimAdapter;

    private int mCurrentPage = 1;

    private String cmmdtyCode;

    private String cmmdtyName;

    private String cmmdtyPrice;

    public static Handler handler;

    public boolean isLoadMore = false;

    /**
     * UI相关
     */
    private PullToRefreshListView myFavBackupListView;

    private ListView mListView;

    private LinearLayout myFavOpsLinearLayout;

    private Button loadMoreBtn;

    private View loading;

    private View footer;

    public MyFavBackupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View favBackup = inflater.inflate(R.layout.fragment_my_fav_backup, container, false);
        //ListFooter
        footer = inflater.inflate(R.layout.listview_footer_w_manage, null);
        mCurrentPage = 1;

        initView(favBackup, footer);
        initHandler();

        /**
         * 第一次进入页面刷新
         */
        myFavBackupListView.doPullRefreshing(true, 500);
        return favBackup;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_load_more:
                setFooterViewLoading();
                isLoadMore = true;
                requestGetFav(String.valueOf(mCurrentPage));
                break;
        }
    }

    private void initView(View favBackup, View footer) {

        myFavBackupListView = (PullToRefreshListView) favBackup.findViewById(R.id.lv_my_fav_backup);
        mListView = myFavBackupListView.getRefreshableView();

        /**
         * 设置下来刷新事件
         */
        myFavBackupListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mAdapter = null;
                mCurrentPage = 1;
                isLoadMore = false;
                requestGetFav(String.valueOf(mCurrentPage));
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

            }
        });
        myFavOpsLinearLayout = (LinearLayout) favBackup.findViewById(R.id.ll_my_fav_ops);
        myFavOpsLinearLayout.setVisibility(View.GONE);

        loadMoreBtn = (Button) footer.findViewById(R.id.bt_load_more);
        loading = footer.findViewById(R.id.ll_loading);
        loadMoreBtn.setOnClickListener(this);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle;
                DbUtils dbUtils;
                switch (msg.what) {
                    case HANDLER_CODE_ADD_FAV:
                        bundle = msg.getData();
                        cmmdtyCode = bundle.getString(MyFavBackupFragment.CMMDTY_CODE);
                        cmmdtyName = bundle.getString(MyFavBackupFragment.CMMDTY_NAME);
                        dbUtils = DbUtils.create(getActivity());
                        try {
                            //    List<ShopCarBackupData> dataList = dbUtils.findAll(Selector.from(ShopCarBackupData.class));
                            List<ShopCarBackupData> dataList = dbUtils.findAll(Selector.from(ShopCarBackupData.class).where("cmmdtyCode", "=", cmmdtyCode));
                            //    T.showShort(getActivity(), "数量:" + dataList.size());
                            if (dataList.size() == 0) {
                                List<ShopCarBackupData> allShopCarData = dbUtils.findAll(ShopCarBackupData.class);
                                if (allShopCarData.size() >= 10) {
                                    T.showShort(getActivity(), getString(R.string.shopcar_is_full));
                                } else {
                                    requestQueryPrice(cmmdtyCode);
                                }
                            } else {
                                T.showShort(getActivity(), getString(R.string.add_shopcar_exist));
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                            T.showShort(getActivity(), getString(R.string.add_shopcar_fail));
                        }
                        break;
                    case HANDLER_CODE_DEL_FAV:
                        int position = msg.getData().getInt(MyFavBackupFragment.CMMDTY_POSITION);
                        String materCode = msg.getData().getString(MyFavBackupFragment.CMMDTY_CODE);
                        requestDelFav(materCode, position);
                        break;
                    case HANDLER_CODE_QUERY_PRICE:
                        bundle = msg.getData();
                        cmmdtyCode = bundle.getString(MyFavBackupFragment.CMMDTY_CODE);
                        cmmdtyName = bundle.getString(MyFavBackupFragment.CMMDTY_NAME);
                        cmmdtyPrice = bundle.getString(MyFavBackupFragment.CMMDTY_PRICE);
                        ShopCarBackupData data = new ShopCarBackupData();
                        data.setCmmdtyCode(cmmdtyCode);
                        data.setCmmdtyName(cmmdtyName);
                        data.setCmmdtyPrice(cmmdtyPrice);
                        data.setCmmdtyNum("1");
                        dbUtils = DbUtils.create(getActivity());
                        try {
                            //    List<ShopCarBackupData> dataList = dbUtils.findAll(Selector.from(ShopCarBackupData.class));
                            List<ShopCarBackupData> dataList = dbUtils.findAll(Selector.from(ShopCarBackupData.class).where("cmmdtyCode", "=", cmmdtyCode));
                            //    T.showShort(getActivity(), "数量:" + dataList.size());
                            if (dataList.size() == 0) {
                                dbUtils.save(data);
                                T.showShort(getActivity(), getString(R.string.add_shopcar_success));
                            } else {
                                T.showShort(getActivity(), getString(R.string.add_shopcar_exist));
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                            T.showShort(getActivity(), getString(R.string.add_shopcar_fail));
                        }
                        break;
                }
            }
        };
    }

    /**
     * 获取我的收藏列表
     *
     * @param currentPage
     */
    private void requestGetFav(String currentPage) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(BackupConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(BackupConstants.PAGE_SIZE, "10");
        params.addBodyParameter(BackupConstants.CURRENT_PAGE, currentPage);

        GetFavProcessor mProcessor = new GetFavProcessor(getActivity(), params);
        mProcessor.sendPostRequest();
    }

    /**
     * 从我的收藏中删除
     *
     * @param materialCode
     * @param position
     */
    public void requestDelFav(String materialCode, final int position) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(BackupConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(BackupConstants.MATERIAL_CODE, materialCode);

        DelFavProcessor mProcessor = new DelFavProcessor(getActivity(), params, position);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_del_fav));
        mProcessor.sendPostRequest();
    }

    /**
     * 请求获取商品价格
     *
     * @param mantrStr
     */
    private void requestQueryPrice(final String mantrStr) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(ManageConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(ManageConstants.MATER_CODES, mantrStr);

        QueryPriceProcessor mProcessor = new QueryPriceProcessor(getActivity(), params, QueryPriceEvent.QUERY_PRICE_TYPE.MY_FAV_BACKUP);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_query_price));
        mProcessor.sendPostRequest();
    }

    /**
     * 接收获取收藏列表的Event
     *
     * @param event
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
            mAdapter = new BackupMyFavListAdapter(getActivity(), width);
            mAdapter.appendDataList(backupFavDataList);
            mListView.removeFooterView(footer);
            if (mCurrentPage < Integer.parseInt(jsonBackupFavorite.getTotalPageNum())) {
                mListView.addFooterView(footer);
            } else {
                //    T.showShort(getActivity(), getString(R.string.no_more_data));
            }
            mAnimAdapter = new SwingRightInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(mListView);
            mListView.setAdapter(mAnimAdapter);

            //    mListView.setAdapter(mAdapter);
        } else {
            mAdapter.appendDataList(backupFavDataList);
            if (mCurrentPage >= Integer.parseInt(jsonBackupFavorite.getTotalPageNum())) {
                mListView.removeFooterView(footer);
                T.showShort(getActivity(), getString(R.string.no_more_data));
            } else {
                //    mListView.addFooterView(footer);
            }
            mAdapter.notifyDataSetChanged();
        }

        // 获取成功CURRENT_PAGE才加1
        mCurrentPage += 1;
    }

    /**
     * 接收请求失败的Event
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        myFavBackupListView.onPullDownRefreshComplete();
        if (isLoadMore == true) {
            setFooterViewNotLoading();
        }
        T.showShort(getActivity(), event.message);
    }

    /**
     * 接收删除收藏的请求
     *
     * @param event
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
     * 接收获取商品价格的Event
     *
     * @param event
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
            bundle.putString(MyFavBackupFragment.CMMDTY_NAME, cmmdtyName);
            bundle.putString(MyFavBackupFragment.CMMDTY_CODE, cmmdtyCode);
            bundle.putString(MyFavBackupFragment.CMMDTY_PRICE, materPrice);
            Message msg = new Message();
            msg.what = HANDLER_CODE_QUERY_PRICE;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
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
