package com.hlyf.selfsupport.domin;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * <pre>
 *     这里和服务器返回的信息是对应的
 * </pre>
 */
@Table(name = "DlbSnConfig")
public class tDlbSnConfig {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;
    //外部商户号
    @Column(name = "tenant")
    private String tenant;
    //购物车id
    @Column(name = "cartId")
    private String cartId;
    @Column(name = "ip")
    private String ip;
    @Column(name = "storeId")
    private String storeId;
    @Column(name = "sotreName")
    private String sotreName;
    //设备号区分 唯一
    @Column(name = "sn")
    private String sn;
    @Column(name = "lineId")
    private int lineId;


    @Column(name = "tel")
    private String tel;

    @Column(name = "address")
    private String address;

    @Column(name = "logoImg")
    private String logoImg;
    @Column(name = "tenantName")
    private String tenantName;
    @Column(name = "remarks")
    private String remarks;


    @Column(name = "remarks")
    private int limit;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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

    public tDlbSnConfig() {
    }

    public tDlbSnConfig(String tenant,
                        String cartId,
                        String ip,
                        String storeId,
                        String sotreName, String sn, int lineId) {
        this.tenant = tenant;
        this.cartId = cartId;
        this.ip = ip;
        this.storeId = storeId;
        this.sotreName = sotreName;
        this.sn = sn;
        this.lineId = lineId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }


    public String getLogoImg() {
        return logoImg;
    }

    public void setLogoImg(String logoImg) {
        this.logoImg = logoImg;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
