package com.heizi.jtshop.block.my;

import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by leo on 17/11/4.
 */

public class ModelRefundList extends BaseModel {
    private String refund_id;
    private String order_id;
    private String order_sn;
    private String refund_sn;
    private String store_id;
    private String store_name;
    private String refund_amount;//退款金额
    private int refund_type;//申请类型:1为退款,2为退货,默认为1
    private String add_time;//添加时间
    private String seller_time;//卖家处理时间
    private String reason_info;//原因内容
    private String buyer_message;//申请原因
    private String refund_state;//申请状态:1为处理中,2为待管理员处理,3为已完成,默认为1
    private String seller_state;//卖家处理状态:1为待审核,2为同意,3为不同意,默认为1
    private String refund_state_des;//退款状态描述
    private ArrayList<ModelProductList> goods_info = new ArrayList<>();

    public String getRefund_state_des() {
        return refund_state_des;
    }

    public void setRefund_state_des(String refund_state_des) {
        this.refund_state_des = refund_state_des;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getRefund_sn() {
        return refund_sn;
    }

    public void setRefund_sn(String refund_sn) {
        this.refund_sn = refund_sn;
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

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public int getRefund_type() {
        return refund_type;
    }

    public void setRefund_type(int refund_type) {
        this.refund_type = refund_type;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getSeller_time() {
        return seller_time;
    }

    public void setSeller_time(String seller_time) {
        this.seller_time = seller_time;
    }

    public String getReason_info() {
        return reason_info;
    }

    public void setReason_info(String reason_info) {
        this.reason_info = reason_info;
    }

    public String getBuyer_message() {
        return buyer_message;
    }

    public void setBuyer_message(String buyer_message) {
        this.buyer_message = buyer_message;
    }

    public String getRefund_state() {
        return refund_state;
    }

    public void setRefund_state(String refund_state) {
        this.refund_state = refund_state;
    }

    public String getSeller_state() {
        return seller_state;
    }

    public void setSeller_state(String seller_state) {
        this.seller_state = seller_state;
    }

    public ArrayList<ModelProductList> getGoods_info() {
        return goods_info;
    }

    public void setGoods_info(ArrayList<ModelProductList> goods_info) {
        this.goods_info = goods_info;
    }
}
