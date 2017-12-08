package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * 店铺信息
 * Created by leo on 17/11/10.
 */

public class ModelStore extends BaseModel {
    private String seller_name;
    private String vr_url;
    private String store_id;
    private String seller_id;
    private String seller_rongyun_token;
    private String store_name;

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getVr_url() {
        return vr_url;
    }

    public void setVr_url(String vr_url) {
        this.vr_url = vr_url;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_rongyun_token() {
        return seller_rongyun_token;
    }

    public void setSeller_rongyun_token(String seller_rongyun_token) {
        this.seller_rongyun_token = seller_rongyun_token;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
}
