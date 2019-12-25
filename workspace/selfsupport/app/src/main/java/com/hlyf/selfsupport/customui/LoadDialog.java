package com.hlyf.selfsupport.customui;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hlyf.selfsupport.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadDialog extends Dialog {
    public LoadDialog(@NonNull Context context) {
        super(context);
    }

    public LoadDialog(@NonNull Context context, int themeResId,String msg) {
        super(context, themeResId);
        this.setContentView(R.layout.dialog_laoding);
        this.setCancelable(false);//设置返回键无效
        this.setCanceledOnTouchOutside(false);//设置外部点击无效
        this.getWindow().getAttributes().gravity= Gravity.CENTER;
        TextView TvMsg=(TextView)this.findViewById(R.id.dialog_laoding_msg);
        if(!TextUtils.isEmpty(msg)){
            TvMsg.setVisibility(View.VISIBLE);
            TvMsg.setText(msg);
        }else {
            TvMsg.setVisibility(View.GONE);
        }
    }

    protected LoadDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public LoadDialog setMessage(String msg){
        TextView TvMsg=(TextView)this.findViewById(R.id.dialog_laoding_msg);
        if(!TextUtils.isEmpty(msg)){
            TvMsg.setVisibility(View.VISIBLE);
            TvMsg.setText(msg);
        }else {
            TvMsg.setVisibility(View.GONE);
        }
        return this;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }else{
            dismiss();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
