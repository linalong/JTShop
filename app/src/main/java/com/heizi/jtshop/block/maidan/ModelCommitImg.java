package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

/**
 * 评论图片
 * Created by leo on 17/11/10.
 */

public class ModelCommitImg extends BaseModel {
    private String big;//大图
    private String small;//小图

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }
}
