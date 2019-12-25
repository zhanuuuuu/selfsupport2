package com.warelucent.huishenghuo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity
        extends Activity
{
    private static OnWeixinListener mListener;

    public static void setOnWeixinListener(OnWeixinListener Listener)
    {
        Log.d("OrangeUI ", "OrangeUI setOnWeixinListener begin");
        mListener = Listener;
        Log.d("OrangeUI ", "OrangeUI setOnWeixinListener end");
    }

    public static final OnWeixinListener getOnOnWeixinListener()
    {
        Log.d("OrangeUI ", "OrangeUI getOnOnWeixinListener");
        return mListener;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("OrangeUI ", "OrangeUI onCreate begin");
        if (mListener != null)
        {
            Log.d("OrangeUI ", "OrangeUI mListener.onHandleIntent begin");
            mListener.onHandleIntent(getIntent(), this);
            Log.d("OrangeUI ", "OrangeUI mListener.onHandleIntent end");
        }
        finish();
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Log.d("OrangeUI ", "OrangeUI onNewIntent begin");
        if (mListener != null)
        {
            Log.d("OrangeUI ", "OrangeUI mListener.onHandleIntent begin");
            mListener.onHandleIntent(intent, this);
            Log.d("OrangeUI ", "OrangeUI mListener.onHandleIntent end");
        }
        finish();
    }
}

