package com.heizi.jtshop.block.pano;

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
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索得全景列表
 * Created by leo on 17/6/8.
 */

public class ActivityPanoList extends BaseListActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout ll_notice;
    private TextView tv_notice;
    //listview
    private List<ModelPano> listData = new ArrayList<>();
    private CommonAdapter adapter;
    //获取全景列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;

    String keyword = "";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.getString("keyword") != null) {
                keyword = bundle.getString("keyword");
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        btn_back.setOnClickListener(this);
        tv_title.setText("搜索结果");
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setText("暂无收藏记录");
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_home) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                ImageView iv_back = holder.findViewById(R.id.iv_back);
                ImageFactory.displayImage(listData.get(position).getPreview_img(), iv_back, 0, 0);
                TextView tv_local = holder.findViewById(R.id.tv_local);
                if (listData.get(position).getStore_id().equals("0")) {
                    tv_local.setText(listData.get(position).getTitle());
                } else {
                    tv_local.setText(listData.get(position).getStore_name());
                }

            }
        };

        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setAutoLoadEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + GETPANOLIST);

        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {


                try {
                    JSONArray jsonArray = new JSONArray(data.json);
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


        if (listData.size() == 0)
            getData();
    }

    //获取文章数据
    public void getData() {
        if (com.heizi.jtshop.utils.Utils.checkLogin(this, userModel)) {
            Map<String, String> maps = new HashMap<>();
            maps.put("city_id", "");
            maps.put("keyword", keyword);
            if (parsePanoList != null)
                parsePanoList.getData(maps, callbackPanoList);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Bundle bundle = new Bundle();
        bundle.putString("url", listData.get(position).getPano_url());
        bundle.putString("pano_id", listData.get(position).getId());
        bundle.putString("title", listData.get(position).getTitle());
        bundle.putString("des", listData.get(position).getArea_info());
        bundle.putString("img", listData.get(position).getPreview_img());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(ActivityPanoList.this, ActivityPanoDetail.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
