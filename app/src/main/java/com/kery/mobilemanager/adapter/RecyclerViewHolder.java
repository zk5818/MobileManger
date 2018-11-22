package com.kery.mobilemanager.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by huanglongfei on 16/7/9.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    public RecyclerViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }


    public static RecyclerViewHolder get(Context context, ViewGroup parent, int layoutId) {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(context, itemView, parent);
        AutoUtils.autoSize(itemView);
        return holder;
    }


    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public RecyclerViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public RecyclerViewHolder addText(int viewId, String text) {
        TextView tv = getView(viewId);
        StringBuilder stringBuilder = new StringBuilder(tv.getContentDescription())
                .append(text);
        tv.setText(stringBuilder);
        return this;
    }
    public RecyclerViewHolder setTextLeft(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setGravity(Gravity.LEFT);
        tv.setText(text);
        return this;
    }

    public RecyclerViewHolder setText(int viewId, SpannableString text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public RecyclerViewHolder setEditText(int viewId, String text) {
        EditText et = getView(viewId);
        et.setText(text);
        return this;
    }

    public RecyclerViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public RecyclerViewHolder setTextVisible(int viewId, int visible) {
        TextView tv = getView(viewId);
        tv.setVisibility(visible);
        return this;
    }

    public RecyclerViewHolder setImageVisible(int viewId, int visible) {
        ImageView view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    public RecyclerViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public RecyclerViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public RecyclerViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public RecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public RecyclerViewHolder setALLBackgroundColor(int color, int... ids) {
        for (int i = 0; i < ids.length; i++) {
            View view = getView(ids[i]);
            view.setBackgroundColor(color);
        }
        return this;
    }

    public RecyclerViewHolder setShapegroundColor(int viewId, int color) {
        View view = getView(viewId);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setStroke(4, color);
        drawable.setColor(color);
        return this;
    }

    public RecyclerViewHolder setBackground(int viewId, int drawable) {
        TextView view = getView(viewId);
        view.setBackground(mContext.getResources().getDrawable(drawable));
        return this;
    }

    public RecyclerViewHolder setTextDrawable(int id, int drawableId, Rect rect, int gravity) {
        Drawable drawable = null;
        if (drawableId != 0) {
            drawable = itemView.getContext().getResources().getDrawable(drawableId);
            drawable.setBounds(rect);
        }
        TextView textView = (TextView) itemView.findViewById(id);
        switch (gravity) {
            case Gravity.LEFT:
                textView.setCompoundDrawables(drawable, null, null, null);
                break;
            case Gravity.TOP:
                textView.setCompoundDrawables(null, drawable, null, null);
                break;
            case Gravity.RIGHT:
                textView.setCompoundDrawables(null, null, drawable, null);
                break;
            case Gravity.BOTTOM:
                textView.setCompoundDrawables(null, null, null, drawable);
                break;
        }
        return this;
    }
}
