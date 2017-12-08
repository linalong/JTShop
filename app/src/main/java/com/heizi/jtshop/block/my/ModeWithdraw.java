package com.heizi.jtshop.block.my;

/**
 * Created by leo on 17/11/7.
 */

public class ModeWithdraw {
    private String pdc_id;//银行卡id
    private String pdc_sn;//用户id
    private String pdc_member_name;//用户名称
    private String pdc_amount;//金额
    private String pdc_bank_name;//银行名称
    private String pdc_bank_no;//银行code
    private String pdc_bank_user;//开户人姓名
    private String pdc_add_time;//
    private int pdc_payment_state;//0：未支付 1：已支付

    public int getPdc_payment_state() {
        return pdc_payment_state;
    }

    public void setPdc_payment_state(int pdc_payment_state) {
        this.pdc_payment_state = pdc_payment_state;
    }

    public String getPdc_id() {
        return pdc_id;
    }

    public void setPdc_id(String pdc_id) {
        this.pdc_id = pdc_id;
    }

    public String getPdc_sn() {
        return pdc_sn;
    }

    public void setPdc_sn(String pdc_sn) {
        this.pdc_sn = pdc_sn;
    }

    public String getPdc_member_name() {
        return pdc_member_name;
    }

    public void setPdc_member_name(String pdc_member_name) {
        this.pdc_member_name = pdc_member_name;
    }

    public String getPdc_amount() {
        return pdc_amount;
    }

    public void setPdc_amount(String pdc_amount) {
        this.pdc_amount = pdc_amount;
    }

    public String getPdc_bank_name() {
        return pdc_bank_name;
    }

    public void setPdc_bank_name(String pdc_bank_name) {
        this.pdc_bank_name = pdc_bank_name;
    }

    public String getPdc_bank_no() {
        return pdc_bank_no;
    }

    public void setPdc_bank_no(String pdc_bank_no) {
        this.pdc_bank_no = pdc_bank_no;
    }

    public String getPdc_bank_user() {
        return pdc_bank_user;
    }

    public void setPdc_bank_user(String pdc_bank_user) {
        this.pdc_bank_user = pdc_bank_user;
    }

    public String getPdc_add_time() {
        return pdc_add_time;
    }

    public void setPdc_add_time(String pdc_add_time) {
        this.pdc_add_time = pdc_add_time;
    }
}
