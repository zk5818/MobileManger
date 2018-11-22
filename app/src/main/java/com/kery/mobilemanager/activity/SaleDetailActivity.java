package com.kery.mobilemanager.activity;

import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.base.HanBaseActivity;
import com.kery.mobilemanager.utils.HintDialog;

import java.util.LinkedHashMap;
import java.util.Map;

public class SaleDetailActivity extends HanBaseActivity {

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
    public void response_no_success(int message_type, int status_code, String error_text) {
        show("删除失败");
        super.response_no_success(message_type, status_code, error_text);
    }

    @Override
    public void response_success(int message_type, String s) {
        switch (message_type) {
            case 1:
                if (s.contains("success")) {
                    show("删除成功");
                    finish();
                } else {
                    show("删除失败");
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sale_detail;
    }

    TextView tvDataTime, tvTotalMoney, tvTotalIn, etName, etInPrice, etOutPrice, etOtherIn, etRemarks;

    @Override
    public void initView() {
        etName = (TextView) findViewById(R.id.etName);
        etInPrice = (TextView) findViewById(R.id.etInPrice);
        etOutPrice = (TextView) findViewById(R.id.etOutPrice);
        etOtherIn = (TextView) findViewById(R.id.etOtherIn);
        etRemarks = (TextView) findViewById(R.id.etRemarks);
        tvDataTime = (TextView) findViewById(R.id.tvDataTime);
        tvTotalMoney = (TextView) findViewById(R.id.tvTotalMoney);
        tvTotalIn = (TextView) findViewById(R.id.tvTotalIn);
    }

    @Override
    public ViewGroup getOverLayView() {
        return (LinearLayout) findViewById(R.id.llContainer);
    }

    @Override
    public void initListener() {

    }

    String sale_id;

    @Override
    public void initData() {
        Map<String, String> stringMap = (Map<String, String>) getIntent().getSerializableExtra(KEY_OBJ_2);
        etName.setText(stringMap.get("GOODS_NAME"));
        etInPrice.setText(stringMap.get("INPRICE"));
        etOutPrice.setText(stringMap.get("OUTPRICE"));
        etOtherIn.setText(stringMap.get("OTHERPRICE"));
        etRemarks.setText(stringMap.get("COMMENTS"));
        tvDataTime.setText(stringMap.get("ADD_TIME"));
        tvTotalMoney.setText("￥" + stringMap.get("TOTALMONEY"));
        tvTotalIn.setText("￥" + stringMap.get("TOTAL_IN"));

        sale_id = stringMap.get("SALE_ID");
    }

    AlertDialog unbind;
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ok:
                    if (sale_id == null)
                        return;
                    LinkedHashMap<String, String> h = new LinkedHashMap<>();
                    h.put("sale_id", sale_id);
                    post(1, null, h, "goodsDelete", true);
                    break;
                case R.id.cancel:
                    unbind.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onRightClick(TextView view) {
        super.onRightClick(view);
        if (unbind == null)
            unbind = HintDialog.creatDialog(R.layout.dialog_alert_2, SaleDetailActivity.this, "提示",
                    "确定删除该笔销售单？", l);
        if (unbind.isShowing())
            return;
        unbind.show();
    }
}
