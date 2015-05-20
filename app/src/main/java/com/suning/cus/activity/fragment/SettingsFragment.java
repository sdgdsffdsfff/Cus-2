package com.suning.cus.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.suning.cus.R;
import com.suning.cus.activity.LoginActivity;
import com.suning.cus.activity.ModifyPasswordActivity;
import com.suning.cus.activity.NoticeActivity;
import com.suning.cus.constants.SettingsConstants;
import com.suning.cus.event.LogoutEvent;
import com.suning.cus.event.RequestFailEvent;
import com.suning.cus.event.SettingsEvent;
import com.suning.cus.logical.LogoutProcessor;
import com.suning.cus.utils.AppUtils;
import com.suning.cus.utils.SpCoookieUtils;
import com.suning.cus.utils.T;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.suning.cus.activity.fragment.SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * 15010551
     */

    /**
     * UI 相关
     */
    private TextView modifyPwdTextView, guideTextView, noticeTextView, switchUserTextView;

    private TextView exitTextView;

    private TextView currentVerTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(settingsView);
        return settingsView;
    }

    private void initView(View v) {
        modifyPwdTextView = (TextView) v.findViewById(R.id.tv_modify_password);
        modifyPwdTextView.setOnClickListener(this);
        guideTextView = (TextView) v.findViewById(R.id.tv_guide);
        guideTextView.setOnClickListener(this);
        noticeTextView = (TextView) v.findViewById(R.id.tv_notice);
        noticeTextView.setOnClickListener(this);
        switchUserTextView = (TextView) v.findViewById(R.id.tv_switch_user);
        switchUserTextView.setOnClickListener(this);
        exitTextView = (TextView) v.findViewById(R.id.tv_exit);
        exitTextView.setOnClickListener(this);
        currentVerTextView = (TextView) v.findViewById(R.id.tv_current_version);
        currentVerTextView.setText(AppUtils.getVersionName(getActivity()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        /*注册subscribers*/
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        /*把自己从subscribers中移除，不再接收*/
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modify_password:
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyPasswordActivity.class);
                intent.putExtra(ModifyPasswordActivity.MODIFY_PWD_TYPE, ModifyPasswordActivity.MODIFY_PWD);
                startActivity(intent);
                break;
            case R.id.tv_guide:
                break;
            case R.id.tv_notice:
                getIntentBy(NoticeActivity.class);
                break;
            case R.id.tv_switch_user:
                break;
            case R.id.tv_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.sure_to_exit));
                builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestUserLogout();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.show();
                break;
        }
    }

    private void getIntentBy(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    private void requestUserLogout() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(SettingsConstants.EMPLOYEE_ID, SpCoookieUtils.getEmployeeId(getActivity()));
        params.addBodyParameter(SettingsConstants.IMEI, SpCoookieUtils.getImei(getActivity()));
//        params.addBodyParameter(SettingsConstants.IMEI, "294892373548458");

        LogoutProcessor mProcessor = new LogoutProcessor(getActivity(), params);
        mProcessor.setDialogEnabled(true);
        mProcessor.setDialogMessage(getString(R.string.loading_logout));
        mProcessor.sendPostRequest();

    }

    public void onEvent(SettingsEvent event) {

    }

    public void onEvent(LogoutEvent event) {
        T.showShort(getActivity(), getString(R.string.logout_success));
        Intent intent = new Intent();
        intent.setClass(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * 接收
     *
     * @param event
     */
    public void onEvent(RequestFailEvent event) {
        T.showShort(getActivity(), event.message);
    }

}
