package com.heizi.jtshop.block.my;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.utils.RefreshUtils;
import com.heizi.jtshop.utils.SelectPhotoUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by leo on 17/6/24.
 */

public class ActivityUserInfo extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.iv_avatar)
    ImageView iv_avatar;
    @InjectView(R.id.et_name)
    TextView et_name;

    private ParseStringProtocol protocolEditName;
    private IResponseCallback<DataSourceModel<String>> callbackEditName;


    String userNameTmp = "";
    //本地图片路径,服务器图片路径
    private String localFilePath = "", img_url = "";
    SelectPhotoUtils selectPhotoUtils;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectPhotoUtils = new SelectPhotoUtils(this, null, btn_back,
                PHOTO_PICKED_WITH_DATA, CAMERA_WITH_DATA);
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("我的信息");
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("保存");
        et_name.setText(userModel.getMember_name());
        if (!TextUtils.isEmpty(userModel.getMember_avatar())) {
            ImageFactory.displayImage(userModel.getMember_avatar(), iv_avatar, R.mipmap.photo, R.mipmap.photo);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        protocolEditName = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.user_center.editMemberInfo.post/");
        callbackEditName = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                userModel.setMember_name(userNameTmp);
                application.setUserModel(userModel);
                RefreshUtils.sendBroadcast(ActivityUserInfo.this, userModel);
                Utils.toastShow(ActivityUserInfo.this, "昵称修改成功!").show();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityUserInfo.this);
            }
        };


    }

    /**
     * 上传图片
     */
    private void upLoadImg(String localFilePath) {

        AjaxParams params = new AjaxParams();
        try {
            params.put("member_avatar", new File(localFilePath));
            params.put("token", userModel.getToken());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FinalHttp fh = new FinalHttp();
        fh.configTimeout(20000);
        fh.post(SERVER_URL_SHOP + "&method=jingtu.user_center.editMemberAvatar.post/", params, new AjaxCallBack<String>() {
            public void onStart() {
                LoadingD.showDialog(ActivityUserInfo.this);
            }

            @Override
            public void onSuccess(String json) {
                LoadingD.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                    userModel.setMember_avatar(jsonObject1.getString("member_avatar"));
                    application.setUserModel(userModel);
                    RefreshUtils.sendBroadcast(ActivityUserInfo.this, userModel);
                    Utils.toastShow(ActivityUserInfo.this, "头像修改成功!").show();
                    Log.d("==", "上传图片成功:" + json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onFailure(Throwable t, int errorNo, String strMsg) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityUserInfo.this, "头像修改失败");
                Log.d("==", "上传图片失败:" + strMsg);
            }
        });
    }

    @OnClick({R.id.tv_right, R.id.btn_back, R.id.iv_avatar})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_right:
                if (!userModel.getMember_name().equals(et_name.getText().toString())) {
                    userNameTmp = et_name.getText().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("token", userModel.getToken());
                    map.put("member_name", et_name.getText().toString());
                    protocolEditName.postData(map, callbackEditName);
                }
                break;
            case R.id.iv_avatar:
                selectPhotoUtils.ShowPop1(btn_back);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == PHOTO_PICKED_WITH_DATA) {// 从相册选择图片
                    try {
                        Uri uri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(uri, proj,
                                null, null, null);
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(column_index);
                        localFilePath = filePath;
                        File file = new File(filePath);

                        ImageFactory.displayImage("file://" + file, iv_avatar, R.mipmap.photo, R.mipmap.photo);
                        upLoadImg(localFilePath);

                    } catch (Exception e) {
                    }
                }
            }
            if (requestCode == CAMERA_WITH_DATA) {// 相机
                String filePath = selectPhotoUtils.getFilePath();
                Uri imgUri = selectPhotoUtils.getImgUri();
                try {
                    if (filePath != null && imgUri != null) {
                        File file = new File(filePath);
                        localFilePath = filePath;
                        ImageFactory.displayImage("file://" + file, iv_avatar, R.mipmap.photo, R.mipmap.photo);
                        upLoadImg(localFilePath);
                    }
                } catch (Exception e) {
                }
            }

        }
    }
}
