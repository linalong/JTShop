package com.heizi.jtshop.block.maidan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.DateUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
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
 * Created by leo on 17/9/19.
 */

public class ActivityCommitList extends BaseListActivity { //listview

    TextView tv_store_name;
    TextView tv_commit;

    private LinearLayout ll_notice;
    private List<ModelCommit> listData = new ArrayList<>();
    private CommonAdapter adapter;

    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;

    String commit = "", storeName = "", goods_id = "";


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_commit_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        commit = getIntent().getExtras().getString("commit");
        storeName = getIntent().getExtras().getString("storename");
        goods_id = getIntent().getExtras().getString("goods_id");
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("评价");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_store_name = (TextView) findViewById(R.id.tv_store_name);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_commit.setText(commit);
        tv_store_name.setText(storeName);
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_commit) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                ImageView iv_photo = holder.findViewById(R.id.iv_photo);
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_time = holder.findViewById(R.id.tv_time);
                TextView tv_des = holder.findViewById(R.id.tv_des);
                TextView tv_spec = holder.findViewById(R.id.tv_spec);
                GridView grid = holder.findViewById(R.id.grid);
                tv_des.setText(listData.get(position).getGeval_content());
                tv_spec.setText(listData.get(position).getSpec());
                tv_name.setText(listData.get(position).getGeval_frommembername());
                if (!TextUtils.isEmpty(listData.get(position).getGeval_addtime()))
                    tv_time.setText(DateUtils.time2(listData.get(position).getGeval_addtime()));
                ImageFactory.displayImage(listData.get(position).getGeval_frommemberavatar(), iv_photo, 0, 0);
                if (listData.get(position).getGeval_image().size() > 0) {
                    grid.setVisibility(View.VISIBLE);
                    grid.setAdapter(new CommonAdapter(ActivityCommitList.this, listData.get(position).getGeval_image(), R.layout.item_device_signed_action_image) {
                        @Override
                        public void getView(int position2, ViewHolderHelper holder) {
                            ImageView iv_select_pic3 = holder.findViewById(R.id.iv_select_pic3);
                            ImageFactory.displayImage(listData.get(position).getGeval_image().get(position2).getSmall(), iv_select_pic3, 0, 0);
                        }
                    });
                } else {
                    grid.setVisibility(View.GONE);
                }
            }
        };
        mListView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + GETCOMMITLIST);
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
                            ModelCommit modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelCommit.class);
                            modelPano.setSpec(jsonArray.getJSONObject(i).getJSONArray("spec_info").getString(0));
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
                LoadingD.showDialog(ActivityCommitList.this);
                LoadingD.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        };

        if (listData.size() == 0)
            getData();
    }

    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("goods_commonid", goods_id);
        maps.put("pagesize", pageSize + "");
        maps.put("curpage", pageIndex + "");
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);
    }

}
