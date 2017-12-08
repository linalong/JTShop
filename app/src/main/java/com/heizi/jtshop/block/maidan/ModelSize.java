package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * Created by leo on 17/10/3.
 */

public class ModelSize extends BaseModel {
    private int id;
    private String value;
    private int state = 0;//0未选中  1选中

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
