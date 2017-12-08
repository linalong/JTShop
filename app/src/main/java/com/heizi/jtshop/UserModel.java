package com.heizi.jtshop;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 用户
 *
 * @author admin
 */
@Table(name = "user_model", onCreated = "")
public class UserModel implements Serializable {

    public UserModel() {

    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;
    @Column(name = "member_id")
    private String member_id = "";
    @Column(name = "member_name")
    private String member_name = "";
    @Column(name = "member_truename")
    private String member_truename = "";
    @Column(name = "member_birthday")
    private String member_birthday = "";
    @Column(name = "member_sex")
    private String member_sex = "";
    @Column(name = "member_email")
    private String member_email = "";
    @Column(name = "member_avatar")
    private String member_avatar = "";
    @Column(name = "weixin_unionid")
    private String weixin_unionid = "";
    @Column(name = "weixin_info")
    private String weixin_info = "";
    @Column(name = "member_state")
    private String member_state = "";
    @Column(name = "member_mobile")
    private String member_mobile = "";
    @Column(name = "member_passwd")
    private String member_passwd = "";
    @Column(name = "member_paypwd")
    private String member_paypwd = "";//支付密码
    @Column(name = "member_points")
    private String member_points = "";

    @Column(name = "available_predeposit")
    private double available_predeposit;//可用余额

    @Column(name = "inviter_id")
    private String inviter_id = "";
    @Column(name = "token")
    private String token = "";

    //融云
    @Column(name = "rongyun_token")
    private String rongyun_token = "";

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public double getAvailable_predeposit() {
        return available_predeposit;
    }

    public void setAvailable_predeposit(double available_predeposit) {
        this.available_predeposit = available_predeposit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_truename() {
        return member_truename;
    }

    public void setMember_truename(String member_truename) {
        this.member_truename = member_truename;
    }

    public String getMember_birthday() {
        return member_birthday;
    }

    public void setMember_birthday(String member_birthday) {
        this.member_birthday = member_birthday;
    }

    public String getMember_sex() {
        return member_sex;
    }

    public void setMember_sex(String member_sex) {
        this.member_sex = member_sex;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getMember_avatar() {
        return member_avatar;
    }

    public void setMember_avatar(String member_avatar) {
        this.member_avatar = member_avatar;
    }

    public String getWeixin_unionid() {
        return weixin_unionid;
    }

    public void setWeixin_unionid(String weixin_unionid) {
        this.weixin_unionid = weixin_unionid;
    }

    public String getWeixin_info() {
        return weixin_info;
    }

    public void setWeixin_info(String weixin_info) {
        this.weixin_info = weixin_info;
    }

    public String getMember_state() {
        return member_state;
    }

    public void setMember_state(String member_state) {
        this.member_state = member_state;
    }

    public String getMember_mobile() {
        return member_mobile;
    }

    public void setMember_mobile(String member_mobile) {
        this.member_mobile = member_mobile;
    }

    public String getMember_passwd() {
        return member_passwd;
    }

    public void setMember_passwd(String member_passwd) {
        this.member_passwd = member_passwd;
    }

    public String getMember_paypwd() {
        return member_paypwd;
    }

    public void setMember_paypwd(String member_paypwd) {
        this.member_paypwd = member_paypwd;
    }

    public String getMember_points() {
        return member_points;
    }

    public void setMember_points(String member_points) {
        this.member_points = member_points;
    }

    public String getInviter_id() {
        return inviter_id;
    }

    public void setInviter_id(String inviter_id) {
        this.inviter_id = inviter_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRongyun_token() {
        return rongyun_token;
    }

    public void setRongyun_token(String rongyun_token) {
        this.rongyun_token = rongyun_token;
    }
}
