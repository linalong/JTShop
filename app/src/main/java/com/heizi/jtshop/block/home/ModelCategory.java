package com.heizi.jtshop.block.home;


import com.heizi.mycommon.model.BaseModel;

/**
 * Created by leo on 16/11/3.
 */

public class ModelCategory extends BaseModel {
    String gc_id = "";
    String gc_thumb = "";
    String gc_name = "";

    public String getGc_id() {
        return gc_id;
    }

    public void setGc_id(String gc_id) {
        this.gc_id = gc_id;
    }

    public String getGc_thumb() {
        return gc_thumb;
    }

    public void setGc_thumb(String gc_thumb) {
        this.gc_thumb = gc_thumb;
    }

    public String getGc_name() {
        return gc_name;
    }

    public void setGc_name(String gc_name) {
        this.gc_name = gc_name;
    }
}
