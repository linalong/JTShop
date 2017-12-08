package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * 确认订单
 * Created by leo on 17/10/17.
 */

public class ModelOrderConfirm extends BaseModel {
    private String freight_hash;//快递参数
    private double order_freight;//运费
    private String vat_hash;//发票信息hash
    private String store_name;//店铺名
    private String store_goods_num;//店铺购买数量
    private String store_id;//店铺id
    private double goods_total;//订单总金额

    public String getVat_hash() {
        return vat_hash;
    }

    public void setVat_hash(String vat_hash) {
        this.vat_hash = vat_hash;
    }

    public String getStore_goods_num() {
        return store_goods_num;
    }

    public void setStore_goods_num(String store_goods_num) {
        this.store_goods_num = store_goods_num;
    }

    public String getFreight_hash() {
        return freight_hash;
    }

    public void setFreight_hash(String freight_hash) {
        this.freight_hash = freight_hash;
    }

    public double getOrder_freight() {
        return order_freight;
    }

    public void setOrder_freight(double order_freight) {
        this.order_freight = order_freight;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public double getGoods_total() {
        return goods_total;
    }

    public void setGoods_total(double goods_total) {
        this.goods_total = goods_total;
    }
}
