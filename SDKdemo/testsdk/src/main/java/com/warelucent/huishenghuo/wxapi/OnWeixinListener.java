package com.warelucent.huishenghuo.wxapi;


import android.app.Activity;
import android.content.Intent;

public abstract interface OnWeixinListener
{
    public abstract void onHandleIntent(Intent paramIntent, Activity paramActivity);
}