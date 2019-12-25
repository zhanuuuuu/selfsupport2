package com.hlyf.selfsupport.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hlyf.selfsupport.MainActivity;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.config.systemConfig;
import com.hlyf.selfsupport.ui.testDbUi.DBActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class payResultActivity extends Activity {

    private final String TAG=this.getClass().getName();
    //title
    @ViewInject(R.id.comm_title_log)
    private ImageView comm_title_log;
    @ViewInject(R.id.comm_title_name)
    private TextView comm_title_name;
    @ViewInject(R.id.comm_title_home)
    private LinearLayout comm_title_home;
    @ViewInject(R.id.comm_title_return)
    private LinearLayout comm_title_return;

    @ViewInject(R.id.pay_btn_return)
    private Button pay_btn_return;

    @ViewInject(R.id.pay_txt_msg_money)
    private TextView pay_txt_msg_money;

    //时间  秒s
    private int maxTime = 10;

    //计时器
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        x.view().inject(payResultActivity.this);
        //初始化头部tite数据
        initTitleData();

        //启动定时器
        createTime();

        getIntnet();
    }

    private void getIntnet() {
        Intent intent = getIntent();
        String money=intent.getStringExtra("money");
        pay_txt_msg_money.setText("¥ "+money+" 元");
    }

    private void createTime() {
         //倒计时
        if (mTimer == null) {
            mTimer = new CountDownTimer((long) (maxTime * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!payResultActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        pay_btn_return.setText("返回("+remainTime+"s)");
                    }
                }

                @Override
                public void onFinish() {
                    Intent intent=new Intent(payResultActivity.this, welcomeActive.class);
                    intent.putExtra("come","随便什么值都可以");
                    startActivity(intent);
                    finish();
                }
            };
            mTimer.start();
        }
    }

    @Event({R.id.comm_title_home,R.id.comm_title_return,R.id.pay_btn_return})
    private void onClick(View v) {

        Intent intent=null;
        switch (v.getId()) {
            case R.id.comm_title_home:
                intent=new Intent(this, welcomeActive.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();
                break;

            case R.id.comm_title_return:
                intent=new Intent(this, MainActivity.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();
                break;
            case R.id.pay_btn_return:
                //这里是关闭的  支付成功才会显示出来
                intent=new Intent(this, welcomeActive.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();
                break;
            default:

                break;
        }

    }
    private void initTitleData() {

        comm_title_return.setVisibility(View.GONE);
        Glide.with(this)
                .load(systemConfig.logoImg)
                .error(R.drawable.headerlogo)
                .into(comm_title_log);
        comm_title_name.setText(systemConfig.tenantName);
        pay_btn_return.setText("返回("+maxTime+"s)");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Log.e(TAG,"onDestroy Finish : "+TAG);

    }

    /**
     *  去掉底部导航栏
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }else{
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 监听返回键，点击两次退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
