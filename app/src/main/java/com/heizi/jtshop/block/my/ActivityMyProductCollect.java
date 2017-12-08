package com.heizi.jtshop.block.my;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.jtshop.block.maidan.ActivityProductDetail;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
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

/**
 * Created by leo on 17/6/8.
 */

public class ActivityMyProductCollect extends BaseListActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout ll_notice;
    private TextView tv_notice;
    //listview
    private List<ModelProductList> listData = new ArrayList<>();
    private CommonAdapter adapter;
    //获取全景列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;

    //批量删除
    private ParseStringProtocol protocolDel;
    private IResponseCallback<DataSourceModel<String>> callbackDel;

    private String delIds = "";
    /**
     * 1为列表 2为选择状态，选择按钮出现
     */
    private int state = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list;
    }


    @Override
    protected void initView() {
        super.initView();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("编辑");
        tv_right1.setText("取消");
        tv_title.setText("我的收藏");
        tv_right.setOnClickListener(this);
        tv_right1.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setText("暂无收藏记录");
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_home_store) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                LinearLayout ll_baoyou = holder.findViewById(R.id.ll_baoyou);
                ImageView iv_store = holder.findViewById(R.id.iv_store);
                ImageView iv_select = holder.findViewById(R.id.iv_select);
                ImageFactory.displayImage(listData.get(position).getGoods_image_url(), iv_store, 0, 0);
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_local = holder.findViewById(R.id.tv_local);
                TextView tv_guanggao = holder.findViewById(R.id.tv_guanggao);
                TextView tv_baoyou = holder.findViewById(R.id.tv_baoyou);
                TextView tv_price = holder.findViewById(R.id.tv_price);
                TextView tv_num = holder.findViewById(R.id.tv_num);
                tv_name.setText(listData.get(position).getGoods_name());
                tv_price.setText(listData.get(position).getGoods_price());
                tv_num.setText("销量:" + listData.get(position).getGoods_salenum());

                // 列表模式
                if (state == 1)

                {
                    iv_select.setVisibility(View.GONE);
                } else

                {
                    iv_select.setVisibility(View.VISIBLE);
                    if (listData.get(position).getDelete_state() == 2) {
                        iv_select.setImageDrawable(getResources()
                                .getDrawable(R.mipmap.dui));
                    } else {
                        iv_select.setImageDrawable(getResources()
                                .getDrawable(R.mipmap.duik));
                    }
                }

            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.myGoodsFavorites.get/");
        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {


                try {
                    JSONObject jsonObject = new JSONObject(data.json);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("page_data"));
                    hasMore = jsonObject1.getBoolean("hasmore");
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("items"));

                    //先判断是否是刷新,刷新成功则清除数据
                    if (isRefresh) {
                        pageIndex = 1;
                        listData.removeAll(listData);
                    }
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            ModelProductList modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelProductList.class);
                            listData.add(modelPano);
                        }
                        adapter.notifyDataSetChanged();
                        //无更多数据
                        if (!hasMore) {
                            mListView.setPullLoadEnable(false);
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listData.size() == 0) {
                    ll_notice.setVisibility(View.VISIBLE);
                    mListView.setPullRefreshEnable(false);
                } else {
                    ll_notice.setVisibility(View.GONE);
                    mListView.setPullRefreshEnable(true);
                }
                isRefresh = false;
                onLoad();

            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                isRefresh = false;
                onLoad();
            }

            @Override
            public void onStart() {
                isBusy = true;
            }
        };


        protocolDel = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.member_favorites.delBatchFavorites.post/");
        callbackDel = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Utils.toastShow(ActivityMyProductCollect.this, data.msg).show();
                LoadingD.hideDialog();
                onRefresh();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Utils.toastShow(ActivityMyProductCollect.this, errorModel.getMsg()).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityMyProductCollect.this);
            }
        };

        if (listData.size() == 0)
            getData();
    }

    //获取文章数据
    public void getData() {
        if (com.heizi.jtshop.utils.Utils.checkLogin(this, userModel)) {
            Map<String, String> maps = new HashMap<>();
            maps.put("token", userModel.getToken());
            maps.put("pagesize", pageSize + "");
            maps.put("curpage", pageIndex + "");
            if (parsePanoList != null)
                parsePanoList.getData(maps, callbackPanoList);
        }
    }

    /**
     * 批量删除
     */
    public void postDel() {
        delIds = "";
        for (ModelProductList model : listData) {
            if (model.getDelete_state() == 2)
                delIds += model.getGoods_id() + ",";
        }
        if (!delIds.equals("")) {
            delIds = delIds.substring(0, delIds.lastIndexOf(","));
            Map<String, String> maps = new HashMap<>();
            maps.put("token", userModel.getToken());
            maps.put("fav_ids", delIds);
            maps.put("type", "goods");
            if (protocolDel != null)
                protocolDel.postData(maps, callbackDel);
        } else {
            Utils.toastShow(this, "请选择要删除的收藏").show();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (state == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("goods_id", listData.get(position - 1).getGoods_id());
            startActivity(this, ActivityProductDetail.class, bundle);
        } else {
            ImageView iv_select = (ImageView) view.findViewById(R.id.iv_select);
            if (listData.get(position - 1).getDelete_state() == 1) {
                listData.get(position - 1).setDelete_state(2);
                iv_select.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
            } else {
                listData.get(position - 1).setDelete_state(1);
                iv_select.setImageDrawable(getResources().getDrawable(
                        R.mipmap.duik));
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_right:
                if (state == 1) {
                    tv_right.setText("删除");
                    tv_right1.setVisibility(View.VISIBLE);
                    state = 2;
                    adapter.notifyDataSetChanged();
                } else if (state == 2) {
                    postDel();
                }
                break;

            case R.id.tv_right1:
                state = 1;
                tv_right1.setVisibility(View.GONE);
                tv_right.setText("编辑");
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
