package com.hlyf.selfsupport.dlbtool;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hlyf.selfsupport.config.appUrlConfig;
import com.hlyf.selfsupport.config.systemConfig;
import com.hlyf.selfsupport.dao.tDlbSnConfigDao;
import com.hlyf.selfsupport.domin.RequestComm;
import com.hlyf.selfsupport.domin.tDlbSnConfig;
import com.hlyf.selfsupport.exception.ApiSysException;
import com.hlyf.selfsupport.exception.ErrorEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.hlyf.selfsupport.dlbtool.ThreeDESUtilDLB.verify;
import static com.hlyf.selfsupport.util.UIUtils.getSerialNumber;
import static com.hlyf.selfsupport.util.UIUtils.toast;
import static com.hlyf.selfsupport.util.Utils.getVersion;


/**
 * @author ZMY
 * @apiNote 我的数据工厂 方法封装
 */
public class MyFactory {

    private final String TAG=this.getClass().getName();

    /**
     *  <pre>
     *      获取UUID
     *  </pre>
     * @throws ApiSysException
     */
    public static String getUUID()  throws ApiSysException {
        try{
            String uuid=UUID.randomUUID().toString().replaceAll("-", "");
            return uuid.substring(0,32);
        }catch (Exception e){
            e.printStackTrace();
            throw  new ApiSysException(ErrorEnum.SSCO001001);
        }
    }

    /**
     * <pre>
     *     得到当前时间的时间戳
     * </pre>
     * @return
     */
    public static String getTimeUnix() {
        return String.valueOf((new Date()).getTime()).substring(0,10);
    }
    /**
     * <pre>
     *     这里支付单号不可以超过32位
     * </pre>
     * @return
     * @throws ApiSysException
     */
    public static String getPayOrderId()  throws ApiSysException {
        try{
            int randNumber =new Random().nextInt(900000) + 100000;
            return systemConfig.tenant
                    +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    +randNumber+systemConfig.storeId;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MyFactory",e.getMessage());
            throw  new ApiSysException(ErrorEnum.SSCO001001);
        }
    }

    /**
     *
     * @param code
     * @return
     */
    public static ErrorEnum getErrorEnumByCode(String code){
        for(ErrorEnum sexEnum : ErrorEnum.values()){
            if(sexEnum.getCode().equals(code)){
                return sexEnum;
            }
        }
        return ErrorEnum.SSCO001002;
    }



    public static String getRequest() throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("cartId","0002");
        map.put("cartFlowNo","0002");
        map.put("storeId","0002");
        map.put("cashierNo","0002");
        map.put("sn","0002");
        map.put("barcode","6928804011203");

