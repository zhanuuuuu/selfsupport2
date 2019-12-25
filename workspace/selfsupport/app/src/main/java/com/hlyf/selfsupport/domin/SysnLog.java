package com.hlyf.selfsupport.domin;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 *
 */
@Table(name = "SysnLog")
public class SysnLog {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    //门店电话
    @Column(name = "tel")
    private  String tel;

    //门店地址
    @Column(name = "address")
    private  String address;

    //门店编号
    @Column(name = "storeId")
    private String storeId;

    //门店名称
    @Column(name = "sotreName")
    private String sotreName;

    //商品数量
    @Column(name = "number")
    private Integer number;

    //单号
    @Column(name = "merchantOrderId")
    private String merchantOrderId;

    //小票号
    @Column(name = "payOrderId")
    private String payOrderId;

    //支付方式
    @Column(name = "payType")
    private String payType;

    //销售时间
    @Column(name = "createTime")
    private String createTime;

    //销售金额
    @Column(name = "money")
    private String money;

    //销售数据
    @Column(name = "itemData")
    private String itemData;

    public SysnLog() {
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public SysnLog(int id, String tel, String address, String storeId, String sotreName,
                   Integer number, String merchantOrderId, String createTime, String money, String itemData) {
        this.id = id;
        this.tel = tel;
        this.address = address;
        this.storeId = storeId;
        this.sotreName = sotreName;
        this.number = number;
        this.merchantOrderId = merchantOrderId;
        this.createTime = createTime;
        this.money = money;
        this.itemData = itemData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSotreName() {
        return sotreName;
    }

    public void setSotreName(String sotreName) {
        this.sotreName = sotreName;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getItemData() {
        return itemData;
    }

    public void setItemData(String itemData) {
        this.itemData = itemData;
    }
}
