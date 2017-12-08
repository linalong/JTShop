package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * 购物车
 * Created by leo on 17/11/8.
 */

public class ModelCartList extends BaseModel {
    private String store_id;
    private String store_name;
    private int delete_state = 1;//1 未选中 2选中
    private ArrayList<ModelProductList> goods_list = new ArrayList<>();

    public int getDelete_state() {
        return delete_state;
    }

    public void setDelete_state(int delete_state) {
        this.delete_state = delete_state;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public ArrayList<ModelProductList> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(ArrayList<ModelProductList> goods_list) {
        this.goods_list = goods_list;
    }
}
