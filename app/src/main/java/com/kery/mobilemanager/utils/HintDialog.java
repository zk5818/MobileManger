package com.kery.mobilemanager.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kery.mobilemanager.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/3/8.,Dialog工具类
 */

public class HintDialog {


    /**
     * 去找dialog里的一个textview控件，设置值
     */
    public static void setText(CharSequence text, @IdRes int viewId, @NonNull AlertDialog dialog) {
        if ( text == null || text.length() == 0)
            return;
        TextView textView = (TextView) dialog.findViewById(viewId);
        if (textView != null) {
            textView.setText(text);
            textView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }


    public static final int dialog1 = R.layout.dialog_alert_1;
    public static final int dialog2 = R.layout.dialog_alert_2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {dialog1, dialog2})
    public @interface DialogRes {}

    public static AlertDialog creatDialog(@LayoutRes int layoutRes, final Activity activity, CharSequence title,
                                          CharSequence message, View.OnClickListener listener) {
        View contentView = View.inflate(activity, layoutRes, null);
        TextView title_text = (TextView) contentView.findViewById(R.id.title);
        title_text.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        title_text.setText(title);
        TextView message_text = (TextView) contentView.findViewById(R.id.message);
        message_text.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        message_text.setText(message);
        TextView ok = (TextView) contentView.findViewById(R.id.ok);
        if (ok != null) ok.setOnClickListener(listener);
        TextView cancel = (TextView) contentView.findViewById(R.id.cancel);
        if (cancel != null) cancel.setOnClickListener(listener);
        final AlertDialog dialog1 = new AlertDialog.Builder(activity)
                .setView(contentView).create();
        contentView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int w = v.getResources().getDisplayMetrics().widthPixels * 3 / 4;
                dialog1.getWindow().setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                v.removeOnAttachStateChangeListener(this);
            }
        });
        dialog1.getWindow().setWindowAnimations(R.style.window_bottom_in_out);
        return dialog1;
    }

//
//    public static <T> AlertDialog getListDialog(Activity activity, CharSequence title,
//                                                ArrayAdapter<T> adapter, AdapterView.OnItemClickListener listener) {
//        View contentView = activity.getLayoutInflater().inflate(R.layout.dialog_alert_3, null);
//        final TextView title_text = (TextView) contentView.findViewById(R.id.title);
//        if (title_text != null) title_text.setText(title);
//        final ListView listView = (ListView) contentView.findViewById(R.id.listView);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(listener);
//        final AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setView(contentView).create();
//        dialog.getWindow().setWindowAnimations(R.style.window_bottom_in_out);
//        final Runnable p = new Runnable() {
//            @Override
//            public void run() {
//                //自动判断大小，如果超过五个item,高度就是五个item;
//                if (listView.getAdapter() == null)
//                    return;
//                int w = listView.getResources().getDisplayMetrics().widthPixels * 3 / 4;
//                LogUtil.log(listView.getWidth(), listView.getHeight());
//                int h = title_text.getHeight() + (listView.getResources().getDimensionPixelOffset(R.dimen.item_height)
//                        * 5 + 38);
//                h = listView.getAdapter().getCount() <= 5 ? ViewGroup.LayoutParams.WRAP_CONTENT : h;
//                dialog.getWindow().setLayout(w, h);
//            }
//        };
//        View.OnAttachStateChangeListener changeListener = new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                v.post(p);
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//                v.removeCallbacks(p);
//            }
//        };
//        listView.addOnAttachStateChangeListener(changeListener);
//        return dialog;
//    }

}
