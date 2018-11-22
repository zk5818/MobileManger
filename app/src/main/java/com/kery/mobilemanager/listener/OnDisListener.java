package com.kery.mobilemanager.listener;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2018/5/23.
 */

public  class OnDisListener implements Dialog.OnDismissListener {
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dialog instanceof BottomSheetDialog) {
            BottomSheetDialog sheetDialog = (BottomSheetDialog) dialog;
            FrameLayout content = (FrameLayout) sheetDialog.findViewById(android.R.id.content);
            if (content == null)
                return;
            //要解决bottomsheetdialog滑动，不再显示的问题
            FrameLayout frameLayout = (FrameLayout) sheetDialog.findViewById(android.R.id.content);
            for (int i = 0; i < frameLayout.getChildCount(); i++) {
                View view = frameLayout.getChildAt(i);
                if (view instanceof CoordinatorLayout) {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view;
                    Resources resources = coordinatorLayout.getResources();
                    for (int j = 0; j < coordinatorLayout.getChildCount(); j++) {
                        int id = coordinatorLayout.getChildAt(j).getId();
                        if (TextUtils.equals(resources.getResourceEntryName(id), "design_bottom_sheet")) {
                            BottomSheetBehavior behavior = BottomSheetBehavior.from(coordinatorLayout.getChildAt(j));
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            break;
                        }
                    }
                    break;
                }
            }

        }
    }
}
