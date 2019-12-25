package com.hlyf.selfsupport.ui;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hlyf.selfsupport.MainActivity;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.config.appUrlConfig;
import com.hlyf.selfsupport.config.systemConfig;
import com.hlyf.selfsupport.customui.LoadDialog;

import com.hlyf.selfsupport.dao.tDlbSnConfigDao;
import com.hlyf.selfsupport.dlbtool.MyFactory;
import com.hlyf.selfsupport.dlbtool.ThreeDESUtilDLB;
import com.hlyf.selfsupport.domin.RequestComm;
import com.hlyf.selfsupport.exception.ApiSysException;
import com.hlyf.selfsupport.exception.ErrorEnum;
import com.hlyf.selfsupport.util.HttpUtils;
import com.hlyf.selfsupport.util.MobileUtils;
import com.hlyf.selfsupport.util.UIUtils;
import com.tbruyelle.rxpermissions.RxPermissions;


import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.hlyf.selfsupport.dlbtool.MyFactory.getErrorEnumByCode;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getRequest;
import static com.hlyf.selfsupport.dlbtool.MyFactory.getVip;
import static com.hlyf.selfsupport.dlbtool.ThreeDESUtilDLB.verify;
import static com.hlyf.selfsupport.util.UIUtils.getSerialNumber;
import static com.hlyf.selfsupport.util.UIUtils.setWindowAlpa;
import static com.hlyf.selfsupport.util.UIUtils.setWindowSoftInputModeHidden;
import static com.hlyf.selfsupport.util.UIUtils.toast;
import static com.hlyf.selfsupport.util.Utils.getVersion;
import static com.hlyf.selfsupport.util.Utils.playMedia;

@ContentView(R.layout.activity_welcome)
public class welcomeActive extends Activity {

    private final String TAG=this.getClass().getName();


    final private int Premssion=1;
    //权限提示框
    private AlertDialog dialog;

    private PopupWindow pwVip;
    //
    private View viewVipInput;

    //根布局
    private View viewRoot;

    private PopupWindow pwSet;
    //
    private View viewSet;

    private EditText vip_input;

    private LoadDialog loadDialog;

    @ViewInject(R.id.btn_vip_hidder)
    private Button btn_vip_hidder;

    //版本号 versionname
    @ViewInject(R.id.versionname)
    private TextView versionname;

    //监听会员输入
    @ViewInject(R.id.wl_hidden_vip_edt)
    private EditText  wl_hidden_vip_edt;
    //
    private final int HIDDEN_BTN=1;
    private final int HIDDEN_BTN_RETURN=2;
    private  int NUM_BTN=1;

    private final int HTTP_ERROR=3;
    private final int HTTP_OK=4;

    private final int HTTP_CLOSE_LOADDING=5;

