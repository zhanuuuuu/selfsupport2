package com.hlyf.selfsupport.domin;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2019-08-16.
 */
public class SMGGoodsInfo implements Serializable {


    private Long lineId;

    private String openId;

    private String unionId;

    private String storeId;

    private String merchantOrderId;

    private String payOrderId;

    private Integer orderStatus;

    private Integer orderType;

    private String cGoodsNo;

    private String cGoodsName;

    private Double amount;

    private Double discountAmount;

    private Double basePrice;

    private Double price;

    private Integer qty;

    private Double dweight;

    private Boolean isWeight;

    private String barcode;

    private String unit;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date payedTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date showTime;

    private String checkUpNo;

    private String checkUpName;

    private String receivingCode;

    private Double actualAmount;


    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getcGoodsNo() {
        return cGoodsNo;
    }

    public void setcGoodsNo(String cGoodsNo) {
        this.cGoodsNo = cGoodsNo;
    }

    public String getcGoodsName() {
        return cGoodsName;
    }

    public void setcGoodsName(String cGoodsName) {
        this.cGoodsName = cGoodsName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getDweight() {
        return dweight;
    }

    public void setDweight(Double dweight) {
        this.dweight = dweight;
    }

    public Boolean getWeight() {
        return isWeight;
    }

    public void setWeight(Boolean weight) {
        isWeight = weight;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayedTime() {
        return payedTime;
    }

    public void setPayedTime(Date payedTime) {
        this.payedTime = payedTime;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public String getCheckUpNo() {
        return checkUpNo;
    }

    public void setCheckUpNo(String checkUpNo) {
        this.checkUpNo = checkUpNo;
    }

    public String getCheckUpName() {
        return checkUpName;
    }

    public void setCheckUpName(String checkUpName) {
        this.checkUpName = checkUpName;
    }

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public SMGGoodsInfo(String cGoodsName) {
        this.cGoodsName = cGoodsName;
    }

    public SMGGoodsInfo(String openId, String merchantOrderId,
                        String payOrderId, Integer orderType, Integer orderStatus, Double actualAmount) {
        this.openId = openId;
        this.merchantOrderId = merchantOrderId;
        this.payOrderId = payOrderId;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.actualAmount = actualAmount;
    }

    public SMGGoodsInfo(String openId, String unionId) {
        this.openId = openId;
        this.unionId = unionId;
    }

    /**
     *  该构造方法有用
     */
    public SMGGoodsInfo(String openId, String unionId, String storeId,
                        String merchantOrderId, String payOrderId, String cGoodsNo,
                        String cGoodsName, Double amount, Double discountAmount,
                        Double basePrice, Double price,
                        Integer qty, Double dweight,
                        Boolean isWeight, String barcode,
                        String unit, String receivingCode) {
        this.openId = openId;
        this.unionId = unionId;
        this.storeId = storeId;
        this.merchantOrderId = merchantOrderId;
        this.payOrderId = payOrderId;
        this.cGoodsNo = cGoodsNo;
        this.cGoodsName = cGoodsName;
        this.amount = amount;
        this.discountAmount = discountAmount;
        this.basePrice = basePrice;
        this.price = price;
        this.qty = qty;
        this.dweight = dweight;
        this.isWeight = isWeight;
        this.barcode = barcode;
        this.unit = unit;
        this.receivingCode = receivingCode;
    }

    public SMGGoodsInfo(String openId, String unionId, String storeId,
                        String merchantOrderId, String payOrderId, String cGoodsNo,
                        String cGoodsName, Double amount, Double discountAmount,
                        Double basePrice, Double price,
                        Integer qty, Double dweight,
                        Boolean isWeight, String barcode,
                        String unit, String receivingCode,Date showTime) {
        this.openId = openId;
        this.unionId = unionId;
        this.storeId = storeId;
        this.merchantOrderId = merchantOrderId;
        this.payOrderId = payOrderId;
        this.cGoodsNo = cGoodsNo;
        this.cGoodsName = cGoodsName;
        this.amount = amount;
        this.discountAmount = discountAmount;
        this.basePrice = basePrice;
        this.price = price;
        this.qty = qty;
        this.dweight = dweight;
        this.isWeight = isWeight;
        this.barcode = barcode;
        this.unit = unit;
        this.receivingCode = receivingCode;
        this.showTime=showTime;
    }
}
