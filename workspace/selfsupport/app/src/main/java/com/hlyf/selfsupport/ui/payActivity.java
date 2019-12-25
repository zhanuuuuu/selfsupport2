package com.hlyf.selfsupport.ui;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hlyf.selfsupport.MainActivity;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.config.appUrlConfig;
import com.hlyf.selfsupport.config.systemConfig;
import com.hlyf.selfsupport.customui.CommonDialog;
import com.hlyf.selfsupport.dao.SysnLogDao;
import com.hlyf.selfsupport.dlbtool.MyFactory;
import com.hlyf.selfsupport.domin.RequestComm;
import com.hlyf.selfsupport.domin.SysnLog;
import com.hlyf.selfsupport.exception.ApiSysException;
import com.hlyf.selfsupport.util.HttpUtils;
import com.hlyf.selfsupport.util.UIUtils;
import com.hlyf.selfsupport.util.Utils;
import com.hlyf.selfsupport.utilPrint.Bills;
import com.hlyf.selfsupport.utilPrint.UtilsPrint;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


import static com.hlyf.selfsupport.dlbtool.MyFactory.getPayOrder;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getPayOrderId;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getPayQuery;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getSelectGoodsRequest;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getSysnOrder;
import static com.hlyf.selfsupport.util.MobileUtils.getIMEI;
import static com.hlyf.selfsupport.util.NetWorkUtils.getIPAddress;
import static com.hlyf.selfsupport.util.UIUtils.getSerialNumber;
import static com.hlyf.selfsupport.util.UIUtils.hideInput;
import static com.hlyf.selfsupport.util.UIUtils.toast;
import static com.hlyf.selfsupport.util.Utils.playMedia;
import static com.hlyf.selfsupport.util.Utils.saveFile;


public class payActivity extends Activity {

    private final String TAG=this.getClass().getName();

    private UtilsPrint utilsPrint;

    //title
    @ViewInject(R.id.comm_title_log)
    private ImageView comm_title_log;
    @ViewInject(R.id.comm_title_name)
    private TextView comm_title_name;

    @ViewInject(R.id.comm_title_home)
    private LinearLayout comm_title_home;
    @ViewInject(R.id.comm_title_return)
    private LinearLayout comm_title_return;

    @ViewInject(R.id.tv_return)
    private TextView tv_return;

    @ViewInject(R.id.tv_home)
    private TextView tv_home;

    @ViewInject(R.id.pay_cutdown_txt)
    private TextView pay_cutdown_txt;

    @ViewInject(R.id.pay_hidden_edt)
    private EditText pay_hidden_edt;

    //支付方式
    @ViewInject(R.id.tv_pay_type)
    private TextView tv_pay_type;

    //金额
    @ViewInject(R.id.tv_pay_money)
    private TextView tv_pay_money;

    @ViewInject(R.id.tv_payOrderId)
    private TextView tv_payOrderId;

    @ViewInject(R.id.tv_merchantOrderId)
    private TextView tv_merchantOrderId;

    @ViewInject(R.id.select_payresult)
    private Button select_payresult;

    @ViewInject(R.id.pay_before)
    private ImageView pay_before;

    @ViewInject(R.id.pay_after)
    private ImageView pay_after;

    //数据dao
    private SysnLogDao sysnLogDao;

    //计时器
    private CountDownTimer mTimer;

    private String resJson;
    private String money;
    private String merchantOrderId;
    private String payType;
    private String paychantOrderId;
    private int actualFee;

    private boolean isPayOrderOk=false;
    private boolean isLoading=false;
    private final int PAYHANDER=0;
    //商品数量
    private int number=0;

