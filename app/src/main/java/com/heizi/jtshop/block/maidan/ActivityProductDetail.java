package com.heizi.jtshop.block.maidan;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.home.ActivityStore;
import com.heizi.jtshop.block.home.ModelHome;
import com.heizi.jtshop.block.home.PagerAdapterHome;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.mycommon.utils.DipPixUtils;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mycommon.utils.WebViewUtil;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;


/**
 * 商品详情
 * Created by leo on 17/10/3.
 */

public class ActivityProductDetail extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.ll_size)
    LinearLayout ll_size;
    @InjectView(R.id.ll_commit)
    LinearLayout ll_commit;
    @InjectView(R.id.tv_commit)
    TextView tv_commit;
    @InjectView(R.id.tv_price)
    TextView tv_price;
    @InjectView(R.id.tv_name)
    TextView tv_name;
    @InjectView(R.id.tv_peisong)
    TextView tv_peisong;
    @InjectView(R.id.tv_xiaoliang)
    TextView tv_xiaoliang;
    @InjectView(R.id.tv_local)
    TextView tv_local;
    @InjectView(R.id.tv_size)
    TextView tv_size;
    @InjectView(R.id.tv_kefu)
    TextView tv_kefu;
    @InjectView(R.id.tv_store)
    TextView tv_store;
    @InjectView(R.id.tv_collect)
    TextView tv_collect;

    @InjectView(R.id.webview)
    WebView webview;

    @InjectView(R.id.btn_cart)
    Button btn_cart;
    @InjectView(R.id.btn_ok)
    Button btn_ok;

    //viewpager_top
    @InjectView(R.id.vp_top)
    ViewPager vp_top;
    @InjectView(R.id.pointlayout)
    LinearLayout pointlayout;
    private List<ModelHome> dataPager = new ArrayList<>();
    private List<ImageView> listImg = new ArrayList<ImageView>();
    private PagerAdapterHome pagerAdapterHome;

    ViewSize viewSize;
    /**
     * 尺码实体
     * modelSize不为1时必须选择才能购买 一个modesize对应一个产品id
     */
    ModelSize modelSize;

    int num = 1;

    private String goods_id = "";
    ModelProductDetail modelProductDetail;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        goods_id = getIntent().getExtras().getString("goods_id");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        btn_back.setOnClickListener(this);
        viewSize = new ViewSize(this);
        viewSize.setCallback(new ViewSize.ViewSizeCallback() {
            @Override
            public void call(ModelSize model, int n) {
                modelSize = model;
                num = n;
                tv_size.setText("已选择: \"" + modelSize.getValue() + "\"  *" + num);
            }
        });

        //轮播图
        pagerAdapterHome = new PagerAdapterHome(this);
        vp_top.setAdapter(pagerAdapterHome);
    }

    @Override
    protected void initData() {
        super.initData();
        getData();
    }

    //viewpager填充
    private void showDataPager(List<ModelHome> list) {

        if (list.size() != listImg.size()) {
            pointlayout.removeAllViews();
            listImg.removeAll(listImg);
            for (int i = 0; i < list.size(); i++) {
                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        15, 15);
                img.setLayoutParams(lp);

                if (i == 0)
                    img.setBackgroundResource(R.drawable.point_home_normal_select);
                else
                    img.setBackgroundResource(R.drawable.point_home_normal);

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        15, 15);
                lp1.setMargins(0, 0, DipPixUtils.dip2px(this, 5), 0);
                pointlayout.addView(img, lp1);
                listImg.add(img);
            }

        }

        //一个时不显示圆点
        if (list.size() == 1) {
            pointlayout.setVisibility(View.GONE);
        }

        pagerAdapterHome.setDatas(list);
        vp_top.setCurrentItem(0);
        vp_top.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                setdefault(listImg);
                if (position < listImg.size())
                    listImg.get(position).setBackgroundResource(
                            R.drawable.point_home_normal_select);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setdefault(List<ImageView> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setBackgroundResource(R.drawable.point_home_normal);
        }
    }

    private void getData() {
        ParseStringProtocol protocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.goods.goodsInfo.get/");
        final Map<String, String> maps = new HashMap<>();
        maps.put("goods_id", goods_id);
        if (userModel != null) {
            maps.put("token", userModel.getToken());
        }
        protocol.getData(maps, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
                LoadingD.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(data.json);
                    modelProductDetail = new ModelProductDetail();
                    modelProductDetail.setGoods_name(jsonObject.getString("goods_name"));
                    modelProductDetail.setGoods_id(jsonObject.getString("goods_id"));
                    modelProductDetail.setCommonid_evaluation_count(jsonObject.getString("commonid_evaluation_count"));
                    modelProductDetail.setGoods_price(jsonObject.getString("goods_price"));
                    modelProductDetail.setGoods_marketprice(jsonObject.getString("goods_marketprice"));
                    modelProductDetail.setIs_favorites(jsonObject.getInt("is_favorites"));
                    modelProductDetail.setGoods_sales_volume(jsonObject.getString("goods_sales_volume"));
                    modelProductDetail.setWap_body(jsonObject.getString("wap_body"));
                    modelProductDetail.setArea_name(jsonObject.getString("area_name"));
                    modelProductDetail.setGoods_freight(jsonObject.getDouble("goods_freight"));
                    modelProductDetail.setGoods_storage(jsonObject.getString("goods_storage"));
                    modelProductDetail.setGoods_commonid(jsonObject.getString("goods_commonid"));
                    Gson gson = new Gson();
                    ModelStore modelStore = gson.fromJson(jsonObject.getString("store_info"),
                            ModelStore.class);

                    modelProductDetail.setStore_info(modelStore);

                    //设置轮播
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_images"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelHome modelHome = new ModelHome();
                        modelHome.setAdv_img(jsonArray.getJSONObject(i).getString("imageUrl"));
                        dataPager.add(modelHome);
                    }

                    //设置尺码
                    JSONArray jsonSpec = new JSONArray(jsonObject.getString("spec_arr"));
                    ArrayList<ModelSize> list = new ArrayList<>();
                    for (int i = 0; i < jsonSpec.length(); i++) {
                        ModelSize modelHome = new ModelSize();
                        modelHome.setId(jsonSpec.getJSONObject(i).getInt("spec_id"));
                        modelHome.setValue(jsonSpec.getJSONObject(i).getString("spec_name"));
                        list.add(modelHome);
                        if (jsonSpec.getJSONObject(i).getInt("spec_id") == Integer.parseInt(modelProductDetail.getGoods_id())) {
                            modelSize = modelHome;
                            modelHome.setState(1);
                        }
                    }

                    //activity 提前被销毁
                    if (isExist) {
                        tv_size.setText("已选择: \"" + modelSize.getValue() + "\"  *" + num);
                        viewSize.setData(list);
                        viewSize.setGoods_storage(modelProductDetail.getGoods_storage());
                        viewSize.setImgurl(jsonArray.getJSONObject(0).getString("imageUrl"));
                        viewSize.setPrice(modelProductDetail.getGoods_price());

                        showDataPager(dataPager);
                        if (modelProductDetail.getIs_favorites() == 1) {
                            Drawable drawable = getResources().getDrawable(R.mipmap.iv_shoucang_on);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect.setCompoundDrawables(null, drawable, null, null);
                            tv_collect.setText("已收藏");
                        } else {
                            Drawable drawable = getResources().getDrawable(R.mipmap.iv_shoucang_off);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tv_collect.setCompoundDrawables(null, drawable, null, null);
                            tv_collect.setText("收藏");
                        }

                        if (modelProductDetail.getGoods_freight() == 0) {
                            tv_peisong.setText("快递:免邮");
                        } else {
                            tv_peisong.setText("快递:" + modelProductDetail.getGoods_freight());
                        }

                        tv_local.setText(modelProductDetail.getArea_name());
                        tv_name.setText(modelProductDetail.getGoods_name());
                        tv_commit.setText(modelProductDetail.getCommonid_evaluation_count());
                        tv_price.setText(modelProductDetail.getGoods_price());
                        tv_xiaoliang.setText("月销" + modelProductDetail.getGoods_sales_volume() + "笔");
                        WebViewUtil webViewUtil = new WebViewUtil(ActivityProductDetail.this);
                        webViewUtil.setView(webview);
                        webview.loadUrl(modelProductDetail.getWap_body());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                Utils.toastShow(ActivityProductDetail.this, errorModel.getMsg());
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityProductDetail.this);
            }
        });
    }


    @OnClick({R.id.btn_back, R.id.ll_size, R.id.ll_commit, R.id.btn_cart, R.id.btn_ok, R.id.tv_collect, R.id.tv_kefu, R.id.tv_store})
    @Override
    public void onClick(View v) {
        if (modelProductDetail != null) {
            switch (v.getId()) {
                case R.id.ll_size:
                    if (viewSize.getCount() > 0)
                        viewSize.ShowPop1(v);
                    break;
                case R.id.ll_commit:
                    Bundle bundle = new Bundle();
                    bundle.putString("goods_id", modelProductDetail.getGoods_commonid());
                    bundle.putString("commit", tv_commit.getText().toString());
                    bundle.putString("storename", modelProductDetail.getStore_info().getStore_name());
                    startActivity(ActivityProductDetail.this, ActivityCommitList.class, bundle);
                    break;
                case R.id.btn_ok:
                    if (application.getUserModel() == null) {
                        Intent intent = new Intent();
                        intent.setClass(this, ActLogin.class);
                        startActivity(intent);
                    } else if (modelSize == null) {
                        Utils.toastShow(ActivityProductDetail.this, "请选择商品尺码");
                    } else {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("cart_ids", modelSize.getId() + "|" + num);
                        bundle1.putInt("ifcart", 0);
                        startActivity(ActivityProductDetail.this, ActivityOrderConfirm.class, bundle1);
                    }
                    break;
                case R.id.btn_cart:
                    if (application.getUserModel() == null) {
                        Intent intent = new Intent();
                        intent.setClass(this, ActLogin.class);
                        startActivity(intent);
                    } else if (modelSize == null) {
                        Utils.toastShow(ActivityProductDetail.this, "请选择商品尺码");
                    } else {
                        addCart();
                    }
                    break;
                case R.id.tv_store:
                    startActivity(ActivityProductDetail.this, ActivityStore.class, null);
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.tv_kefu:
                    if (application.getUserModel() == null) {
                        Intent intent = new Intent();
                        intent.setClass(this, ActLogin.class);
                        startActivity(intent);
                    } else {
                        /**
                         * 启动单聊界面。
                         *
                         * @param context      应用上下文。
                         * @param targetUserId 要与之聊天的用户 Id。
                         * @param title        聊天的标题，开发者需要在聊天界面通过 intent.getData().getQueryParameter("title")
                         *                     获取该值, 再手动设置为聊天界面的标题。
                         */
                        RongIM.getInstance().startPrivateChat(this, "9527", "标题");
                    }
                    break;

                case R.id.tv_collect:
                    if (application.getUserModel() == null) {
                        Intent intent = new Intent();
                        intent.setClass(this, ActLogin.class);
                        startActivity(intent);
                    } else {
                        if (modelProductDetail != null) {
                            //已收藏 取消
                            if (modelProductDetail.getIs_favorites() == 1) {
                                cancleCollect();
                            }
                            //未收藏 收藏
                            else {
                                addCollect();
                            }
                        }
                    }
                    break;
            }
        } else {
            Utils.toastShow(ActivityProductDetail.this, "未获得产品信息,请重新进入该页面");
        }
    }

    /**
     * 添加购物车
     */
    private void addCart() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + ADDCART);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("goods_id", modelSize.getId() + "");
        map.put("quantity", num + "");
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, "添加购物车成功");
                //刷新购物车
                Intent intent = new Intent();
                intent.setAction(action_refresh_cartlist);
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityProductDetail.this);
            }
        });
    }

    /**
     * 取消收藏
     */
    private void cancleCollect() {
        tv_collect.setEnabled(false);
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.delBatchFavorites.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("fav_ids", goods_id);
        map.put("type", "goods");
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                tv_collect.setEnabled(true);
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, "取消收藏成功");
                Drawable drawable = getResources().getDrawable(R.mipmap.iv_shoucang_off);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_collect.setCompoundDrawables(null, drawable, null, null);
                tv_collect.setText("收藏");
                modelProductDetail.setIs_favorites(0);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                tv_collect.setEnabled(true);
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, "取消收藏失败");
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityProductDetail.this);
            }
        });
    }

    /**
     * 添加收藏
     */
    private void addCollect() {
        tv_collect.setEnabled(false);
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.addGoodFavorites.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("goods_id", goods_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                tv_collect.setEnabled(true);
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, "添加收藏成功");
                Drawable drawable = getResources().getDrawable(R.mipmap.iv_shoucang_on);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_collect.setCompoundDrawables(null, drawable, null, null);
                tv_collect.setText("已收藏");
                modelProductDetail.setIs_favorites(1);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                tv_collect.setEnabled(true);
                LoadingD.hideDialog();
                Utils.toastShow(ActivityProductDetail.this, "添加收藏失败");
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityProductDetail.this);
            }
        });
    }

}
