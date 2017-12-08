package com.heizi.jtshop.block.pano;

import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * 首页全景model
 * Created by leo on 17/6/2.
 */

public class ModelPano extends BaseModel {

    private String id;
    private String store_name;
    private String store_id;
    private String city_id;
    private String area_info;
    private String preview_img;
    private String description;
    private String title;
    private String pano_url;
    private String latitude;
    private String longitude;
    private String is_favorites;

    public String getIs_favorites() {
        return is_favorites;
    }

    public void setIs_favorites(String is_favorites) {
        this.is_favorites = is_favorites;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int delete_state = 1;//是否需要删除收藏

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getDelete_state() {
        return delete_state;
    }

    public void setDelete_state(int delete_state) {
        this.delete_state = delete_state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }


    public String getArea_info() {
        return area_info;
    }

    public void setArea_info(String area_info) {
        this.area_info = area_info;
    }

    public String getPreview_img() {
        return preview_img;
    }

    public void setPreview_img(String preview_img) {
        this.preview_img = preview_img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPano_url() {
        return pano_url;
    }

    public void setPano_url(String pano_url) {
        this.pano_url = pano_url;
    }

}
