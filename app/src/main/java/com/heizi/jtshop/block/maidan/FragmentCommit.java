package com.heizi.jtshop.block.maidan;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.heizi.jtshop.R;
import com.heizi.jtshop.fragment.BaseFragment;
import com.heizi.jtshop.utils.SelectPhotoUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.StringUtils;
import com.heizi.mycommon.utils.Utils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单评价(单个产品)
 * Created by leo on 17/10/30.
 */

public class FragmentCommit extends BaseFragment {
    View view;
    ImageView iv_product;
    RatingBar rb_star;
    EditText et_remark;
    GridView mGrid;

    ModelProductList modelProductList;
    int position;
    boolean isSuccess = false;//json数据是否拼装成功

    CommitCallback commitCallback;

    private DeviceSignedActionAdapter adapter;
    List<DeviceSignedActionImage> dataList = new ArrayList<>();
    SelectPhotoUtils selectPhotoUtils;

    public void setCallback(CommitCallback commitCallback) {
        this.commitCallback = commitCallback;
    }

    public interface CommitCallback {
        void onsuccess(JSONObject json);

        void onfailed();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.view_commit;
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        position = bundle.getInt("position");
        modelProductList = (ModelProductList) bundle.getSerializable("modelProductList");
        selectPhotoUtils = new SelectPhotoUtils(getActivity(), null, btn_back,
                PHOTO_PICKED_WITH_DATA + position, CAMERA_WITH_DATA + position);
        return super.onCreateView(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        iv_product = (ImageView) view.findViewById(R.id.iv_product);
        rb_star = (RatingBar) view.findViewById(R.id.rb_star);
        et_remark = (EditText) view.findViewById(R.id.et_remark);
        ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
        mGrid = (GridView) view.findViewById(R.id.grid_view_device_signed_action);
        adapter = new DeviceSignedActionAdapter(getActivity());
        adapter.setShow(false);
        mGrid.setAdapter(adapter);
        StringUtils.setProhibitEmoji(et_remark);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position < 3) {
                    DeviceSignedActionImage info = (DeviceSignedActionImage) adapter.getItem(position);
                    if (info.getType() == 1) {
                        selectPhotoUtils.ShowPop1(iv_product);
                    } else {
                        if (info.getIsSusses() == 1) {
                            //重新上传图片
                            dataList.remove(position);
                            selectPhotoUtils.ShowPop1(iv_product);
                        } else if (info.getIsSusses() == 0) {
                            //查看图片
                            Intent intent = new Intent(getActivity(), ActivityImageViewShow.class);
                            //intent.putExtra("url", dataList.get(position));
                            DeviceSignedActionImage itemData = dataList.get(position);
                            String url = "";
                            if (itemData.getLocalUrl() != null) {
                                url = itemData.getLocalUrl();
                            } else if (itemData.getHttpUrl() != null) {
                                url = itemData.getHttpUrl();
                            }
                            intent.putExtra("url", url);
                            getActivity().startActivity(intent);
                        }
                    }
                }
//                else {
//                    showShortToast("不好意思，暂时只能上传5张图片");
//                }
            }
        });
    }


    /**
     * 有图片提交图片,无图片开始拼装json返回
     */
    public void post() {
        if (dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getIsSusses() != 0) {
                    dataList.get(0).setIsSusses(2);
                    upLoadImg(dataList.get(0).getLocalUrl());
                    return;
                }
            }
            upload();
        } else {
            upload();
        }
    }

    /**
     * 返回json数据
     */
    private void upload() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("goods_id", modelProductList.getGoods_id());
            jsonObject.put("score", rb_star.getRating());
            jsonObject.put("comment", et_remark.getText().toString());
            JSONArray jsonArray = new JSONArray();
            if (dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    jsonArray.put(dataList.get(i).getHttpUrl());
                }
            }
            jsonObject.put("images", jsonArray);
            if (commitCallback != null) {
                isSuccess = true;
                commitCallback.onsuccess(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //用requestcode和fragment位置联合识别返回
            if (data != null) {
                if (requestCode == PHOTO_PICKED_WITH_DATA + position) {// 从相册选择图片
                    try {
                        Uri uri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().managedQuery(uri, proj,
                                null, null, null);
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(column_index);
                        loadPictrue(filePath);

                    } catch (Exception e) {
                    }
                }
            }
            if (requestCode == CAMERA_WITH_DATA + position) {// 相机
                String filePath = selectPhotoUtils.getFilePath();
                Uri imgUri = selectPhotoUtils.getImgUri();
                try {
                    if (filePath != null && imgUri != null) {
                        loadPictrue(filePath);
                    }
                } catch (Exception e) {
                }
            }

        }
    }

    public void loadPictrue(final String file) {
        DeviceSignedActionImage uploadImageNew = new DeviceSignedActionImage();
        uploadImageNew.setLocalUrl(file);
        dataList.add(uploadImageNew);
        adapter.setData(dataList);
    }

    /**
     * 上传图片
     */
    private void upLoadImg(String localFilePath) {

        AjaxParams params = new AjaxParams();
        try {
            params.put("imgs", new File(localFilePath));
            params.put("token", userModel.getToken());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FinalHttp fh = new FinalHttp();
        fh.configTimeout(20000);
        fh.post(SERVER_URL_SHOP + "&method=jingtu.orders.addEvaluatesImg.post", params, callBack);
    }


    private AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
        public void onStart() {

        }

        @Override
        public void onSuccess(String json) {
            String result = "";
            try {

                JSONObject jsonObject = new JSONObject(json);
                int code = jsonObject.getInt("code");
                result = jsonObject.getString("result");
                if (code == 1) {
                    Log.d("==", "上传图片返回数据" + json);
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getIsSusses() == 2) {
                            DeviceSignedActionImage deviceSignedActionImage =
                                    dataList.get(i);
                            deviceSignedActionImage.setHttpUrl("" + result);
                            deviceSignedActionImage.setIsSusses(0);
                            break;
                        }
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getIsSusses() != 0) {
                            dataList.get(i).setIsSusses(2);
                            upLoadImg(dataList.get(i).getLocalUrl());
                            return;
                        }
                    }

                    upload();

                } else {
                    LoadingD.hideDialog();
                    Utils.toastShow(getActivity(), "上传图片失败");
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getIsSusses() == 2) {
                            dataList.get(i).setIsSusses(1);
                            break;
                        }
                    }
                    if (commitCallback != null) {
                        commitCallback.onfailed();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        public void onFailure(Throwable t, int errorNo, String strMsg) {
            Utils.toastShow(getActivity(), "上传图片失败");
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getIsSusses() == 2) {
                    dataList.get(i).setIsSusses(1);
                    break;
                }
            }
            if (commitCallback != null) {
                commitCallback.onfailed();
            }
        }
    };

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
