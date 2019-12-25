package com.hlyf.selfsupport.ui.testDbUi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.dlbtool.NetUtils;
import com.hlyf.selfsupport.ui.payResultActivity;
import com.hlyf.selfsupport.ui.settingActivity;
import com.hlyf.selfsupport.util.HttpUtils;
import com.hlyf.selfsupport.util.UIUtils;
import com.hlyf.selfsupport.util.Utils;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.hlyf.selfsupport.dlbtool.MyFactory.getTimeUnix;
import static com.hlyf.selfsupport.dlbtool.WxPayHelper.createNonceStr;
import static com.hlyf.selfsupport.dlbtool.WxPayHelper.createWxPaySign;
import static com.hlyf.selfsupport.dlbtool.WxPayHelper.map2XML;
import static com.hlyf.selfsupport.dlbtool.WxPayHelper.postXml;
import static com.hlyf.selfsupport.dlbtool.WxPayHelper.postXml2;
import static com.hlyf.selfsupport.util.UIUtils.getSerialNumber;
import static com.hlyf.selfsupport.util.UIUtils.toast;
import static com.hlyf.selfsupport.util.Utils.saveFile;

public class WXtestActivity extends Activity {
    private final String TAG=this.getClass().getName();

    @ViewInject(R.id.btn_facepay)
    private Button btn_facepay;

    @ViewInject(R.id.btn_getWxpayfaceRawdata)
    private Button btn_getWxpayfaceRawdata;

    @ViewInject(R.id.btn_get_wxpayface_authinfo)
    private Button btn_get_wxpayface_authinfo;

    @ViewInject(R.id.btn_getWxpayfaceUserInfo)
    private Button btn_getWxpayfaceUserInfo;

    @ViewInject(R.id.btn_get_wxpayface_authinfoBy)
    private Button btn_get_wxpayface_authinfoBy;

    @ViewInject(R.id.btn_dogetfacecode)
    private Button btn_dogetfacecode;

    @ViewInject(R.id.btn_dogetfacecodeZhiJie)
    private Button btn_dogetfacecodeZhiJie;


    @ViewInject(R.id.et_re)
    private EditText et_re;

    private String rawdata="";

