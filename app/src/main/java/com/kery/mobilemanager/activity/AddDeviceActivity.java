package com.kery.mobilemanager.activity;

import android.os.Message;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.base.HanBaseActivity;

public class AddDeviceActivity extends HanBaseActivity {

    @Override
    public void message(Message message) {

    }

    @Override
    public void dofirstRequest() {

    }

    @Override
    public void response_error(int message_type, String error_text) {

    }

    @Override
    public void response_success(int message_type,String s) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    public void initView() {
        setRed(getWindow().getDecorView(), R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5, R.id.h6, R.id.h7);
    }

    @Override
    public ViewGroup getOverLayView() {
        return (LinearLayout) findViewById(R.id.llContainer);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }
}
