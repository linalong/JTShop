package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * 确认订单中,一条商品的详细信息
 * Created by leo on 17/10/17.
 */

public class ModelOrderProductList extends BaseModel {

    private String goods_num;
    private String goods_id;
    private String goods_name;
    private String goods_price;
    private String goods_image_url;
    private String spec_info;//规格

    public String getSpec_info() {
        return spec_info;
    }

    public void setSpec_info(String spec_info) {
        this.spec_info = spec_info;
    }

    public String getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(String goods_num) {
        this.goods_num = goods_num;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
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

    public String getGoods_image_url() {
        return goods_image_url;
    }

    public void setGoods_image_url(String goods_image_url) {
        this.goods_image_url = goods_image_url;
    }
}
