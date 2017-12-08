package com.heizi.jtshop.block.maidan;

import com.heizi.mycommon.model.BaseModel;

import java.util.ArrayList;

/**
 * 评论model
 * Created by leo on 17/11/10.
 */

public class ModelCommit extends BaseModel {

    private String geval_id;
    private String geval_frommembername;
    private String geval_frommemberavatar;
    private String geval_addtime;
    private String spec;//尺码
    private String geval_content;
    private ArrayList<ModelCommitImg> geval_image = new ArrayList<>();

    public String getGeval_content() {
        return geval_content;
    }

    public void setGeval_content(String geval_content) {
        this.geval_content = geval_content;
    }

    public String getGeval_id() {
        return geval_id;
    }

    public void setGeval_id(String geval_id) {
        this.geval_id = geval_id;
    }

    public String getGeval_frommembername() {
        return geval_frommembername;
    }

    public void setGeval_frommembername(String geval_frommembername) {
        this.geval_frommembername = geval_frommembername;
    }

    public String getGeval_frommemberavatar() {
        return geval_frommemberavatar;
    }

    public void setGeval_frommemberavatar(String geval_frommemberavatar) {
        this.geval_frommemberavatar = geval_frommemberavatar;
    }

    public String getGeval_addtime() {
        return geval_addtime;
    }

    public void setGeval_addtime(String geval_addtime) {
        this.geval_addtime = geval_addtime;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public ArrayList<ModelCommitImg> getGeval_image() {
        return geval_image;
    }

    public void setGeval_image(ArrayList<ModelCommitImg> geval_image) {
        this.geval_image = geval_image;
    }
}