    private String authinfo="";

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //updateWxpayfacePayResult();
                    saveFile(new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())+"  releasePayFace 开始",new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");
                    releasePayFace();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxtest);
        x.view().inject(WXtestActivity.this);

    }

    @Event({R.id.btn_facepay,
            R.id.btn_getWxpayfaceRawdata,
            R.id.btn_get_wxpayface_authinfo,
            R.id.btn_getWxpayfaceUserInfo,
            R.id.btn_get_wxpayface_authinfoBy,
            R.id.btn_dogetfacecode,R.id.btn_dogetfacecodeZhiJie})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.btn_facepay: //人脸支付
                initPayFace();
                break;
            case R.id.btn_getWxpayfaceRawdata:
                getWxpayfaceRawdata();
                break;
            case R.id.btn_get_wxpayface_authinfoBy://哆啦宝
                get_wxpayface_authinfo2();
                break;
            case R.id.btn_get_wxpayface_authinfo://官方
                get_wxpayface_authinfo3();
                break;
            case R.id.btn_getWxpayfaceUserInfo:
                doFaceRecognize();
                break;
            case R.id.btn_dogetfacecode:
                doGetFaceCode();
                break;
            case R.id.btn_dogetfacecodeZhiJie:
                doGetFaceCode2();
                break;
            default:

                break;
        }

    }

    private void releasePayFace() {
        WxPayFace.getInstance().releaseWxpayface(this);
    }
    //获取调用凭证
    private void get_wxpayface_authinfo() {

        et_re.setText("");
        String key = "ABCDEFG123456789987654321UTRON88";

        SortedMap<Object, Object> map = new TreeMap<>();
        map.put("store_id","0001");
        map.put("store_name","华隆0001测试门店");
        map.put("device_id","000101");
        map.put("rawdata",rawdata);
        map.put("appid","wx7a16caec7c2d6bd8");
        map.put("mch_id","1335237301");
        map.put("now",getTimeUnix());
        map.put("version","1");
        map.put("sign_type","MD5");
        map.put("nonce_str",createNonceStr(32));
        String sign = createWxPaySign(key, map);
        map.put("sign",sign);
        String xmlParam = map2XML(map);
        et_re.setText(xmlParam);
        try{
           String res= postXml2("https://payapp.weixin.qq.com/face/get_wxpayface_authinfo",xmlParam);
        }catch (Exception e){
            et_re.setText(" ："+e.getMessage());
        }

    }

    //这里是走的哆啦宝
    private void get_wxpayface_authinfo2() {

        et_re.setText("");

        Map<String, String> map = new HashMap<>();
        map.put("agentNum","10001015465911345452493");
        map.put("customerNum","10001115747349958190179");
        map.put("deviceNo","10011015749236270554092");
        map.put("storeId","10001215747352505438812");
        map.put("rawData",rawdata);
        map.put("accessKey","b20fc79703ae440198c121640d31ef330354cf0d");
        map.put("secretKey","8e3ed733f9cf4982a0f8244576e54605d75c1e5d");

        try{
            String url="http://"+"192.168.11.242:12379"+"/HLDLB/wxpay/api/getauthinfo";
            Log.e(TAG,"访问的地址： "+url);
            UIUtils.showLoading(this,"");

            map.put("sn",getSerialNumber());
            HttpUtils.getInstance().doPost(url, map, new HttpUtils.RequestCallBack() {

                @Override
                public void success(String successResult) {
                    String result=successResult;
                    Log.e(TAG,result);
                    UIUtils.dismiss();
                    et_re.setText("我是获取来的数据 ："+result);

                }



                @Override
                public void fail(String failResult) {
                    UIUtils.dismiss();
                    toast("fail 0 :"+failResult,true);
                }
            });

        }catch (Exception e){
            et_re.setText(" ："+e.getMessage());
        }

    }

    private void stopFaceRecognize() {

        saveFile(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())
                        +"  stopFaceRecognize 关闭人脸识别"
                       ,
                new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("authinfo", authinfo); // 调用凭证，必填
        WxPayFace.getInstance().stopWxpayface(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                if (code == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }
                /*
                在这里处理您自己的业务逻辑
                 */
            }
        });
    }

    private void updateWxpayfacePayResult() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("appid","wx6fafadc3da56fbb6");
        map.put("mch_id","1526872041");
        map.put("store_id", "0001"); // 门店编号，必填
        map.put("authinfo",authinfo);
        map.put("payresult", "SUCCESS"); // 支付结果，SUCCESS:支付成功   ERROR:支付失败   必填
        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述

                saveFile(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())
                                +"  updateWxpayfacePayResult 接收回调"
                                +"   code:"+code+"   msg:"+msg,
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");

                if (code == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }
                /*
                在这里处理您自己的业务逻辑：
                执行到这里说明用户已经确认支付结果且成功了，此时刷脸支付界面关闭，您可以在这里选择跳转到其它界面
                 */

                Utils.playMedia(WXtestActivity.this,"payok.mp3");
                Intent intent=new Intent(WXtestActivity.this, payResultActivity.class);
                intent.putExtra("money","100");
                startActivity(intent);
            }
        });
    }


    public boolean doGetFaceCode2() {

        saveFile(new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())+"  doGetFaceCode2 开始",new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");
        Map<String, String> m1 = new HashMap<String, String>();

        m1.put("appid","wx6fafadc3da56fbb6");
        m1.put("mch_id","1526872041");
        m1.put("store_id", "0001"); // 门店编号，必填
        m1.put("authinfo",authinfo);
        m1.put("face_authtype", "FACEPAY_DELAY"); // FACEPAY：人脸凭证，常用于人脸支付    FACEPAY_DELAY：延迟支付   必填
        m1.put("ask_face_permit", "0"); // 展开人脸识别授权项，详情见上方接口参数，必填
        m1.put("ask_ret_page", "1");  // 是否展示微信支付成功页，可选值："0"，不展示；"1"，展示，非必填
        m1.put("face_code_type","1");
        m1.put("ignore_update_pay_result","1");
        WxPayFace.getInstance().getWxpayfaceCode(m1, new IWxPayfaceCallback() {
            @Override
            public void response(final Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    et_re.setText("人脸支付出错了 :");
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                String faceCode = info.get("face_code").toString(); // 人脸凭证，用于刷脸支付
                saveFile(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())
                                +"  doGetFaceCode2 接收回调"
                                +"我是获取到的人脸 :"+faceCode+"   code:"+code+"   msg:"+msg,
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");

                et_re.setText("我是获取到的人脸 :"+faceCode+"   code:"+code+"   msg:"+msg);

                stopFaceRecognize();
                //handler.sendEmptyMessageDelayed(1,5000);

                toast("我是获取到的人脸 :"+faceCode,true);
                String openid = info.get("openid").toString(); // openid
                String sub_openid = ""; // 子商户号下的openid(服务商模式)
                int telephone_used = 0; // 获取的`face_code`，是否使用了请求参数中的`telephone`
                int underage_state = 0; // 用户年龄信息（需联系微信支付开通权限）
                if (info.get("sub_openid") != null) sub_openid = info.get("sub_openid").toString();
                if (info.get("telephone_used") != null) telephone_used = Integer.parseInt(info.get("telephone_used").toString());
                if (info.get("underage_state") != null) underage_state = Integer.parseInt(info.get("underage_state").toString());
                if (code == null || faceCode == null || openid == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    toast("调用返回非成功信息,return_msg:" + msg + "   ",true);
                    return ;
                }


       	        /*
       	        在这里处理您自己的业务逻辑
       	        解释：您在上述中已经获得了支付凭证或者用户的信息，您可以使用这些信息通过调用支付接口来完成支付的业务逻辑
       	        需要注意的是：
       	            1、上述注释中的内容并非是一定会返回的，它们是否返回取决于相应的条件
       	            2、当您确保要解开上述注释的时候，请您做好空指针的判断，不建议直接调用
       	         */
            }
        });
        return true;
    }

    public boolean doGetFaceCode() {

        saveFile(new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())+"  doGetFaceCode 开始",new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");
        Map<String, String> m1 = new HashMap<String, String>();

        m1.put("appid","wx6fafadc3da56fbb6");
        m1.put("mch_id","1526872041");
        m1.put("store_id", "0001"); // 门店编号，必填
        m1.put("authinfo",authinfo);
        m1.put("face_authtype", "FACEPAY"); // FACEPAY：人脸凭证，常用于人脸支付    FACEPAY_DELAY：延迟支付   必填
        m1.put("ask_face_permit", "0"); // 展开人脸识别授权项，详情见上方接口参数，必填
        m1.put("ask_ret_page", "1");  // 是否展示微信支付成功页，可选值："0"，不展示；"1"，展示，非必填
        m1.put("face_code_type","1");
        m1.put("ignore_update_pay_result","1");
        WxPayFace.getInstance().getWxpayfaceCode(m1, new IWxPayfaceCallback() {
            @Override
            public void response(final Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    et_re.setText("人脸支付出错了 :");
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                String faceCode = info.get("face_code").toString(); // 人脸凭证，用于刷脸支付
                saveFile(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date())
                                +"  doGetFaceCode 接收回调"
                        +"我是获取到的人脸 :"+faceCode+"   code:"+code+"   msg:"+msg,
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"FaceCode.log");

                et_re.setText("我是获取到的人脸 :"+faceCode+"   code:"+code+"   msg:"+msg);

                stopFaceRecognize();
               //handler.sendEmptyMessageDelayed(1,5000);

                toast("我是获取到的人脸 :"+faceCode,true);
                String openid = info.get("openid").toString(); // openid
                String sub_openid = ""; // 子商户号下的openid(服务商模式)
                int telephone_used = 0; // 获取的`face_code`，是否使用了请求参数中的`telephone`
                int underage_state = 0; // 用户年龄信息（需联系微信支付开通权限）
                if (info.get("sub_openid") != null) sub_openid = info.get("sub_openid").toString();
                if (info.get("telephone_used") != null) telephone_used = Integer.parseInt(info.get("telephone_used").toString());
                if (info.get("underage_state") != null) underage_state = Integer.parseInt(info.get("underage_state").toString());
                if (code == null || faceCode == null || openid == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    toast("调用返回非成功信息,return_msg:" + msg + "   ",true);
                    return ;
                }
//                Message msg1 = new Message();
//                msg1.what = 1;
//                handler.sendMessage(msg1);


       	        /*
       	        在这里处理您自己的业务逻辑
       	        解释：您在上述中已经获得了支付凭证或者用户的信息，您可以使用这些信息通过调用支付接口来完成支付的业务逻辑
       	        需要注意的是：
       	            1、上述注释中的内容并非是一定会返回的，它们是否返回取决于相应的条件
       	            2、当您确保要解开上述注释的时候，请您做好空指针的判断，不建议直接调用
       	         */
            }
        });
        return true;
    }

    public void doFaceRecognize() {
        // 详细的参数配置表可见上方的“接口参数表”
        Map<String, String> m1 = new HashMap<String, String>();
        m1.put("store_id","0001");
        m1.put("device_id","000101");
        m1.put("appid","wx6fafadc3da56fbb6");
        m1.put("mch_id","1526872041");
//        m1.put("sub_appid", "xxxxxxxxxxxxxx"); // 子商户公众账号ID(非服务商模式不填)
//        m1.put("sub_mch_id", "填您的子商户号"); // 子商户号(非服务商模式不填)

        m1.put("face_authtype", "FACEID-ONCE"); // 人脸识别模式， FACEID-ONCE`: 人脸识别(单次模式) FACEID-LOOP`: 人脸识别(循环模式), 必填
        m1.put("authinfo", authinfo); // 调用凭证，详见上方的接口参数
//        m1.put("ask_unionid", "1"); // 是否获取union_id    0：获取    1：不获取
        WxPayFace.getInstance().getWxpayfaceUserInfo(m1, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                et_re.setText("");
                if (info == null) {
                    toast("调用返回为空",true);
                    new RuntimeException("调用返回为空").printStackTrace();

                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                String openid = info.get("openid").toString(); // openid
                String sub_openid = "";
                if (info.get("sub_openid") != null) sub_openid = info.get("sub_openid").toString(); // 子商户号下的openid(服务商模式)
                String nickName = info.get("nickname").toString(); // 微信昵称
                String token = "";
                if (info.get("token") != null) token = info.get("token").toString(); // facesid,用户获取unionid
                if (code == null || openid == null || nickName == null || !code.equals("SUCCESS")) {
                    toast("调用返回非成功信息,return_msg:" + msg + "   ",true);
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    et_re.setText("调用返回非成功信息,return_msg:" + msg + "   ");
                    return ;
                }
                et_re.setText("openid :"+openid+"    nickName:"+nickName);
                stopFaceRecognize();
                /*
                获取union_id逻辑，传入参数ask_unionid为"1"时使用
                String unionid_code = "";
                if (info.get("unionid_code") != null) unionid_code = info.get("unionid_code").toString();
                if (TextUtils.equals(unionid_code,"SUCCESS")) {
                    //获取union_id逻辑
                } else {
                    String unionid_msg = "";
                    if (info.get("unionid_msg") != null) unionid_msg = info.get("unionid_msg").toString();
                    //处理返回信息
                }
                */
       	        /*
       	        在这里处理您自己的业务逻辑
       	        需要注意的是：
       	            1、上述注释中的内容并非是一定会返回的，它们是否返回取决于相应的条件
       	            2、当您确保要解开上述注释的时候，请您做好空指针的判断，不建议直接调用
       	         */
            }
        });
    }
    private void get_wxpayface_authinfo3() {

        et_re.setText("");
        String key = "ABCDEFG123456789987654321UTRON88";

        Map<String, String> map = new HashMap<>();
        map.put("store_id","0001");
        map.put("store_name","华隆0001测试门店");
        map.put("device_id","000101");
        map.put("appid","wx6fafadc3da56fbb6");
        map.put("mch_id","1526872041");
        map.put("rawdata",rawdata);
        map.put("now",getTimeUnix());
        map.put("version","1");
        map.put("sign_type","MD5");
        map.put("nonce_str",createNonceStr(32));


        try{
            String url="http://"+"192.168.11.242:12379"+"/HLDLB/wxpay/api/getauthinfoByWx";
            Log.e(TAG,"访问的地址： "+url);
            UIUtils.showLoading(this,"");

            map.put("sn",getSerialNumber());
            HttpUtils.getInstance().doPost(url, map, new HttpUtils.RequestCallBack() {
                @Override
                public void success(String successResult) {
                    String result=successResult;
                    Log.e(TAG,result);
                    UIUtils.dismiss();
                    et_re.setText("我是获取来的数据 ："+result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //解析数据
                            analysisData(result);

                        }
                    });

                }

                @Override
                public void fail(String failResult) {
                    UIUtils.dismiss();
                    toast("fail 0 :"+failResult,true);
                }
            });


        }catch (Exception e){
            et_re.setText(" ："+e.getMessage());
        }

    }
    private String analysisData(String obj){
        JSONObject jsonObject=JSON.parseObject(obj);
        try{
            if(jsonObject.getString("retCode").equals("000000")){
                jsonObject=JSON.parseObject(jsonObject.getString("data"));
                authinfo=jsonObject.getString("authinfo");
                et_re.setText(authinfo);
            }
        }catch (Exception e){
            toast(e.getMessage(),true);
        }

        return null;
    }
    //初始化刷脸支付
    private void initPayFace() {
        et_re.setText("");
        Map<String, String> m1 = new HashMap<>();
        WxPayFace.getInstance().initWxpayface(this, m1, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    toast("调用返回为空",true);
                    return ;
                }
                String code = (String) info.get("return_code");
                String msg = (String) info.get("return_msg");
                Log.d(TAG, "response info :: " + code + " | " + msg);
                if (code == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
                    toast("调用返回非成功信息: "+ msg,true);
                    return ;
                }
                Log.d(TAG, "调用返回成功");
                toast("调用返回成功: "+ "response info :: " + code + " | " + msg,true);
                et_re.setText("调用返回成功: "+ "response info :: " + code + " | " + msg);
                /*
                 在这里处理您自己的业务逻辑
                 */
            }
        });
    }

    //获取数据 (getWxpayfaceRawdata)
    private void getWxpayfaceRawdata() {
        et_re.setText("");
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String code = (String) info.get("return_code");
                String msg = (String) info.get("return_msg");
                rawdata = info.get("rawdata").toString();
                if (code == null || rawdata == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }

                toast("获取数据: "+ "rawdata:: " + rawdata,true);
                et_re.setText("获取数据: "+ "rawdata:: " + rawdata);
       	        /*
       	        在这里处理您自己的业务逻辑
       	         */
            }
        });
    }

}