        String cipherJson= ThreeDESUtilDLB.encrypt(JSONObject.toJSONString(map), systemConfig.deskey,"utf-8");
        String uuid= getUUID();
        String sign=ThreeDESUtilDLB.md5(cipherJson+uuid,systemConfig.mdkey);
        Map<String,String> mapdata=new HashMap<>();
        mapdata.put("merchantNo","hualong");
        mapdata.put("tenant","0002");
        mapdata.put("storeId","0002");
        mapdata.put("cipherJson", cipherJson);
        mapdata.put("sign",sign);
        mapdata.put("systemId","jdpay-offlinepay-isvaccess");
        mapdata.put("uuid",uuid);
        return JSONObject.toJSONString(mapdata);
    }


    public static String CommReq(Map<String,Object> map) throws Exception {
        String cipherJson= ThreeDESUtilDLB.encrypt(JSONObject.toJSONString(map), systemConfig.deskey,"utf-8");
        String uuid= getUUID();
        String sign=ThreeDESUtilDLB.md5(cipherJson+uuid,systemConfig.mdkey);
        Map<String,String> mapdata=new HashMap<>();
        mapdata.put("merchantNo",systemConfig.merchantno);
        mapdata.put("tenant",systemConfig.tenant);
        mapdata.put("storeId",systemConfig.storeId);
        mapdata.put("cipherJson", cipherJson);
        mapdata.put("sign",sign);
        mapdata.put("systemId",systemConfig.systemId);
        mapdata.put("uuid",uuid);
        return JSONObject.toJSONString(mapdata);
    }
    /**
     * <pre>
     *     根据商品条码查询商品信息
     * </pre>
     * @param barcode
     * @return
     * @throws Exception
     */
    public static String getSelectGoodsRequest(String barcode) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("cartId",systemConfig.cartId);
        map.put("cartFlowNo","0002");
        map.put("storeId",systemConfig.storeId);
        map.put("cashierNo",systemConfig.cashierNo);
        map.put("sn",systemConfig.sn);
        map.put("barcode",barcode);
        return CommReq(map);
    }

    /**
     * <pre>
     *     得到会员的信息
     * </pre>
     * @param vipNo
     * @return
     * @throws Exception
     */
    public static String getVip(String vipNo) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("cartId",systemConfig.cartId);
        map.put("cartFlowNo","0002");
        map.put("storeId",systemConfig.storeId);
        map.put("cashierNo",systemConfig.cashierNo);
        map.put("sn",systemConfig.sn);
        map.put("userId",vipNo);
        map.put("userType","ISV");
        return CommReq(map);
    }
    /**
     *
     *  quantity 0  代表删除
     * @return
     * @throws Exception
     */
    public static String getUpdateGoodsRequest(RequestComm requestComm) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("cartId",systemConfig.cartId);
        map.put("cartFlowNo","0002");
        map.put("storeId",systemConfig.storeId);
        map.put("cashierNo",systemConfig.cashierNo);
        map.put("sn",systemConfig.sn);
        map.put("lineId",requestComm.getLineId());
        map.put("quantity",requestComm.getQuantity());
        map.put("barcode",requestComm.getBarcode());
        return CommReq(map);
    }

    /**
     *<pre>
     *     这里查询更改 相关 配置信息
     *</pre>
     * @param context
     * @param tDlbSnConfigDao
     */
    public static boolean ResetAppUrlConfigAndSystemConfig(Context context, tDlbSnConfigDao tDlbSnConfigDao){
        tDlbSnConfig tDlbSnConfig=null;
        boolean result=true;
        try{
            tDlbSnConfig=tDlbSnConfigDao.findOneSnConfig(tDlbSnConfig.class);
            if(tDlbSnConfig!=null && tDlbSnConfig.getIp()!=null && !tDlbSnConfig.getIp().equals("")){
                appUrlConfig.ip=tDlbSnConfig.getIp();
                String ip=appUrlConfig.ip;
                appUrlConfig.selectMemberInfo="http://"+ip+"/HLDLB/hlyf/api/selectMemberInfo";
                /**
                 * <pre>
                 *     商品查询
                 * </pre>
                 */
                appUrlConfig.selectGoods="http://"+ip+"/HLDLB/hlyf/api/selectGoods";
                /**
                 * <pre>
                 *     更改商品数量
                 * </pre>
                 */
                appUrlConfig.updateGoods="http://"+ip+"/HLDLB/hlyf/api/updateGoods";
                /**
                 * <pre>
                 *     删除商品
                 * </pre>
                 */
                appUrlConfig.deleteGoods="http://"+ip+"/HLDLB/hlyf/api/deleteGoods";
                /**
                 * <pre>
                 *     清空购物车
                 * </pre>
                 */
                appUrlConfig.clearCartInfo="http://"+ip+"/HLDLB/hlyf/api/clearCartInfo";
                /**
                 * <pre>
                 *     提交购物车
                 * </pre>
                 */
                appUrlConfig.commitCartInfo="http://"+ip+"/HLDLB/hlyf/api/commitCartInfo";

                /**
                 * <pre>
                 *     订单同步
                 * </pre>
                 */
                appUrlConfig.orderSysn="http://"+ip+"/HLDLB/hlyf/api/orderSysn";

                /**
                 * <pre>
                 *     检查apk是否需要更新
                 * </pre>
                 */
                appUrlConfig.checkUpdateApk="http://"+ip+"/HLDLB/own/api/checkUpdateApk";
                /**
                 * <pre>
                 *     直接更新的地址
                 * </pre>
                 */
                appUrlConfig.updateApk="http://"+ip+"/HLDLB/selfsupport.apk";

                systemConfig.sn=tDlbSnConfig.getSn();
                systemConfig.cartId=tDlbSnConfig.getCartId();
                systemConfig.cashierNo=getSerialNumber();
                systemConfig.storeId=tDlbSnConfig.getStoreId();
                systemConfig.tenant=tDlbSnConfig.getTenant();
                systemConfig.systemId=getVersion(context);
                systemConfig.storeName=tDlbSnConfig.getSotreName();
                systemConfig.logoImg=tDlbSnConfig.getLogoImg();
                systemConfig.tenantName=tDlbSnConfig.getTenantName();
                systemConfig.remarks=tDlbSnConfig.getRemarks();
                systemConfig.tel=tDlbSnConfig.getTel();
                systemConfig.limit=tDlbSnConfig.getLimit();

                Log.e("MyFactory","我进来了"+tDlbSnConfig.getTenantName());

            }else {
                result=false;
            }
        } catch (Exception e){
            e.printStackTrace();
            result=false;
            Log.e("MyFactory",e.getMessage());
            toast("系统异常"+e.getMessage(),false);
        }
        return result;
    }

    /**
     * <pre>
     *     支付下单的参数封装
     * </pre>
     * @param tradeNo           流水号  商户下不唯一
     * @param merchantOrderId   我们的单号
     * @param amount            金额
     * @param authcode          微信 或 支付宝 授权码
     * @return
     * @throws Exception
     */
    public static String getPayOrder(String tradeNo,String merchantOrderId,
                                     int amount,String authcode) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("bizType","AppPaySplitBill");
        map.put("orderId",merchantOrderId);
        map.put("tradeNo",tradeNo);
        map.put("tenant",systemConfig.tenant);
        map.put("amount",amount);
        map.put("currency","CNY");
        map.put("authcode",authcode);
        map.put("appType","WX");
        map.put("orderIp",systemConfig.sn);
        return CommReq(map);
    }

    /**
     * <pre>
     *     交易查询的参数封装
     * </pre>
     * @param tradeNo
     * @param merchantOrderId
     * @return
     * @throws Exception
     */
    public static String getPayQuery(String tradeNo,String merchantOrderId) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("bizType","AppPayQuery");
        map.put("orderId",merchantOrderId);
        map.put("tradeNo",tradeNo);
        map.put("tenant",systemConfig.tenant);
        return CommReq(map);
    }

    /**
     * <pre>
     *     数据同步的接口
     * </pre>
     * @param payNo
     * @param merchantOrderId
     * @param payTypeId
     * @param payAmount
     * @param goodItems
     * @return
     * @throws Exception
     */
    public static String getSysnOrder(String payNo,String merchantOrderId,
                                      String payTypeId,Long payAmount,String goodItems) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("merchantOrderId",merchantOrderId);
        map.put("payTypeId",payTypeId);
        map.put("payNo",payNo);
        map.put("payAmount",payAmount);
        map.put("cartFlowNo",systemConfig.sn);
        map.put("items",goodItems);
        map.put("storeId",systemConfig.storeId);//
        map.put("cartId",systemConfig.cartId);
        return CommReq(map);
    }

    public static String analysisDataConn(String obj) throws ApiSysException {

        JSONObject jsonObject=null;
        try{
            jsonObject= JSON.parseObject(obj);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MyFactory","数据解析失败 "+e.getMessage());
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
            Log.e("MyFactory","数据验签失败 "+e.getMessage());
            throw new ApiSysException(ErrorEnum.SSCO001004);
        }

        //第三部 数据解码
        try{
            return ThreeDESUtilDLB.decrypt(jsonObject.getString("cipherJson"),
                    systemConfig.deskey,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MyFactory","数据解码失败 "+e.getMessage());
            throw new ApiSysException(ErrorEnum.SSCO001006);
        }


    }
}
