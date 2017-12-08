package com.heizi.jtshop.block.pano;

import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.utils.Utils;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.WebViewUtil;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.SYSTEM_UI_FLAG_VISIBLE;
import static com.heizi.jtshop.R.id.parent_layout;


/**
 * Created by leo on 17/6/3.
 */

public class ActivityPanoDetail extends BaseSwipeBackCompatActivity implements View.OnClickListener {
    WebView webView;
    LinearLayout parent_layout;
    private String url = "", pano_id, title = "", des = "", img = "";
    private int state = 0;//0 显示收藏  1显示已收藏

    //添加收藏
    private ParseStringProtocol protocolCollect;
    private IResponseCallback<DataSourceModel<String>> callbackCollect;
    //判断是否收藏
    private ParseStringProtocol protocolIsCollect;
    private IResponseCallback<DataSourceModel<String>> callbackIsCollect;
    //取消收藏
    private ParseStringProtocol protocolUnCollect;
    private IResponseCallback<DataSourceModel<String>> callbackUnCollect;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            title_include.setVisibility(View.VISIBLE);
            parent_layout.setSystemUiVisibility(SYSTEM_UI_FLAG_VISIBLE);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            title_include.setVisibility(View.GONE);
            parent_layout.setSystemUiVisibility(INVISIBLE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pano_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("url")))
            url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("title")))
            title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("des")))
            des = getIntent().getStringExtra("des");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("des")))
            img = getIntent().getStringExtra("img");
        pano_id = getIntent().getStringExtra("pano_id");
        webView = (WebView) findViewById(R.id.webview);
        parent_layout= (LinearLayout) findViewById(R.id.parent_layout);
        tv_title.setText("全景展示");
        btn_close.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(title)) {
            tv_right1.setVisibility(View.VISIBLE);
            tv_right.setVisibility(View.VISIBLE);
        }
        tv_right1.setText("收藏");
        tv_right.setText("分享");
        WebViewUtil util = new WebViewUtil(this);
        util.setView(webView);
        webView.loadUrl(url);
    }

    @Override
    protected void initData() {
        super.initData();
        protocolCollect = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.addVrFavorites.post/");
        callbackCollect = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Toast.makeText(ActivityPanoDetail.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                tv_right1.setText("已收藏");
                tv_right1.setTextColor(getResources().getColor(R.color.yellow1));
                state = 1;
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                Toast.makeText(ActivityPanoDetail.this, errorModel.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityPanoDetail.this);
            }
        };
        protocolUnCollect = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.delBatchFavorites.post/");
        callbackUnCollect = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Toast.makeText(ActivityPanoDetail.this, "取消收藏成功!", Toast.LENGTH_SHORT).show();
                tv_right1.setText("收藏");
                tv_right1.setTextColor(getResources().getColor(R.color.white));
                state = 0;
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityPanoDetail.this);
            }
        };
        protocolIsCollect = new ParseStringProtocol(this, SERVER_URL_SHOP + IFCOLLECTEDVR);
        callbackIsCollect = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    state=Integer.parseInt(data.json);
                    if (state==0) {
                        tv_right1.setText("收藏");
                        tv_right1.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        tv_right1.setText("已收藏");
                        tv_right1.setTextColor(getResources().getColor(R.color.yellow1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
            }

            @Override
            public void onStart() {
            }
        };

        if (userModel != null)
            requestIsCollect();

    }

    private void requestCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("vr_id", pano_id);
        protocolCollect.postData(map, callbackCollect);
    }

    private void requestUnCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("fav_ids", pano_id);
        map.put("type", "store");
        protocolUnCollect.postData(map, callbackUnCollect);
    }

    private void requestIsCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("vr_id", pano_id);
        protocolIsCollect.getData(map, callbackIsCollect);
    }


    @OnClick({R.id.tv_right, R.id.tv_right1, R.id.btn_back, R.id.btn_close})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.btn_close:
                finish();
                break;
            case R.id.tv_right1:
                if (Utils.checkLogin(this, userModel)) {
                    if (state == 0) {
                        requestCollect();
                    } else {
                        requestUnCollect();
                    }
                }

                break;
            case R.id.tv_right:

//                Bitmap bitmap = ImageFactory.loadBitmap(img, 0, 0);
//                if (WechatShareManager.isWebchatAvaliable(ActivityPanoDetail.this)) {
//                    WechatShareManager.ShareContentWebpage mShareContentWebpage = null;
//                    mShareContentWebpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(title, des, url, 0, bitmap);
//                    mShareManager.shareByWebchat(mShareContentWebpage, WechatShareManager.WECHAT_SHARE_TYPE_TALK);
//                } else {
//                    Toast.makeText(ActivityPanoDetail.this, "请先安装微信", Toast.LENGTH_LONG).show();
//                }
                break;
        }
    }

    @Override
    protected void onPause() {
//        webView.reload();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LoadingD.hideDialog();
        webView.destroy();
        super.onDestroy();
    }
}