    private long fistTime = System.currentTimeMillis();
    private long lastTime = System.currentTimeMillis();
    //循环查询的  30秒内没有查到支付结果 视为支付失败
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //支付结果查询
                case PAYHANDER:
                    //小于
                    if ((System.currentTimeMillis() - fistTime) < 30000) {
                        //走
                        selectPayResult(false,true);
                    }else{
                        select_payresult.setVisibility(View.VISIBLE);
                        final CommonDialog dialog = new CommonDialog(payActivity.this);
                        dialog.setMessage("没有检测到支付结果,点击确定重新查询支付结果")
                                .setImageResId(R.drawable.x)
                                //.setTitle("提示")
                                .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick() {
                                dialog.dismiss();
                                selectPayResult(false,false);
                            }

                            @Override
                            public void onNegtiveClick() {
                                dialog.dismiss();

                            }
                        }).show();
                    }
                    break;
                default:
                        break;

            }
        }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        x.view().inject(payActivity.this);
        playMedia(this,"fukuaima.mp3");
        //初始化头部tite数据
        initTitleData();
        initView();
        getIntentDataDrawView();
        createTime();
        //初始化打印
        initPrint();
        try {
            sysnLogDao=new SysnLogDao();
        } catch (DbException e) {
            toast("数据库初始化失败 "+e.getMessage(),false);
            e.printStackTrace();
        }
    }
    private void initPrint() {
        utilsPrint=UtilsPrint.getInstance(this);
        utilsPrint.getUsbDriverService();//连接usb
        utilsPrint.printConnStatus(); //
    }

    //获取Intent数据 更新页面
    private void getIntentDataDrawView() {

        Intent intent = getIntent();
        resJson=intent.getStringExtra("resJson");
        actualFee=intent.getIntExtra("actualFee",0);
        money=intent.getStringExtra("money");
        number=intent.getIntExtra("number",0);
        //单号
        merchantOrderId=intent.getStringExtra("merchantOrderId");
        //收银流水号
        try {
            paychantOrderId=getPayOrderId();
            tv_payOrderId.setText("凭证号:"+paychantOrderId);
        } catch (ApiSysException e) {
            Log.e(TAG,"凭证号出问题了 "+e.getMessage());
            e.printStackTrace();
        }
        payType=intent.getStringExtra("payType");
        if(payType.equals("wx")){
            tv_pay_type.setText("请打开微信【付款码】对准下方扫码口");
        }else {
            //支付宝
            tv_pay_type.setText("请打开支付宝【付款码】对准下方扫码口");
        }
        tv_pay_money.setText(" ¥ "+money);
        tv_merchantOrderId.setText("小票号:"+merchantOrderId);

    }

    //这里下单失败就重置支付单号
    private void resetPaychantOrderId(){
        try {
            paychantOrderId=getPayOrderId();
            tv_payOrderId.setText("凭证号:"+paychantOrderId);
        } catch (ApiSysException e) {
            Log.e(TAG,"凭证号出问题了 "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTime() {
        //倒计时
        if (mTimer == null) {
            mTimer = new CountDownTimer((long) (300 * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (!payActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        pay_cutdown_txt.setText("请在"+remainTime+"s内完成");
                    }
                }

                @Override
                public void onFinish() {
                    Intent intent=new Intent(payActivity.this, welcomeActive.class);
                    intent.putExtra("come","随便什么值都可以");
                    startActivity(intent);
                    finish();
                }
            };
            mTimer.start();
        }
    }

    private void initView() {

        pay_before.setVisibility(View.VISIBLE);
        pay_after.setVisibility(View.GONE);
        Glide.with(this)
                .load(systemConfig.logoImg)
                .error(R.drawable.headerlogo)
                .into(comm_title_log);

        tv_home.setText("回到首页");
        tv_return.setText("取消支付");

        comm_title_name.setText(systemConfig.tenantName);

        pay_hidden_edt.setText("");
        pay_hidden_edt.requestFocus();
        hideInput(this);
        pay_hidden_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!isPayOrderOk && !isLoading){
                    //去下单
                    gotoPayOrder(pay_hidden_edt.getText().toString());
                }else{
                  //  toast("已经下单成功,若要重新扫码支付,请点击右上方返回按钮",true);
                }
                pay_hidden_edt.setText("");
                pay_hidden_edt.requestFocus();
                return true;
            }


        });
    }
    //去查询单据是否支付成功
    private void selectPayResult(Boolean var1,Boolean sendMessage) {
        if(var1){
             fistTime=System.currentTimeMillis();
        }
        try {
            // getPayQuery(String tradeNo,String merchantOrderId)
            String jsonParam=getPayQuery(paychantOrderId,merchantOrderId);
            HttpUtils.getInstance().doPost(appUrlConfig.queryPayOrder, jsonParam, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //在主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            select_payresult.setVisibility(View.VISIBLE);
                            toast("支付结果查询异常 "+e.getMessage(),false);
                        }
                    });

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if(response.isSuccessful()){
                        String result=response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //解析数据
                                analysisDataPay(var1,result,sendMessage);
                            }
                        });
                        Log.e(TAG," Successful : "+result );

                    }else {
                        //在主线程更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                select_payresult.setVisibility(View.VISIBLE);
                                toast("支付结果查询异常 ",false);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            select_payresult.setVisibility(View.VISIBLE);
            toast("支付结果查询异常 "+e.getMessage(),false);
        }
    }
    //解析数来查询的支付结果
    private void analysisDataPay(boolean var1,String result,Boolean sendMessage) {
        try{
            String res= MyFactory.analysisDataConn(result);
            JSONObject json=JSON.parseObject(res);
            //确认支付成功的条件
            if(json.getString("retCode").equals("000000") && json.getString("status").equals("SUCCESS")){
                pay_cutdown_txt.setText("支付成功");
                //TODO 保存本地数据库
                SysnToDb();
                //TODO 同步单据信息
                SysnOrder();
                //这里跳转页面  去下个页面查询
                Utils.playMedia(this,"payok.mp3");
                Intent intent=new Intent(this,payResultActivity.class);
                intent.putExtra("money",money);
                startActivity(intent);
                finish();

            }else {
                if(sendMessage){
                    //发送消息
                    Message msg = new Message();
                    msg.what = PAYHANDER;
                    handler.sendMessage(msg);
                }else {
                    if(!var1){
                        Utils.playMedia(this,"error.mp3");
                        toast("支付中返回支付状态 ： " +json.getString("status").equals("SUCCESS"),false);
                    }
                    pay_cutdown_txt.setText("支付中........");
                }


            }
        }catch (Exception e){
            e.printStackTrace();
            select_payresult.setVisibility(View.VISIBLE);
            toast("支付失败 "+e.getMessage(),false);
        }
    }
    //保存到数据库
    private void SysnToDb() {
        SysnLog sysnLog=new SysnLog();
        sysnLog.setId(1);//随便写
        sysnLog.setAddress(systemConfig.address);
        sysnLog.setTel(systemConfig.tel);
        sysnLog.setStoreId(systemConfig.storeId);
        sysnLog.setSotreName(systemConfig.storeName);
        sysnLog.setNumber(number);
        sysnLog.setMerchantOrderId(merchantOrderId);
        sysnLog.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        sysnLog.setMoney(money);
        sysnLog.setItemData(resJson);
        sysnLog.setPayOrderId(paychantOrderId);

        if(payType.equals("wx")){
            sysnLog.setPayType("微信支付");
        }else {
            sysnLog.setPayType("支付宝支付");
        }

        //记录日志需要的东西
        String var=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"  "+JSONObject.toJSONString(sysnLog);

        try{
            sysnLogDao.addOneData(sysnLog);
            //下面是打印数据的
            Bills.printGood(utilsPrint.getmUsbDriver(),1,sysnLog,false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveFile(var,new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"SysnSuccss.log");
                }
            }).start();
        }catch (Exception e){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveFile(var,new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"SysnError.log");
                }
            }).start();
            toast("订单本地保存失败 "+e.getMessage(),false);
        }
    }

    //数据同步
    private void SysnOrder() {
        try {
            String jsonParam=getSysnOrder(paychantOrderId,merchantOrderId,payType,(long)actualFee,resJson);
            HttpUtils.getInstance().doPost(appUrlConfig.orderSysn, jsonParam, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //在主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            toast("订单同步失败 "+e.getMessage(),false);
                        }
                    });

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if(response.isSuccessful()){
                        String result=response.body().string();

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                toast("数据同步成功 ",false);
//                            }
//                        });

                        Log.e(TAG," Successful : "+result );

                    }else {
                        //在主线程更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toast("数据同步失败 ",false);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            toast("数据同步失败 "+e.getMessage(),false);
        }
    }

    //去下单
    private void gotoPayOrder(String authcode) {
        try {
//            getPayOrder(String tradeNo,String merchantOrderId,
//            int amount,String authcode)

            String jsonParam=getPayOrder(paychantOrderId,merchantOrderId,actualFee,authcode);
            UIUtils.showLoading(this,"");
            isLoading=true;
            HttpUtils.getInstance().doPost(appUrlConfig.payOrder, jsonParam, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //在主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLoading=false;
                            UIUtils.dismiss();
                            playMedia(payActivity.this,"error.mp3");
                            //toast("支付下单失败 请重新扫描付款码"+e.getMessage(),false);
                        }
                    });

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLoading=false;
                            UIUtils.dismiss();
                        }
                    });

                    if(response.isSuccessful()){
                        String result=response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //解析数据
                                analysisData(result);
                            }
                        });

                        Log.e(TAG," Successful : "+result );

                    }else {
                        //在主线程更新UI
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               resetPaychantOrderId();
                               //toast("支付下单失败 请重新扫描付款码",false);
                           }
                       });
                    }
                }
            });
        }catch (Exception e){
            isLoading=false;
            resetPaychantOrderId();
            e.printStackTrace();
           toast("支付下单失败 请重新扫描付款码"+e.getMessage(),false);
        }finally {
            pay_hidden_edt.setText("");
            pay_hidden_edt.requestFocus();
        }
    }

    //数据解析
    private void analysisData(String result) {
        try{
           String res= MyFactory.analysisDataConn(result);
           JSONObject json=JSON.parseObject(res);
          if(json.getString("retCode").equals("000000")){
              isPayOrderOk=true;
              //这里跳转页面  去下个页面查询
              if (mTimer != null) {
                  mTimer.cancel();
                  mTimer = null;
              }
              pay_cutdown_txt.setText("等待支付中.........");

              pay_before.setVisibility(View.GONE);
              pay_after.setVisibility(View.VISIBLE);

              selectPayResult(true,true);

              //下单成功保存订单信息  这里查询是否支付成功

          }else {
              resetPaychantOrderId();
              playMedia(payActivity.this,"error.mp3");
              //toast("支付下单失败 请重新扫描付款码 原因"+json.getString("retMsg"),false);
          }
        }catch (Exception e){
            resetPaychantOrderId();
            e.printStackTrace();
            playMedia(payActivity.this,"error.mp3");
            toast("支付下单失败 请重新扫描付款码 "+e.getMessage(), false);
        }
    }
    @Event({R.id.comm_title_home,R.id.comm_title_return,R.id.select_payresult})
    private void onClick(View v){

        Intent intent=null;
        switch (v.getId()){
            //查询支付结果
            case R.id.select_payresult:
                selectPayResult(false,false);
                break;
            case R.id.comm_title_home:

                final CommonDialog dialog = new CommonDialog(this);
                dialog.setMessage("确定放弃支付,回到首页重新购物？")
                      //  .setImageResId(R.drawable.x)
                        .setTitle("提示")
                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {
                        dialog.dismiss();
                        Intent intent=new Intent(payActivity.this, welcomeActive.class);
                        intent.putExtra("come","随便什么值都可以");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegtiveClick() {
                        dialog.dismiss();

                    }
                }).show();

                break;

            case R.id.comm_title_return:
                intent=new Intent(this, MainActivity.class);
                intent.putExtra("come","不可以写welcomeActive");
                startActivity(intent);
                finish();
                break;
            default:

                break;
        }

    }
    private void initTitleData() {
        //
        Glide.with(this)
                .load(systemConfig.logoImg)
                .error(R.drawable.headerlogo)
                .into(comm_title_log);
        comm_title_name.setText(systemConfig.tenantName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

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
