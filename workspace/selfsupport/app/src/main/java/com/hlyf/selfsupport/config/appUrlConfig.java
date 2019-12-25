package com.hlyf.selfsupport.config;

/**
 * 配置类
 */
public class appUrlConfig {


    public static String ip="";
    /**
     * <pre>
     *     会员查询
     * </pre>
     */
    public static String selectMemberInfo="http://"+ip+"/HLDLB/hlyf/api/selectMemberInfo";
    /**
     * <pre>
     *     商品查询
     * </pre>
     */
    public static String selectGoods="http://"+ip+"/HLDLB/hlyf/api/selectGoods";
    /**
     * <pre>
     *     更改商品数量
     * </pre>
     */
    public static String updateGoods="http://"+ip+"/HLDLB/hlyf/api/updateGoods";
    /**
     * <pre>
     *     删除商品
     * </pre>
     */
    public static String deleteGoods="http://"+ip+"/HLDLB/hlyf/api/deleteGoods";
    /**
     * <pre>
     *     清空购物车
     * </pre>
     */
    public static String clearCartInfo="http://"+ip+"/HLDLB/hlyf/api/clearCartInfo";
    /**
     * <pre>
     *     提交购物车
     * </pre>
     */
    public static String commitCartInfo="http://"+ip+"/HLDLB/hlyf/api/commitCartInfo";

    /**
     * <pre>
     *     订单同步
     * </pre>
     */
    public static String orderSysn="http://"+ip+"/HLDLB/hlyf/api/orderSysn";

    /**
     * <pre>
     *     检查apk是否需要更新
     * </pre>
     */
    public static String checkUpdateApk="http://"+ip+"/HLDLB/hlyf/api/checkUpdateApk";

    /**
     * <pre>
     *     下载apk的
     * </pre>
     */
    public static String updateApk="http://"+ip+"/HLDLB/selfsupport.apk";
    
    /**
     * <pre>
     *     支付下单 192.168.11.242:7788
     *
     * </pre>
     */
    public static String payOrder="http://dlb.warelucent.cc:7788/DLB/hlyf/api/pay/payOrder";

    /**
     * <pre>
     *     查询支付结果  dlb.warelucent.cc:7788 正式的地址
     * </pre>
     */
    public static String queryPayOrder="http://dlb.warelucent.cc:7788/DLB/hlyf/api/pay/queryPayOrder";










}
