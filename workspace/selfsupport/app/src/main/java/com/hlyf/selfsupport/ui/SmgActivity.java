package com.hlyf.selfsupport.ui;

import androidx.appcompat.app.AppCompatActivity;
import cn.iwgang.countdownview.CountdownView;

import android.app.Activity;
import android.os.Bundle;

import com.hlyf.selfsupport.R;

import static com.hlyf.selfsupport.util.UIUtils.toast;

public class SmgActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smg);
        initView();
    }

    private void initView() {
        CountdownView mCvCountdownViewTest22 = (CountdownView)findViewById(R.id.cv_countdownViewTest22);
        mCvCountdownViewTest22.setTag("test22");
        //倒计时30分钟
        long time22 = (long)5 * 60 * 1000;
        mCvCountdownViewTest22.start(time22);
        mCvCountdownViewTest22.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                toast("我执行完毕了",true);
            }
        });
    }
}
