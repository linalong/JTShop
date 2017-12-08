package com.heizi.jtshop.block.my;

import com.heizi.mycommon.model.BaseModel;

/**
 * 银行卡model
 * Created by leo on 17/11/3.
 */

public class ModelBank extends BaseModel {
    private String pdc_id;//银行卡id
    private String pdc_member_id;//用户id
    private String pdc_member_name;//用户名称
    private String pdc_user_name;//开户人姓名
    private String pdc_bank_code;//银行code
    private String pdc_bank_name;//银行名称
    private String pdc_bank_branch_name;//开户行
    private String pdc_bank_no;//银行卡号
    private String pdc_province_id;//银行开户省id
    private String pdc_city_id;//银行开户城市id
    private String pdc_city_area;//银行区
    private String pdc_area_info;//省市区信息集合
    private String pdc_add_time;//绑定时间
    private String bank_logo;//银行logo

    public String getBank_logo() {
        return bank_logo;
    }

    public void setBank_logo(String bank_logo) {
        this.bank_logo = bank_logo;
    }

    public String getPdc_area_info() {
        return pdc_area_info;
    }

    public void setPdc_area_info(String pdc_area_info) {
        this.pdc_area_info = pdc_area_info;
    }

    public String getPdc_id() {
        return pdc_id;
    }

    public void setPdc_id(String pdc_id) {
        this.pdc_id = pdc_id;
    }

    public String getPdc_member_id() {
        return pdc_member_id;
    }

    public void setPdc_member_id(String pdc_member_id) {
        this.pdc_member_id = pdc_member_id;
    }

    public String getPdc_member_name() {
        return pdc_member_name;
    }

    public void setPdc_member_name(String pdc_member_name) {
        this.pdc_member_name = pdc_member_name;
    }

    public String getPdc_user_name() {
        return pdc_user_name;
    }

    public void setPdc_user_name(String pdc_user_name) {
        this.pdc_user_name = pdc_user_name;
    }

    public String getPdc_bank_code() {
        return pdc_bank_code;
    }

    public void setPdc_bank_code(String pdc_bank_code) {
        this.pdc_bank_code = pdc_bank_code;
    }

    public String getPdc_bank_name() {
        return pdc_bank_name;
    }

    public void setPdc_bank_name(String pdc_bank_name) {
        this.pdc_bank_name = pdc_bank_name;
    }

    public String getPdc_bank_branch_name() {
        return pdc_bank_branch_name;
    }

    public void setPdc_bank_branch_name(String pdc_bank_branch_name) {
        this.pdc_bank_branch_name = pdc_bank_branch_name;
    }

    public String getPdc_bank_no() {
        return pdc_bank_no;
    }

    public void setPdc_bank_no(String pdc_bank_no) {
        this.pdc_bank_no = pdc_bank_no;
    }

    public String getPdc_province_id() {
        return pdc_province_id;
    }

    public void setPdc_province_id(String pdc_province_id) {
        this.pdc_province_id = pdc_province_id;
    }

    public String getPdc_city_id() {
        return pdc_city_id;
    }

    public void setPdc_city_id(String pdc_city_id) {
        this.pdc_city_id = pdc_city_id;
    }

    public String getPdc_city_area() {
        return pdc_city_area;
    }

    public void setPdc_city_area(String pdc_city_area) {
        this.pdc_city_area = pdc_city_area;
    }

    public String getPdc_add_time() {
        return pdc_add_time;
    }

    public void setPdc_add_time(String pdc_add_time) {
        this.pdc_add_time = pdc_add_time;
    }
}
