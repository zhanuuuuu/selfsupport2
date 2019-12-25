package com.hlyf.selfsupport.ui;



import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.adapter.SysnLogAdapter;
import com.hlyf.selfsupport.adapter.SysnLogAdapter.SysnLogListenr;
import com.hlyf.selfsupport.config.systemConfig;
import com.hlyf.selfsupport.dao.SysnLogDao;
import com.hlyf.selfsupport.domin.SysnLog;
import com.hlyf.selfsupport.usbsdk.UsbDriver;
import com.hlyf.selfsupport.utilPrint.Bills;
import com.hlyf.selfsupport.utilPrint.Constant;
import com.hlyf.selfsupport.utilPrint.T;
import com.hlyf.selfsupport.utilPrint.Utils;
import com.hlyf.selfsupport.utilPrint.UtilsPrint;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hlyf.selfsupport.util.UIUtils.hideInput;
import static com.hlyf.selfsupport.util.UIUtils.toast;

public class orderlogActivity extends Activity   {

    private final String TAG=this.getClass().getName();
    
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

    @ViewInject(R.id.tv_search)
    private TextView tv_search;

    @ViewInject(R.id.tv_where)
    private TextView tv_where;

    private PullToRefreshListView lv_goodslog;

    private ListView listView;

    private int number=0;

    private int pagenumber=100;

    //数据集
    List<SysnLog> SysnLogList = new ArrayList<SysnLog>();

    private SysnLogDao sysnLogDao;
    private SysnLogAdapter sysnLogAdapter;


    private  UtilsPrint utilsPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlog);
        x.view().inject(orderlogActivity.this);
        initView();
        initData();

        //初始化打印设备
        initPrint();

    }

    private void initPrint() {
        utilsPrint=UtilsPrint.getInstance(this);
        utilsPrint.getUsbDriverService();//连接usb
        utilsPrint.printConnStatus(); //
    }

    private Handler handler=new Handler();

    private void initData() {
        try {

            lv_goodslog = (PullToRefreshListView) findViewById(R.id.lv_goodslog);
            lv_goodslog.setMode(PullToRefreshBase.Mode.BOTH);
            listView=lv_goodslog.getRefreshableView();
            sysnLogDao=new SysnLogDao();
            //这里是删除一个月以前的单子  机器上面只保存一个月的单据
            sysnLogDao.deleteData();
            SysnLogList=sysnLogDao.findSysnLogBy(new SqlInfo("SELECT * FROM SysnLog  ORDER BY id DESC LIMIT 0,"+pagenumber));
//            SysnLogList=SysnLogList==null ? new ArrayList<>():SysnLogList;
//            for(int i=0;i<30;i++){
//                SysnLog sysnLog=new SysnLog();
//                sysnLog.setId(1);//随便写
//                sysnLog.setAddress(systemConfig.address);
//                sysnLog.setTel(systemConfig.tel);
//                sysnLog.setStoreId(systemConfig.storeId);
//                sysnLog.setSotreName(systemConfig.storeName);
//                sysnLog.setNumber(123);
//                sysnLog.setMerchantOrderId("1568");
//                sysnLog.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                sysnLog.setMoney("50");
//                sysnLog.setItemData("");
//                SysnLogList.add(sysnLog);
//            }
            sysnLogAdapter=new SysnLogAdapter(this,SysnLogList);

            lv_goodslog.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                /**
                 * 下拉刷新
                 * @param refreshView
                 */
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //得到当前刷新的时间
                    String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    //设置更新时间
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setPullLabel("刷新完成");
                    Log.i("下拉刷新","下拉刷新");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        number=0;
                                        List<SysnLog> logList=new ArrayList<>();
                                        String sql = "select * from SysnLog  order by id limit 0 offset "+(number+1)*pagenumber;
                                        logList=sysnLogDao.findSysnLogBy(new SqlInfo(sql));
                                        if(logList!=null && logList.size()>0){
                                            sysnLogAdapter.setSysnLogList2(logList);
                                        }
                                    }catch (Exception e){
                                         e.printStackTrace();
                                         Log.e(TAG," "+e.getMessage());
                                    }

                                    lv_goodslog.onRefreshComplete();
                                }
                            });
                        }
                    }).start();

                }

                /**
                 * 上拉刷新
                 * @param refreshView
                 */
                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //得到当前刷新的时间
                    String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    //设置更新时间
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setPullLabel("加载更多完成");
                    Log.i("上拉刷新","上拉刷新");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        List<SysnLog> logList = new ArrayList<>();
                                        String sql = "select * from SysnLog  order by id limit "+((number+1)*pagenumber)+" offset "+pagenumber;
                                        number = number+1;
                                        logList = sysnLogDao.findSysnLogBy(new SqlInfo(sql));
                                        if (logList != null && logList.size() > 0) {
                                            sysnLogAdapter.setSysnLogList(logList);
                                        }else{
                                            //toast("数据已经显示完全了",true);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e(TAG," "+e.getMessage());
                                    }
                                    lv_goodslog.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                }

            });

            //设置监听最后一条
            lv_goodslog.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

                @Override
                public void onLastItemVisible() {
                   // Toast.makeText(orderlogActivity.this, "滑动到最后一条了!", Toast.LENGTH_SHORT).show();
                }
            });
            sysnLogAdapter.setSysnLogListenr(new SysnLogListenr() {
                @Override
                public void print(int position, View view, SysnLog sysnLog) {
                    Bills.printGood(utilsPrint.getmUsbDriver(),1,sysnLog,true);
                    //toast("打印成功",false);
                }
            });
            //设置声音
            SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
            soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
            soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
            soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
            lv_goodslog.setOnPullEventListener(soundListener);
            lv_goodslog.setAdapter(sysnLogAdapter);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }



    private void initView() {
        Glide.with(this)
                .load(systemConfig.logoImg)
                .error(R.drawable.headerlogo)
                .into(comm_title_log);
        comm_title_name.setText(systemConfig.tenantName);
        comm_title_return.setVisibility(View.GONE);
        tv_home.setText("回到首页");
    }
    @Event({R.id.comm_title_home,R.id.comm_title_return,R.id.select_payresult,R.id.tv_search})
    private void onClick(View v){

        Intent intent=null;
        switch (v.getId()){
            //查询支付结果
            case R.id.tv_search:
                try{
                    String where=tv_where.getText().toString();
                    if(where.trim().equals("")){
                        //toast("请输入要查询的单号或金额",false);
                        break;
                    }
                    List<SysnLog> logList=new ArrayList<>();
                    logList=sysnLogDao.findSysnLogBy(new SqlInfo("SELECT * FROM SysnLog WHERE money='"+
                            where+"' or merchantOrderId ='"+where+"' ORDER BY id DESC "));
                    if(logList!=null && logList.size()>0){
                        sysnLogAdapter.setSysnLogList2(logList);
                    }else {
                        toast("找不到单据信息",false);
                    }
                }catch (Exception e){
                    toast("找不到单据信息"+e.getMessage(),false);
                }
                break;
            case R.id.comm_title_home:
                //上面是测试
                intent=new Intent(orderlogActivity.this, welcomeActive.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();

                break;

            case R.id.comm_title_return:
                intent=new Intent(this, welcomeActive.class);
                intent.putExtra("come","随便什么值都可以");
                startActivity(intent);
                finish();
                break;
            default:

                break;
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 监听返回键，点击两次退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInput(this);
    }

}
