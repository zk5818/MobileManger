package com.kery.mobilemanager.activity;

import android.app.DatePickerDialog;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.base.HanBaseActivity;
import com.kery.mobilemanager.listener.SimpleWatacher;
import com.kery.mobilemanager.utils.HintDialog;
import com.kery.mobilemanager.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

public class OutSaleActivity extends HanBaseActivity {
    @Override
    public void initTitleBar() {
        getIntent().putExtra(KEY_RIGHT_ICON, R.drawable.infomation);
        super.initTitleBar();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
        params.gravity = Gravity.CENTER_VERTICAL;
        rightImg.setLayoutParams(params);
    }

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
    public void response_success(int message_type, String s) {
        LogUtil.log(s);
        switch (message_type) {
            case 1:
                if (s.contains("success")) {
                    show("录入成功");
                    finish();
                } else {
                    show("录入失败");
                    finish();
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_out_sale;
    }

    EditText etName, etInPrice, etOutPrice, etOtherIn, etRemarks;
    TextView tvDataTime, tvTotalMoney, tvTotalIn, tvETLength;


    @Override
    public void initView() {
        HanBaseActivity.setRed(getWindow().getDecorView(), R.id.h3, R.id.h4, R.id.h5, R.id.h7);

        etName = (EditText) findViewById(R.id.etName);
        etInPrice = (EditText) findViewById(R.id.etInPrice);
        etOutPrice = (EditText) findViewById(R.id.etOutPrice);
        etOtherIn = (EditText) findViewById(R.id.etOtherIn);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        tvDataTime = (TextView) findViewById(R.id.tvDataTime);
        tvTotalMoney = (TextView) findViewById(R.id.tvTotalMoney);
        tvTotalIn = (TextView) findViewById(R.id.tvTotalIn);
        tvETLength = (TextView) findViewById(R.id.tvETLength);
        dialog = new DatePickerDialog(this, c, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        tvDataTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        calendar.setTime(new Date());
    }

    @Override
    public ViewGroup getOverLayView() {
        return (LinearLayout) findViewById(R.id.llContainer);
    }

    DatePickerDialog dialog;

    @Override
    public void initListener() {
        etInPrice.addTextChangedListener(simpleWatacher);
        etOutPrice.addTextChangedListener(simpleWatacher);
        etOtherIn.addTextChangedListener(simpleWatacher);
        tvDataTime.setOnClickListener(l);
        findViewById(R.id.doComfirm).setOnClickListener(l);
        etRemarks.addTextChangedListener(new SimpleWatacher() {
            @Override
            public void afterTextChanged(Editable s) {
                tvETLength.setText(s.length() + "/100");
            }
        });


    }

    AlertDialog unbind;
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvDataTime:
                    dialog.show();
                    break;
                case R.id.doComfirm:
                    if (unbind == null)
                        unbind = HintDialog.creatDialog(R.layout.dialog_alert_2, OutSaleActivity.this, "提示",
                                "确定录入该笔销售单？\n销售信息：\n名称:" + etName.getText().toString()
                                        + "\n出售价：" + etOutPrice.getText().toString() + "\n一经录入将无法修改，请仔细核对", this);
                    if (unbind.isShowing())
                        return;
                    unbind.show();
                    break;
                case R.id.ok:
                    if (etName.getText().toString().contains("#") || etRemarks.getText().toString().contains("#")) {
                        show("不能输入“#”字符");
                        return;
                    }
                    if (etName.getText().toString().equals("")) {
                        show(etName.getHint().toString());
                    }
                    if (etInPrice.getText().toString().equals("")) {
                        show(etInPrice.getHint().toString());
                    }
                    if (etOutPrice.getText().toString().equals("")) {
                        show(etOutPrice.getHint().toString());
                    }
                    if (tvDataTime.getText().toString().equals("")) {
                        show(tvDataTime.getHint().toString());
                    }
                    LinkedHashMap<String, String> h = new LinkedHashMap<>();
                    h.put("goods_name", etName.getText().toString());
                    h.put("inPrice", etInPrice.getText().toString());
                    h.put("outPrice", etOutPrice.getText().toString());
                    h.put("otherPrice", etOtherIn.getText().toString());
                    h.put("comments", etRemarks.getText().toString());
                    h.put("add_time", tvDataTime.getText().toString());
                    h.put("total_in", tvTotalIn.getText().toString().replace("￥", ""));
                    h.put("totalMoney", tvTotalMoney.getText().toString().replace("￥", ""));
                    post(1, null, h, "goodsAdd", true);
                    unbind.dismiss();
                    break;
                case R.id.cancel:
                    unbind.dismiss();
                    break;
            }
        }
    };
    double d1, d2, d3;
    SimpleWatacher simpleWatacher = new SimpleWatacher() {
        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (etInPrice.getText().toString().equals("")) {
                    d1 = 0;
                } else {
                    d1 = Double.parseDouble(etInPrice.getText().toString());
                }
                if (etOutPrice.getText().toString().equals("")) {
                    d2 = 0;
                } else {
                    d2 = Double.parseDouble(etOutPrice.getText().toString());
                }
                if (etOtherIn.getText().toString().equals("")) {
                    d3 = 0;
                } else {
                    d3 = Double.parseDouble(etOtherIn.getText().toString());
                }
                tvTotalMoney.setText("￥" + String.valueOf(d2 + d3));
                tvTotalIn.setText("￥" + String.valueOf(d2 - d1 + d3));
            } catch (Exception e) {
                show("价格格式错误！");
            }
        }
    };

    @Override
    public void initData() {
//        LinkedHashMap<String, String> h = new LinkedHashMap<>();
//        h.put("user_id", "22");
//        h.put("user_password", "22");
//        post(0, null, h, "CheckLogin", true);
    }


    @Override
    public void onRightImageClick(ImageView imageView) {
        super.onRightImageClick(imageView);
        Snackbar.make(imageView, "此销售单为一次性单，可随意填写盈利情况", Snackbar.LENGTH_LONG).show();
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener c = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = new StringBuilder().append(year).append("-").append
                    ((month + 1) < 10 ? "0" + (month + 1) : (month + 1) + "").append("-")
                    .append((day < 10) ? "0" + day : day + "").toString();
            tvDataTime.setText(date);

        }
    };

}
