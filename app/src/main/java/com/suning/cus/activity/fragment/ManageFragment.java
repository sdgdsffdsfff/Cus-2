package com.suning.cus.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.suning.cus.R;
import com.suning.cus.activity.OrderManageActivity;
import com.suning.cus.activity.WManageActivity;
import com.suning.cus.event.ManageEvent;

public class ManageFragment extends Fragment implements View.OnClickListener {

    /**
     * 15010551
     */

    /**
     * UI相关
     */
    private LinearLayout mWManageLL;

    private LinearLayout mOrderManagerLL;

    public void onEvent(ManageEvent event) {

    }

    public ManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_manage, container, false);
        View content = inflater.inflate(R.layout.fragment_manage, container, false);
        mWManageLL = (LinearLayout) content.findViewById(R.id.ll_w_manage);
        mOrderManagerLL = (LinearLayout) content.findViewById(R.id.ll_order_manage);
        mWManageLL.setOnClickListener(this);
        mOrderManagerLL.setOnClickListener(this);
        return content;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_w_manage:
                intent.setClass(getActivity(), WManageActivity.class);
                intent.putExtra(WManageActivity.TYPE, WManageActivity.NORMAL);
                break;
            case R.id.ll_order_manage:
                intent.setClass(getActivity(), OrderManageActivity.class);
                intent.putExtra(OrderManageActivity.TYPE, OrderManageActivity.ORDER_MANAGE);
                break;
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
