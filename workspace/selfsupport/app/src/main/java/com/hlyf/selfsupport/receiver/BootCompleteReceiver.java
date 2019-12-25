package com.hlyf.selfsupport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.hlyf.selfsupport.ui.welcomeActive;

/**
 * Created by Administrator on 2018-04-14.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    public BootCompleteReceiver(){
        Log.e(TAG, "BootCompleteReceiver. 构造方法执行了...");
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        //接收到开机的广播
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent i = new Intent(context, welcomeActive.class);
            //非常重要，如果缺少的话，程序将在启动时报错
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
