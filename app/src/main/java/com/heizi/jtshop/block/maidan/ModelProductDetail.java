package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * 商品详情
 * Created by leo on 17/10/16.
 */

public class ModelProductDetail extends BaseModel {
    private String goods_name;
    private String goods_price;
    private String goods_marketprice;
    private String goods_id;
    private int is_favorites;
    private String wap_body;
    private String goods_sales_volume;
    private String commonid_evaluation_count;//评论数量
    private String area_name;//所在地
    private double goods_freight;//邮费
    private String goods_storage;//库存

    private String goods_commonid;//产品组id 获取评论

    private ModelStore store_info = new ModelStore();//店铺信息

    public ModelStore getStore_info() {
        return store_info;
    }

    public String getGoods_commonid() {
        return goods_commonid;
    }

    public void setGoods_commonid(String goods_commonid) {
        this.goods_commonid = goods_commonid;
    }

    public void setStore_info(ModelStore store_info) {
        this.store_info = store_info;
    }

    public String getGoods_storage() {
        return goods_storage;
    }

    public void setGoods_storage(String goods_storage) {
        this.goods_storage = goods_storage;
    }

    public double getGoods_freight() {
        return goods_freight;
    }

    public void setGoods_freight(double goods_freight) {
        this.goods_freight = goods_freight;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
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

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public int getIs_favorites() {
        return is_favorites;
    }

    public void setIs_favorites(int is_favorites) {
        this.is_favorites = is_favorites;
    }

    public String getWap_body() {
        return wap_body;
    }

    public void setWap_body(String wap_body) {
        this.wap_body = wap_body;
    }

    public String getGoods_sales_volume() {
        return goods_sales_volume;
    }

    public void setGoods_sales_volume(String goods_sales_volume) {
        this.goods_sales_volume = goods_sales_volume;
    }

    public String getCommonid_evaluation_count() {
        return commonid_evaluation_count;
    }

    public void setCommonid_evaluation_count(String commonid_evaluation_count) {
        this.commonid_evaluation_count = commonid_evaluation_count;
    }
}
