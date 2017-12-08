package com.heizi.jtshop.block.my;

import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * 订单列表和详情model
 * Created by leo on 17/10/20.
 */

public class ModelOrderList extends BaseModel {
    private String order_id;
    private String order_sn;//订单号
    private String pay_sn;//支付单号
    private int order_state;//订单状态
    private String order_message;//订单留言信息
    private String store_id;
    private String store_name;
    private String add_time;//下单时间
    private String payment_time;//订单支付时间
    private String shipping_time;//发货时间
    private String finnshed_time;//订单完成时间
    private String order_amount;//订单实际金额
    private String goods_amount;//产品总金额
    private String goods_num_amount;//订单产品数量
    private double shipping_fee;//运费
    private String shipping_code;//物流单号
    private String payment_name;//支付方式名称
    private String state_desc;//订单状态描述
    private String store_phone;//店铺电话
    private String seller_rongyun_token;//店铺融云id
    private String order_cancel_day;//订单取消时间 待付款用
    private String order_confirm_day;//自动收货剩余时间 待收货用
    //    private String close_info;//订单关闭时间
    private String if_cancel;//是否可取消订单
    private String if_evaluation;//是否评价
    private int evaluation_state;//是否评价 0未评价,1已评价
    private double voucher_price;//优惠金额

    //退款相关
    private int is_refund;//退款状态:0是无退款,1是有退款
    private String refund_amount;//退款金额

    //地址相关
    private String reciver_name;
    private String mob_phone;
    private String address;
    private ArrayList<ModelProductList> order_goods = new ArrayList<>();

    public double getVoucher_price() {
        return voucher_price;
    }

    public void setVoucher_price(double voucher_price) {
        this.voucher_price = voucher_price;
    }

    public String getStore_phone() {
        return store_phone;
    }

    public void setStore_phone(String store_phone) {
        this.store_phone = store_phone;
    }

    public String getSeller_rongyun_token() {
        return seller_rongyun_token;
    }

    public void setSeller_rongyun_token(String seller_rongyun_token) {
        this.seller_rongyun_token = seller_rongyun_token;
    }

    public int getEvaluation_state() {
        return evaluation_state;
    }

    public void setEvaluation_state(int evaluation_state) {
        this.evaluation_state = evaluation_state;
    }

    public String getOrder_cancel_day() {
        return order_cancel_day;
    }

    public void setOrder_cancel_day(String order_cancel_day) {
        this.order_cancel_day = order_cancel_day;
    }

    public String getOrder_confirm_day() {
        return order_confirm_day;
    }

    public void setOrder_confirm_day(String order_confirm_day) {
        this.order_confirm_day = order_confirm_day;
    }


    public String getIf_evaluation() {
        return if_evaluation;
    }

    public void setIf_evaluation(String if_evaluation) {
        this.if_evaluation = if_evaluation;
    }

    public String getGoods_amount() {
        return goods_amount;
    }

    public String getPay_sn() {
        return pay_sn;
    }

    public int getOrder_state() {
        return order_state;
    }

    public int getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(int is_refund) {
        this.is_refund = is_refund;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
    }

    public void setPay_sn(String pay_sn) {
        this.pay_sn = pay_sn;
    }

    public String getShipping_code() {
        return shipping_code;
    }

    public void setShipping_code(String shipping_code) {
        this.shipping_code = shipping_code;
    }

    public String getIf_cancel() {
        return if_cancel;
    }

    public void setIf_cancel(String if_cancel) {
        this.if_cancel = if_cancel;
    }

    public String getPayment_time() {
        return payment_time;
    }

    public void setPayment_time(String payment_time) {
        this.payment_time = payment_time;
    }

    public String getShipping_time() {
        return shipping_time;
    }

    public void setShipping_time(String shipping_time) {
        this.shipping_time = shipping_time;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public void setGoods_amount(String goods_amount) {
        this.goods_amount = goods_amount;
    }

    public String getGoods_num_amount() {
        return goods_num_amount;
    }

    public void setGoods_num_amount(String goods_num_amount) {
        this.goods_num_amount = goods_num_amount;
    }

    public double getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(double shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public String getState_desc() {
        return state_desc;
    }

    public void setState_desc(String state_desc) {
        this.state_desc = state_desc;
    }

    public ArrayList<ModelProductList> getOrder_goods() {
        return order_goods;
    }

    public void setOrder_goods(ArrayList<ModelProductList> order_goods) {
        this.order_goods = order_goods;
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

    public String getOrder_message() {
        return order_message;
    }

    public void setOrder_message(String order_message) {
        this.order_message = order_message;
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

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }


    public String getFinnshed_time() {
        return finnshed_time;
    }

    public void setFinnshed_time(String finnshed_time) {
        this.finnshed_time = finnshed_time;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getReciver_name() {
        return reciver_name;
    }

    public void setReciver_name(String reciver_name) {
        this.reciver_name = reciver_name;
    }

    public String getMob_phone() {
        return mob_phone;
    }

    public void setMob_phone(String mob_phone) {
        this.mob_phone = mob_phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
