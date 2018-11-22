package com.kery.mobilemanager.wight;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.kery.mobilemanager.R;


/**
 * Created by Administrator on 2017/12/13.
 * <p>
 * 这个线性布局主要就是为了能够用rippleDrawable
 */

public class RippleLinearLayout extends LinearLayout {
    public RippleLinearLayout(Context context) {
        this(context, null);
    }

    public RippleLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackgroundResource(R.drawable.ripple_activity_water_gatewaylist_itembg);
        } else {
            setBackgroundResource(R.drawable.activity_hetongxiangqing_item_bg);
        }
    }

    public RippleLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
