package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by leo on 17/10/16.
 */

public class ModelProductList extends BaseModel {
    private String goods_id;
    private String goods_salenum;
    private String goods_num;
    private String goods_name;
    private String goods_price;
    private String goods_marketprice;
    private String goods_image_url;
    private String store_name;
    private float goods_freight;//运费
    private ArrayList<String> spec_info = new ArrayList<>();
    private String refund_state_des;//退款描述
    private String refund_amount;//退款金额
    private int is_refund;//退款状态:0是无退款,1是有退款

    private int delete_state = 1;//1 未选中 2选中
    private String cart_id;//购物车id

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public int getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(int is_refund) {
        this.is_refund = is_refund;
    }

    public String getRefund_state_des() {
        return refund_state_des;
    }

    public void setRefund_state_des(String refund_state_des) {
        this.refund_state_des = refund_state_des;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(String goods_num) {
        this.goods_num = goods_num;
    }

    public ArrayList<String> getSpec_info() {
        return spec_info;
    }

    public void setSpec_info(ArrayList<String> spec_info) {
        this.spec_info = spec_info;
    }

    public String getStore_name() {
        return store_name;
    }

    public float getGoods_freight() {
        return goods_freight;
    }

    public void setGoods_freight(float goods_freight) {
        this.goods_freight = goods_freight;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public int getDelete_state() {
        return delete_state;
    }

    public void setDelete_state(int delete_state) {
        this.delete_state = delete_state;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_salenum() {
        return goods_salenum;
    }

    public void setGoods_salenum(String goods_salenum) {
        this.goods_salenum = goods_salenum;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_marketprice() {
        return goods_marketprice;
    }

    public void setGoods_marketprice(String goods_marketprice) {
        this.goods_marketprice = goods_marketprice;
    }

    public String getGoods_image_url() {
        return goods_image_url;
    }

    public void setGoods_image_url(String goods_image_url) {
        this.goods_image_url = goods_image_url;
    }
}
