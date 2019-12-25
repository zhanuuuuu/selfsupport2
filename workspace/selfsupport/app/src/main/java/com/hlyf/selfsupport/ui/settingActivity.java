package com.hlyf.selfsupport.ui;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.hlyf.selfsupport.customui.LoadDialog;
import com.hlyf.selfsupport.dao.tDlbSnConfigDao;
import com.hlyf.selfsupport.dlbtool.MyFactory;
import com.hlyf.selfsupport.domin.tDlbSnConfig;
import com.hlyf.selfsupport.util.HttpUtils;
import com.hlyf.selfsupport.util.UIUtils;
import com.hlyf.selfsupport.util.Utils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import static com.hlyf.selfsupport.util.UIUtils.getSerialNumber;
import static com.hlyf.selfsupport.util.UIUtils.toast;


public class settingActivity extends Activity {

    private final String TAG=this.getClass().getName();



    @ViewInject(R.id.et_set_ip)
    private EditText et_set_ip;


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

    //上传机器型号
    @ViewInject(R.id.btn_set_commitset)
    private Button btn_set_commitset;

    //下载配置信息
    @ViewInject(R.id.btn_set_upload)
    private Button btn_set_upload;

    //查看当前配置
    @ViewInject(R.id.btn_set_lookset)
    private Button btn_set_lookset;

    //Scroll的最外层
    @ViewInject(R.id.ly_set_hidden)
    private LinearLayout ly_set_hidden;

    //商品查询的地址
    @ViewInject(R.id.tv_selectgoods)
    private TextView tv_selectgoods;

    //更改商品数量地址
    @ViewInject(R.id.tv_updategoods)
    private TextView tv_updategoods;

    //会员查询的地址
    @ViewInject(R.id.tv_vipgoods)
    private TextView tv_vipgoods;

    //删除购物车地址
    @ViewInject(R.id.tv_deletecart)
    private TextView tv_deletecart;

    //提交购物车地址
    @ViewInject(R.id.tv_commitcart)
    private TextView tv_commitcart;

    //支付下单接口
    @ViewInject(R.id.tv_payorder)
    private TextView tv_payorder;

    //支付查询接口
    @ViewInject(R.id.tv_querypayoder)
    private TextView tv_querypayoder;

    //单据同步地址
    @ViewInject(R.id.tv_sysnorder)
    private TextView tv_sysnorder;

    //检查更新的地址
    @ViewInject(R.id.tv_checkupdate)
    private TextView tv_checkupdate;

