package com.hlyf.selfsupport.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author  zmy
 * @apiNote  自定义Dialog 遮挡框
 */
public class DiaLogUtils {

    private static AlertDialog tDialog_laoding;

    private static void showDefaultProgressDialog(Activity context, String title, String msg, boolean canceledOnTouchOutside, boolean cancelable, DialogInterface.OnCancelListener listener) {
        if (context != null && !context.isFinishing()) {
            closeKeyboardHidden(context);
            initDialog();
            if (TextUtils.isEmpty(title)) {
                title = "";
            }


        }
    }

    public static void dismissProgress() {
        initDialog();
    }

    private static void initDialog() {
        //第一个
        if(tDialog_laoding!=null && tDialog_laoding.isShowing()){
            tDialog_laoding.dismiss();
            tDialog_laoding=null;
        }
    }

    private static void closeKeyboardHidden(Activity context) {
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            @SuppressLint("WrongConstant") InputMethodManager inputmanger = (InputMethodManager)context.getSystemService("input_method");
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
