package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.jtshop.block.pano.ModelPano;
import com.heizi.jtshop.block.pano.ActivityPanoDetail;
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

public class ActivityMyVRCollect extends BaseListActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout ll_notice;
    private TextView tv_notice;
    //listview
    private List<ModelPano> listData = new ArrayList<>();
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
        adapter = new CommonAdapter(this, listData, R.layout.item_home) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                ImageView iv_select = holder.findViewById(R.id.iv_select);
                ImageView iv_back = holder.findViewById(R.id.iv_back);
                TextView tv_local = holder.findViewById(R.id.tv_local);
                if (listData.get(position).getStore_id().equals("0")) {
                    tv_local.setText(listData.get(position).getTitle());
                } else {
                    tv_local.setText(listData.get(position).getStore_name());
                }

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

                ImageFactory.displayImage(listData.get(position).getPreview_img(), iv_back, 0, 0);

            }
        };

        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + MYCOLLECTVR);
        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {


                try {
                    JSONObject jsonObject = new JSONObject(data.json);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("items"));
                    //先判断是否是刷新,刷新成功则清除数据
                    if (isRefresh) {
                        pageIndex = 1;
                        listData.removeAll(listData);
                    }
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            ModelPano modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelPano.class);
                            listData.add(modelPano);
                        }
                        //数据小于请求的每页数据
                        if (jsonArray.length() < pageSize) {
                            mListView.setPullLoadEnable(false);
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listData.size() == 0) {
                    ll_notice.setVisibility(View.VISIBLE);
                } else {
                    ll_notice.setVisibility(View.GONE);
                }
                onLoad();
                isRefresh = false;

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


        protocolDel = new ParseStringProtocol(this, SERVER_URL + "/index.php/Api/userfavorites/delBatchFavorites/");
        callbackDel = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Utils.toastShow(ActivityMyVRCollect.this, data.msg).show();
                LoadingD.hideDialog();
                onRefresh();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Utils.toastShow(ActivityMyVRCollect.this, errorModel.getMsg()).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityMyVRCollect.this);
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
        for (ModelPano model : listData) {
            if (model.getDelete_state() == 2)
                delIds += model.getId() + ",";
        }
        if (!delIds.equals("")) {
            delIds = delIds.substring(0, delIds.lastIndexOf(","));
            Map<String, String> maps = new HashMap<>();
            maps.put("token", userModel.getToken());
            maps.put("pano_ids", delIds);
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
            bundle.putString("url", listData.get(position).getPano_url());
            bundle.putString("pano_id", listData.get(position).getId());
            bundle.putString("title", listData.get(position).getTitle());
            bundle.putString("des", listData.get(position).getArea_info());
            bundle.putString("img", listData.get(position).getPreview_img());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(ActivityMyVRCollect.this, ActivityPanoDetail.class);
            startActivity(intent);
        } else {
            ImageView iv_select = (ImageView) view.findViewById(R.id.iv_select);
            if (listData.get(position).getDelete_state() == 1) {
                listData.get(position).setDelete_state(2);
                iv_select.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
            } else {
                listData.get(position).setDelete_state(1);
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
