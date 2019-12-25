package com.hlyf.selfsupport.domin;

import com.alibaba.fastjson.annotation.JSONField;


import java.math.BigDecimal;

/**
 * Created by Administrator on 2019-07-09.
 */

public class BLBGoodsInfo  {

    private String lineId;
    private String id;
    private String name;
    private long amount;
    private long discountAmount;
    private long basePrice;
    private long price;
    private Integer qty;
    private double weight;
    private Boolean isWeight;
    private String barcode;
    private String unit;
    private Boolean select;

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public long getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(long basePrice) {
        this.basePrice = basePrice;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public double getWeight() {
        return weight;
    }

    public void setIsWeight(Boolean weight) {
        isWeight = weight;
    }

    public Boolean getIsWeight() {
        return isWeight;
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

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BLBGoodsInfo() {

    }

    @Override
    public String toString() {
        return "BLBGoodsInfo{" +
                "lineId='" + lineId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", discountAmount=" + discountAmount +
                ", basePrice=" + basePrice +
                ", price=" + price +
                ", qty=" + qty +
                ", weight=" + weight +
                ", isWeight=" + isWeight +
                ", barcode='" + barcode + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
