package com.hlyf.selfsupport.util;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.hlyf.selfsupport.MyApplication;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.customui.LoadDialog;

import java.lang.reflect.Method;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 专门提供为处理一些UI相关的问题而创建的工具类，
 * 提供资源获取的通用方法，避免每次都写重复的代码获取结果。
 */
public class UIUtils {


    /**

       * 获取手机序列号

       *

       * @return 手机序列号

       */

  @SuppressLint({"NewApi", "MissingPermission"})
  public static String getSerialNumber() {

    String serial = "";

    try {

      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) { //8.0+

        serial = Build.SERIAL;

      } else {//8.0-

        Class<?> c = Class.forName("android.os.SystemProperties");

        Method get = c.getMethod("get", String.class);

        serial = (String) get.invoke(c, "ro.serialno");

      }

    } catch (Exception e) {

      e.printStackTrace();

      Log.e("e", "读取设备序列号异常：" + e.toString());

    }

    return serial;

  }


    public static String getSharedPreferences(Context context,String key){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, null);
    }

    public static void setSharedPreferences(Context context,String key, String value){
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit()
//                .putString(SELFSUPPORT, jobNumber)
//                .apply();
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .commit();
    }


    public static Context getContext(){
        return MyApplication.context;
    }

    public static Handler getHandler(){
        return MyApplication.handler;
    }

    //返回指定colorId对应的颜色值
    public static int getColor(int colorId){
        return getContext().getResources().getColor(colorId);
    }

    //加载指定viewId的视图对象，并返回
    public static View getView(int viewId){
        View view = View.inflate(getContext(), viewId, null);
        return view;
    }

    public static String[] getStringArr(int strArrId){
        String[] stringArray = getContext().getResources().getStringArray(strArrId);
        return stringArray;
    }

    //将dp转化为px
    public static int dp2px(int dp){
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);//实现四舍五入
    }

    public static int px2dp(int px){
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);//实现四舍五入
    }

    //保证runnable中的操作在主线程中执行
    public static void runOnUiThread(Runnable runnable) {
        if(isInMainThread()){
            runnable.run();
        }else{
            UIUtils.getHandler().post(runnable);
        }
    }
    //判断当前线程是否是主线程
    private static boolean isInMainThread() {
        int currentThreadId = android.os.Process.myTid();
        return MyApplication.mainThreadId == currentThreadId;

    }


    public static void toast(String message, boolean isLengthLong){
        Toast toast=Toast.makeText(UIUtils.getContext(), message,isLengthLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**
     * 这里是加载的通用方法
     */
    private static  LoadDialog loadDialog=null;
    public static void showLoading(Context context,String message){
        //setWindowAlpa(true, MainActivity.this);
        loadDialog=new LoadDialog(context, R.style.MyDialog,message);
        loadDialog.show();
    }
    public static void dismiss(){
        //setWindowAlpa(false,MainActivity.this);
        if(loadDialog!=null){
            loadDialog.dismiss();
        }
    }

    /**
     * 动态设置Activity背景透明度
     *
     * @param isopen
     */
    public static void setWindowAlpa(boolean isopen,Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }
        final Window window = ((Activity) context).getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ValueAnimator animator;
        if (isopen) {
            animator = ValueAnimator.ofFloat(1.0f, 0.5f);
        } else {
            animator = ValueAnimator.ofFloat(0.5f, 1.0f);
        }
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                lp.alpha = alpha;
                window.setAttributes(lp);
            }
        });
        animator.start();
    }

    //进制键盘弹出
    public static void setWindowSoftInputModeHidden(EditText editText, Activity activity){
        if (android.os.Build.VERSION.SDK_INT <= 10) {//4.0以下 danielinbiti
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        View v = context.getWindow().peekDecorView();
        //如果window上view获取焦点 && view不为空
        if(imm.isActive() && context.getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (context.getCurrentFocus().getWindowToken()!=null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }


    }
}