    private tDlbSnConfigDao tDlbSnConfigDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        x.view().inject(settingActivity.this);
        //初始化数据库操作的Dao
        initDb();
        //保存数据的
       // saveSnConfig();
        //初始化界面
        initView();
        ResetData();


    }

    private void initDb() {
        try{
            tDlbSnConfigDao=new tDlbSnConfigDao();
        }catch (Exception e){
            e.printStackTrace();
            toast("数据初始化失败",false);
        }
    }

    private void ResetAppUrlConfigAndSystemConfig(){
        if(MyFactory.ResetAppUrlConfigAndSystemConfig(this,tDlbSnConfigDao)){
            //这里是读取信息成功 查看信息
            ResetData();
            ly_set_hidden.setVisibility(View.VISIBLE);
        }else {
            toast("请联系管理员进行配置后再点击下载配置信息",false);
        }
    }

    private void saveSnConfig() {
//        String tenant,
//        String cartId,
//        String ip,
//        String storeId,
//        String sotreName, String sn, int lineId
        tDlbSnConfig tDlbSnConfig=new tDlbSnConfig("0002","0002",
                "192.16.11.242:12379","0002","华隆测试门店","JB123456789456123456",1);
        tDlbSnConfig.setTel("联系电话:1362862210");
        tDlbSnConfig.setAddress("江夏区武汉市汤逊湖社区");
        tDlbSnConfig.setLogoImg("http://www.newasp.net/attachment/soft/2017/0601/150644_11107651.png");
        tDlbSnConfig.setTenantName("北京商城");
        tDlbSnConfig.setRemarks("123");
        try{
            tDlbSnConfigDao.deleteAllData();
            tDlbSnConfigDao.addOneData(tDlbSnConfig);
        }catch (Exception e){
            e.printStackTrace();
            toast("数据库系统异常",false);
        }

    }

    private void ResetData() {

        Glide.with(this)
                .load(systemConfig.logoImg)
                .error(R.drawable.headerlogo)
                .into(comm_title_log);
        comm_title_name.setText(systemConfig.tenantName);
        tv_selectgoods.setText(appUrlConfig.selectGoods);

        //更改商品数量地址
        tv_updategoods.setText(appUrlConfig.updateGoods);

        //会员查询的地址
        tv_vipgoods.setText(appUrlConfig.selectMemberInfo);

        //删除购物车地址
        tv_deletecart.setText(appUrlConfig.clearCartInfo);

        //提交购物车地址
        tv_commitcart.setText(appUrlConfig.commitCartInfo);

        //支付下单接口
        tv_payorder.setText(appUrlConfig.payOrder);

        //支付查询接口
        tv_querypayoder.setText(appUrlConfig.queryPayOrder);

        //单据同步地址
        tv_sysnorder.setText(appUrlConfig.orderSysn);

        //检查更新的地址
        tv_checkupdate.setText(appUrlConfig.checkUpdateApk);

    }

    private void initView() {
        tv_home.setText("回到首页");
        comm_title_return.setVisibility(View.GONE);
        ly_set_hidden.setVisibility(View.GONE);


    }

    @Event({R.id.comm_title_home,R.id.comm_title_return,
            R.id.btn_set_commitset,R.id.btn_set_upload,R.id.btn_set_lookset})
    private void onClick(View v){

        Intent intent=null;
        switch (v.getId()){
            case R.id.comm_title_home:
                intent=new Intent(this, welcomeActive.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();
                break;

            case R.id.comm_title_return:
                intent=new Intent(this, welcomeActive.class);
                intent.putExtra("come","不可以写welcomeActive");
                startActivity(intent);
                finish();
                break;

                //上传机器序列号信息 后台根据序列号配置机器相关信息
            case R.id.btn_set_commitset:
                commitsetConfig();
                break;
            //下载配置信息到sqlite中
            case R.id.btn_set_upload:
                uploadsetConfig();
                break;
            //配置
            case R.id.btn_set_lookset:
                //查看配置的信息
                ResetAppUrlConfigAndSystemConfig();

                break;
            default:

                break;
        }

    }

    //上传数据到后台
    private void commitsetConfig() {
        try {
            String url="http://"+et_set_ip.getText().toString()+"/HLDLB/own/api/insertSnConfig";
            Log.e(TAG,"访问的地址： "+url);
            UIUtils.showLoading(this,"");
            Map<String,String> map=new HashMap<>();
            map.put("sn",getSerialNumber());
            HttpUtils.getInstance().doPost(url, map, new HttpUtils.RequestCallBack() {
                @Override
                public void success(String successResult) {
                    String result=successResult;
                    Log.e(TAG,result);
                    UIUtils.dismiss();
                    try{
                        JSONObject jsonObject= JSON.parseObject(result);
                        if(jsonObject.getString("retCode").equals("000000")){
                            toast("SN上传成功,等待管理员配置相关信息以后在进行下载",true);
                        }else {
                            toast("SN上传失败,"
                                    +jsonObject.getString("retMessage"),true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        toast("数据解析异常",true);
                    }
                }

                @Override
                public void fail(String failResult) {
                    UIUtils.dismiss();
                    Log.e(TAG,"上传SN到服务器失败"+failResult);
                    toast("上传SN到服务器失败0"+failResult,true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            UIUtils.dismiss();
            Log.e(TAG,"上传SN到服务器失败"+e.getMessage());
            toast("上传SN到服务器失败"+e.getMessage(),true);
        }
    }

    //下载数据到本地
    private void uploadsetConfig() {
        try {
            String url="http://"+et_set_ip.getText().toString()+"/HLDLB/own/api/selectSnConfig";
            Log.e(TAG,"访问的地址： "+url);
            UIUtils.showLoading(this,"");
            Map<String,String> map=new HashMap<>();
            map.put("sn",getSerialNumber());
            HttpUtils.getInstance().doPost(url, map, new HttpUtils.RequestCallBack() {

                @Override
                public void success(String successResult) {
                    String result=successResult;
                    Log.e(TAG,result);
                    UIUtils.dismiss();
                    try{
                        JSONObject jsonObject= JSON.parseObject(result);
                        if(jsonObject.getString("retCode").equals("000000")){
                            //解析数据
                            analysisData(jsonObject);
                        }else {
                            toast("配置信息下载失败"
                                    +jsonObject.getString("retMessage"),true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        toast("数据解析异常",true);
                    }
                }



                @Override
                public void fail(String failResult) {
                    UIUtils.dismiss();
                    Log.e(TAG,"配置信息下载失败"+failResult);
                    toast("配置信息下载失败0"+failResult,true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            UIUtils.dismiss();
            Log.e(TAG,"配置信息下载失败"+e.getMessage());
            toast("配置信息下载失败"+e.getMessage(),true);
        }
    }

    //数据解析
    private void analysisData(JSONObject jsonObject) {
        try{
            tDlbSnConfig tDlbSnConfig=JSON.parseObject(jsonObject.getString("data"),tDlbSnConfig.class);
//            tDlbSnConfig.setId(0);
            if(tDlbSnConfig!=null && tDlbSnConfig.getTenant()!=null & tDlbSnConfig.getIp()!=null & tDlbSnConfig.getCartId()!=null && tDlbSnConfig.getStoreId()!=null){
                //更新数据库
                try{
                    tDlbSnConfigDao.deleteAllData();
                    tDlbSnConfigDao.addOneData(tDlbSnConfig);
                    toast("配置信息下载成功",false);
                }catch (Exception e){
                    e.printStackTrace();
                    toast("数据库更新配置信息系统异常",false);
                }

            }else{
                toast("配置信息不完整,请联系管理员重新配置",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"配置信息数据解析失败 "+e.getMessage());
            toast("配置信息数据解析失败 "+e.getMessage(),true);
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

    @Override
    protected void onDestroy() {
        hideInput();
        super.onDestroy();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 监听返回键，点击两次退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        //如果window上view获取焦点 && view不为空
        if(imm.isActive()&&getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken()!=null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }



}
