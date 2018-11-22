package com.kery.mobilemanager.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.adapter.RecyclerViewAdapter;
import com.kery.mobilemanager.adapter.RecyclerViewHolder;
import com.kery.mobilemanager.base.HanBaseActivity;
import com.kery.mobilemanager.listener.OnDisListener;
import com.kery.mobilemanager.utils.CalendarUtil;
import com.kery.mobilemanager.utils.LogUtil;
import com.kery.mobilemanager.utils.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaleActivity extends HanBaseActivity {

    @Override
    public void message(Message message) {

    }

    @Override
    public void dofirstRequest() {
        getNewData();
    }

    @Override
    public void onViewVisable() {
        super.onViewVisable();

    }

    @Override
    public void response_error(int message_type, String error_text) {
        LogUtil.log(error_text);
        refresh.setRefreshing(false);
    }

    @Override
    public void response_no_success(int message_type, int status_code, String error_text) {
        refresh.setRefreshing(false);
        super.response_no_success(message_type, status_code, error_text);
    }

    List<Map<String, String>> mList = new ArrayList<>();

    @Override
    public void response_success(int message_type, String s) {
        refresh.setRefreshing(false);
//        s = "ALLMONEY◎22⊙TOTALINCOME◎21⊙┦#GOODS_NAME◎12⊙INPRICE◎12⊙OUTPRICE◎12⊙OTHERPRICE◎21⊙COMMENTS◎3343⊙TOTAL_IN◎82⊙ADD_TIME◎2012-12-12⊙TOTALMONEY◎45⊙┦";
        LogUtil.log(s);
        try {
            String[] strings = s.split("#");
            if (strings.length < 2) {
                show("该段日期无数据");
                tvExpends.setText("0.0");
                tvIncome.setText("0.0");
                mList.clear();
                mAdapter.notifyDataSetChanged();
                showDataEmpty();
                return;
            }
            String[] strings1 = strings[0].split("⊙");
            String all_money = strings1[0].split("◎")[1];
            String total_in = strings1[1].split("◎")[1];
            tvExpends.setText("￥" + all_money);
            tvIncome.setText("￥" + total_in);
            LogUtil.log(all_money, total_in);
            List<Map<String, String>> mList1 = StringUtil.strToList(strings[1]);
            mList.clear();
            mList.addAll(mList1);
            mAdapter.notifyDataSetChanged();
            LogUtil.log(mList);

        } catch (Exception e) {

        }


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sale;
    }

    TextView select_time, tvIncome, tvExpends;
    RecyclerView rvItems;
    SwipeRefreshLayout refresh;

    @Override
    public void initView() {
        select_time = (TextView) findViewById(R.id.select_time);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);

        tvExpends = (TextView) findViewById(R.id.tvExpends);
        tvIncome = (TextView) findViewById(R.id.tvIncome);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

    }

    @Override
    public ViewGroup getOverLayView() {
        return rvItems;
    }

    @Override
    public int getDataEmptyDrawable() {
        return R.drawable.icon_no_message;
    }

    @Override
    public void initListener() {
        select_time.setOnClickListener(l);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });
    }

    void getNewData() {
        LinkedHashMap<String, String> h = new LinkedHashMap<>();
        h.put("startTime", sTime);
        h.put("endTime", eTime);
        post(0, null, h, "getTotal", true);
    }

    private BottomSheetDialog bottomSheetDialog;
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.select_time:
                    if (bottomSheetDialog == null) {
                        bottomSheetDialog = new BottomSheetDialog(v.getContext());
                        bottomSheetDialog.setOnDismissListener(new OnDisListener());
                        bottomSheetDialog.setContentView(R.layout.dialog_xuanze_zunin_riqi_fd);
                        bottomSheetDialog.findViewById(R.id.ok).setOnClickListener(this);
                        setMargin();
                    }
                    bottomSheetDialog.show();
                    break;
                case R.id.ok:
                    DatePicker startPicker = (DatePicker) bottomSheetDialog.findViewById(R.id.picker_start_time);
                    DatePicker endPicker = (DatePicker) bottomSheetDialog.findViewById(R.id.picker_end_time);
                    String startText = CalendarUtil.getdate(startPicker.getYear(), startPicker.getMonth(), startPicker.getDayOfMonth());
                    String endText = CalendarUtil.getdate(endPicker.getYear(), endPicker.getMonth(), endPicker.getDayOfMonth());
                    int compare = CalendarUtil.compareInt(endText, startText);
                    if (compare < 0) {
                        show("开始时间不能比结束时间早");
                        return;
                    }
                    LogUtil.log(startText.replace(".", "-"), endText.replace(".", "-"));
                    select_time.setText(startText.replace("-", ".") + "\n~\n" + endText.replace("-", "."));
                    sTime = startText.replace(".", "-");
                    eTime = endText.replace(".", "-");
                    getNewData();
