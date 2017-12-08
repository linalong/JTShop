package com.heizi.jtshop.block.my;

import com.heizi.mycommon.model.BaseModel;


/**
 * 三级联动
 * Created by leo on 17/10/8.
 */

public class ModelCityPicker extends BaseModel {
    private String area_id;
    private String area_name;

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }
}