    private tDlbSnConfigDao tDlbSnConfigDao;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HIDDEN_BTN: //隐藏事件  按五次才有响应
                    NUM_BTN=NUM_BTN+1;
                    Log.e(TAG," NUM_BTN  " +NUM_BTN);
                    if(NUM_BTN>3){
                        //开始操作
                        NUM_BTN=0;
                        setWindowAlpa(true,welcomeActive.this);
                        showPopupSet();
                    }
                    break;
                case HIDDEN_BTN_RETURN:
                    Log.e(TAG," HIDDEN_BTN_RETURN  " +NUM_BTN);
                    NUM_BTN=0;
                    break;
                case HTTP_ERROR:
                    dismiss();
                    Log.e(TAG," HTTP_ERROR  " +(String) msg.obj);
                    toast("HTTP_ERROR 获取会员失败 "+ msg.obj,true);
                    break;
                case HTTP_OK:
                    dismiss();
                    Log.e(TAG," HTTP_OK  " +(String) msg.obj);
                    //获取导数据开始解析数  拿到里面的需要的东西
                    try {
                        String result= analysisData((String) msg.obj);
                        workWithData(result);
                        Log.e(TAG," HTTP_OK  " +result);
                        //toast("获取到的数据是 "+ result,true);
                    } catch (ApiSysException e) {
                        toast("数 获取会员失败 "+ getErrorEnumByCode(e.getExceptionEnum().getCode()),true);
                        e.printStackTrace();
                    }
                    break;


            }

        }
    };

    //解析数据
    private String analysisData(String obj) throws ApiSysException {

        JSONObject jsonObject=null;
        try{
            jsonObject=JSON.parseObject(obj);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"数据解析失败 "+e.getMessage());
            throw new ApiSysException(ErrorEnum.SSCO001005);
        }
        //第二步 验签
        try{
            if(verify(jsonObject.getString("cipherJson") + jsonObject.getString("uuid"), systemConfig.mdkey, jsonObject.getString("sign"))){
            } else {
                throw new ApiSysException(ErrorEnum.SSCO001004);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"数据验签失败 "+e.getMessage());
            throw new ApiSysException(ErrorEnum.SSCO001004);
        }

        //第三部 数据解码
        try{
             return ThreeDESUtilDLB.decrypt(jsonObject.getString("cipherJson"),
                     systemConfig.deskey,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"数据解码失败 "+e.getMessage());
            throw new ApiSysException(ErrorEnum.SSCO001006);
        }
    }
    //处理数据
    private String  workWithData(String obj) throws ApiSysException{

        //第一步 先转换数据  判断
        JSONObject jsonObject=null;
        try{
            jsonObject= JSONObject.parseObject(obj);
        }catch (Exception e){
            e.printStackTrace();
            toast("处理数据 失败 "+e.getMessage(),true);
            throw new ApiSysException(ErrorEnum.SSCO001005);
        }

        if(!jsonObject.getString("retCode").equals("000000") || !jsonObject.getBoolean("success")){
            toast(jsonObject.getString("retMessage"),false);
        }else {
            JSONObject data=jsonObject.getJSONObject("data");
            data=data.getJSONObject("memberInfo");
            systemConfig.userId=data.getString("userId");
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("come","welcomeActive");
            Log.e(TAG,"welcomeActive");
            startActivity(intent);
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(welcomeActive.this);
        viewRoot=LayoutInflater.from(welcomeActive.this).inflate(R.layout.activity_welcome, null);
        loadDialog=new LoadDialog(this,R.style.MyDialog,"加载中......");

       // hideBottomUIMenu();
       // AppManager.getInstance().addActivity(this); //添加到栈中
        //   版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPromission();
        }
        initView();

        initData();



    }

    private void initData() {
        //记录设备号
        systemConfig.sn=getSerialNumber();

    }



    private void initView() {

        versionname.setText("当前版本号："+getVersion(this));
        //下面是 初始化监听 扫描枪的代码
        wl_hidden_vip_edt.setText("");
        wl_hidden_vip_edt.requestFocus();
        wl_hidden_vip_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //这里加载数据
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Premssion) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE
            };

                boolean isPermission=true;

                for(int i=0;i<permissions.length;i++){
                    int permission = ContextCompat.checkSelfPermission(this, permissions[i]);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        isPermission=false;
                    }
                }
                //int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (!isPermission) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }
            }
        }
    }
    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("权限申请")
                .setMessage("请在-应用设置-权限-中，开启相关需要的权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }
    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, Premssion);
    }
    private void checkPromission(){
        RxPermissions rxPermissions=new RxPermissions(welcomeActive.this);
        rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted){
                        //申请的权限全部允许
                        checkSystemConfig();


                    }else{
                        //只要有一个权限被拒绝，就会执行
                        Toast.makeText(welcomeActive.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
                        showDialogTipUserGoToAppSettting();

                    }
                });
    }

    private void  checkSystemConfig(){
        //这里重置配置信息
        try {
            tDlbSnConfigDao=new tDlbSnConfigDao();
            if(!MyFactory.ResetAppUrlConfigAndSystemConfig(this,tDlbSnConfigDao)){
                //去配置页面
                startActivity(new Intent(this,settingActivity.class));
                toast("请联系管理员配置相关机器信息",false);
            }
        } catch (DbException e) {
            e.printStackTrace();
            toast("系统异常 数据库初始化失败",false);
        }
    }
    @Event({R.id.btn_novip,R.id.btn_vip,R.id.btn_vip_hidder})
    private void onClick(View v){

        switch (v.getId()){
            case R.id.btn_novip:
                //toast("我是非会员",true);
                hideBottomUIMenu();
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("come","welcomeActive");
                Log.e(TAG,"welcomeActive");
                startActivity(intent);
                break;
            case R.id.btn_vip_hidder:
               handler.removeMessages(HIDDEN_BTN_RETURN);
                //发送指令
                handler.sendEmptyMessage(HIDDEN_BTN);
                //还原指令
                handler.sendEmptyMessageDelayed(HIDDEN_BTN_RETURN,5000);

                break;
            case R.id.btn_vip:
//                toast("我是会员",true);
                setWindowAlpa(true,welcomeActive.this);
                showPopup();
                break;
            default:

                break;
        }

    }

    private void showPopupSet() {
        if(pwSet==null){
            viewSet = LayoutInflater.from(welcomeActive.this).inflate(R.layout.layout_setting, null);
            pwSet = new PopupWindow(viewSet,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                    false);

            pwSet.setContentView(viewSet);

            LinearLayout layout_setting_ceshi=(LinearLayout)viewSet.findViewById(R.id.layout_setting_ceshi);
            layout_setting_ceshi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwSet.dismiss();
                }
            });

            //检查更新
            LinearLayout layout_setting_jieko=(LinearLayout)viewSet.findViewById(R.id.layout_setting_jieko);
            layout_setting_jieko.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwSet.dismiss();
                    checkVersion(true);
                }
            });

            LinearLayout layout_setting_set=(LinearLayout)viewSet.findViewById(R.id.layout_setting_set);
            layout_setting_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwSet.dismiss();
                    startActivity(new Intent(welcomeActive.this,settingActivity.class));
                }
            });

            LinearLayout layout_setting_print=(LinearLayout)viewSet.findViewById(R.id.layout_setting_print);
            layout_setting_print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重新打印的界面
                    startActivity(new Intent(welcomeActive.this,orderlogActivity.class));
                    pwSet.dismiss();
                    //downloadAPK();

                }
            });

            pwSet.setBackgroundDrawable(new BitmapDrawable());
            //设置各个控件的点击响应

        }

        if(pwSet.isShowing()){
            pwSet.dismiss();
        }

        pwSet.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpa(false,welcomeActive.this);
            }
        });

        pwSet.showAtLocation(viewRoot, Gravity.TOP|Gravity.LEFT, 10, 10);

        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        //启动动画
        viewSet.startAnimation(animation);

        NUM_BTN=0;


    }
    //检查更新
    private void checkVersion(Boolean b) {
        try {

            Map<String,String> map=new HashMap<>();
            map.put("sn",getSerialNumber());
            map.put("versionName",getVersion(this));
            HttpUtils.getInstance().doPost(appUrlConfig.checkUpdateApk, map, new HttpUtils.RequestCallBack() {
                @Override
                public void success(String successResult) {
                    String result=successResult;
                    try{
                        JSONObject jsonObject= JSON.parseObject(result);
                        if(jsonObject.getString("retCode").equals("000000")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(welcomeActive.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("提示");
                                    builder.setMessage("检查到新的版本,是否升级当前应用？");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            MobileUtils.downloadAPK(welcomeActive.this,appUrlConfig.updateApk);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    AlertDialog b=builder.create();
                                    b.show();
                                }
                            });
                        }else {
                            if(b){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("当前版本是最新版本",false);
                                    }
                                });
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                @Override
                public void fail(String failResult) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showLoading(){

        loadDialog=new LoadDialog(this,R.style.MyDialog,"加载中......");
        loadDialog.show();

    }
    //通过接口 加载数据开始
    private void getData(String vipNo) {
        try {
            if(loadDialog.isShowing()){
                return;
            }
            showLoading();

            String jsonParam=getVip(vipNo);

            HttpUtils.getInstance().doPost(appUrlConfig.selectMemberInfo, jsonParam, new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg = new Message();
                    msg.what = HTTP_ERROR;
                    msg.obj = ""+e.getMessage();
                    handler.sendMessage(msg);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        String result=response.body().string();
                        Message msg = new Message();
                        msg.what = HTTP_OK;
                        msg.obj = ""+result;
                        handler.sendMessage(msg);
                        Log.e(TAG," Successful : "+result );

                    }else {
                        Message msg = new Message();
                        msg.what = HTTP_ERROR;
                        msg.obj = "Successful 连接失败";
                        handler.sendMessage(msg);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Message msg = new Message();
            msg.what = HTTP_ERROR;
            msg.obj = ""+e.getMessage();
            handler.sendMessage(msg);
        }
    }

    private void dismiss(){
        if(loadDialog!=null){
            loadDialog.dismiss();
        }
    }

    private void showPopup() {
        if(pwVip==null){
            viewVipInput = LayoutInflater.from(welcomeActive.this).inflate(R.layout.pw_vipinput, null);
            pwVip = new PopupWindow(viewVipInput,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);

            pwVip.setContentView(viewVipInput);

            ImageView img_x=(ImageView)viewVipInput.findViewById(R.id.btn_x);
            img_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(PopupWindowActivity.this,"取消",Toast.LENGTH_SHORT).show();
                    pwVip.dismiss();
                }
            });

            vip_input = (EditText) viewVipInput.findViewById(R.id.vip_input);

            setWindowSoftInputModeHidden(vip_input,welcomeActive.this);

            setBtnLenstener();

            pwVip.setBackgroundDrawable(new BitmapDrawable());
            //设置各个控件的点击响应

        }

        vip_input.setText("");

        if(pwVip.isShowing()){
            pwVip.dismiss();
        }

        pwVip.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpa(false,welcomeActive.this);
            }
        });


        pwVip.showAtLocation(viewRoot, Gravity.CENTER, 0, 0);

        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        //启动动画
        viewVipInput.startAnimation(animation);


    }

    //设置0-9的单机事件
    private void setBtnLenstener() {

        Button btn_0=(Button)viewVipInput.findViewById(R.id.btn_0);
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"0";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_1=(Button)viewVipInput.findViewById(R.id.btn_1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"1";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_2=(Button)viewVipInput.findViewById(R.id.btn_2);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"2";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_3=(Button)viewVipInput.findViewById(R.id.btn_3);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"3";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_4=(Button)viewVipInput.findViewById(R.id.btn_4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"4";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_5=(Button)viewVipInput.findViewById(R.id.btn_5);
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"5";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_6=(Button)viewVipInput.findViewById(R.id.btn_6);
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"6";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_7=(Button)viewVipInput.findViewById(R.id.btn_7);
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"7";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_8=(Button)viewVipInput.findViewById(R.id.btn_8);
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"8";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_9=(Button)viewVipInput.findViewById(R.id.btn_9);
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=vip_input.getText().toString()+"9";
                vip_input.setText(content);
                vip_input.setSelection(content.length());
            }
        });

        Button btn_vip_clear=(Button)viewVipInput.findViewById(R.id.btn_vip_clear);
        btn_vip_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vip_input.setText("");
            }
        });

        Button btn_vip_x=(Button)viewVipInput.findViewById(R.id.btn_vip_x);
        btn_vip_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = vip_input.getSelectionStart();
                if(index>0){
                    Editable editable = vip_input.getText();
                    editable.delete(index-1, index);
                }

            }
        });

        Button btn_vip_sure=(Button)viewVipInput.findViewById(R.id.btn_vip_sure);
        btn_vip_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwVip.dismiss();
                if(vip_input.getText().toString().length()>0){
                    //去加载
                    getData(vip_input.getText().toString().trim());
                    vip_input.setText("");
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadDialog!=null ){
            loadDialog.dismiss();
            loadDialog=null;
        }
        //移除所有的消息
        handler.removeCallbacksAndMessages(null);

     //   AppManager.getInstance().finishActivity(this); //从栈中移除
    }

    //每次打开界面都会走这里的方法
    @Override
    protected void onResume() {
        super.onResume();
        //检查升级
        checkVersion(false);

        //这里清空会员信息
        systemConfig.userId="";
        Intent intent = getIntent();
        String come= intent.getStringExtra("come");
        if(come!=null){
            //清空会员信息
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

    // 设置返回按钮的监听事件
    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 监听返回键，点击两次退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 5000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public  void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            View decorView = this.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View
                    .SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }

}