//                    getHttpData(financeList.size(), "");//请求网络
                    bottomSheetDialog.dismiss();
                    break;
            }
        }
    };
    String sTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    String eTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private void setMargin() {
        DatePicker startPicker = (DatePicker) bottomSheetDialog.findViewById(R.id.picker_start_time);
        DatePicker endPicker = (DatePicker) bottomSheetDialog.findViewById(R.id.picker_end_time);
        List<DatePicker> datePickers = new ArrayList<>();
        datePickers.add(startPicker);
        datePickers.add(endPicker);
        for (DatePicker picker : datePickers) {
            LinearLayout layout1 = (LinearLayout) picker.getChildAt(0);
            LinearLayout layout11 = (LinearLayout) layout1.getChildAt(0);
            for (int i = 0; i < layout11.getChildCount(); i++) {
                if (!(layout11.getChildAt(i) instanceof NumberPicker))
                    continue;
                NumberPicker numberPicker = (NumberPicker) layout11.getChildAt(i);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) numberPicker.getLayoutParams();
                layoutParams.width = 0;
                layoutParams.weight = 1;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                numberPicker.setLayoutParams(layoutParams);
                modifierNumberPicker(numberPicker, ContextCompat.getColor(SaleActivity.this, R.color.c_CCCCCC)
                        , getResources().getDimensionPixelOffset(R.dimen.dp0_5));
            }
        }
    }

    public static void modifierNumberPicker(NumberPicker numberPicker, @ColorInt int dividercolor, int dividerHeight) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值 透明
                    pf.set(numberPicker, new ColorDrawable(dividercolor));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        // 分割线高度
        for (Field pf2 : pickerFields) {
            if (pf2.getName().equals("mSelectionDividerHeight")) {
                pf2.setAccessible(true);
                try {
                    pf2.set(numberPicker, dividerHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    RecyclerViewAdapter mAdapter;

    @Override
    public void initData() {
        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(mAdapter = new RecyclerViewAdapter<Map<String, String>>(this, R.layout.item_gate_wifi, mList) {
            @Override
            public void convert(RecyclerViewHolder holder, final Map<String, String> waterInfo, int position) {
                // #GOODS_NAME◎12⊙INPRICE◎12⊙OUTPRICE◎12⊙OTHERPRICE◎21⊙COMMENTS◎3343⊙TOTAL_IN◎⊙ADD_TIME◎2012-12-12⊙TOTALMONEY◎⊙┦";
//                LogUtil.log(waterInfo);
                holder.setText(R.id.sale_name, "出售信息：" + waterInfo.get("GOODS_NAME"));
                holder.setText(R.id.inCome, "￥" + waterInfo.get("TOTAL_IN"));
                holder.setText(R.id.out_date, "出售日期：" + waterInfo.get("ADD_TIME"));
                holder.setOnClickListener(R.id.llItem_gate, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(v.getContext(), SaleDetailActivity.class)
                                .putExtra(KEY_OBJ_2, (Serializable) waterInfo)
                                .putExtra(KEY_RIGHT_TEXT, "删除")
                                .putExtra(KEY_TITLE, "销售详情"));
                    }
                });
            }
        });
    }

    @Override
    public void onRightImageClick(ImageView imageView) {
        super.onRightImageClick(imageView);
        startActivity(new Intent(imageView.getContext(), OutSaleActivity.class)
                .putExtra(KEY_TITLE, "自定义销售"));
    }
}
