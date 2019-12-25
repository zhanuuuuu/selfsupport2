package com.hlyf.selfsupport.domin;

public class Request {

    private String merchantNo;
    private String tenant;
    private String storeId;
    private String cipherJson;
    private String sign;
    private String systemId;
    private String uuid;

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCipherJson() {
        return cipherJson;
    }

    public void setCipherJson(String cipherJson) {
        this.cipherJson = cipherJson;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Request() {

    }

    public Request(String merchantNo, String tenant, String storeId, String cipherJson, String sign, String systemId, String uuid) {
        this.merchantNo = merchantNo;
        this.tenant = tenant;
        this.storeId = storeId;
        this.cipherJson = cipherJson;
        this.sign = sign;
        this.systemId = systemId;
        this.uuid = uuid;
    }
}
