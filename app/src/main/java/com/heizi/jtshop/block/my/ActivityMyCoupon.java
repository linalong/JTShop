package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.jtshop.block.maidan.ActivityOrderConfirm;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.DateUtils;
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
 * 优惠券
 * Created by leo on 17/6/8.
 */

public class ActivityMyCoupon extends BaseListActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout ll_notice;
    private TextView tv_notice;
    //listview
    private List<ModelCoupon> listData = new ArrayList<>();
    private CommonAdapter adapter;

    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;


    /**
     * 1为列表 2为选择状态，选择按钮出现
     */
    private int state = 1;
    private String store_goods_total = "";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        state = getIntent().getExtras().getInt("state");
        store_goods_total = getIntent().getExtras().getString("store_goods_total");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("我的优惠券");
        btn_back.setOnClickListener(this);
        findViewById(R.id.parent_layout).setBackgroundColor(getResources().getColor(R.color.gray12));
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setText("暂无优惠券");
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_coupon) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                TextView tv_money = holder.findViewById(R.id.tv_money);
                TextView tv_time = holder.findViewById(R.id.tv_time);
                TextView tv_condition = holder.findViewById(R.id.tv_condition);
                TextView tv_title = holder.findViewById(R.id.tv_title);
                tv_title.setText(listData.get(position).getVoucher_title());
                tv_money.setText(listData.get(position).getVoucher_price());
                tv_time.setText("使用期限: " + DateUtils.timedate2(listData.get(position).getVoucher_start_date()) + " ~ " + DateUtils.timedate2(listData.get(position).getVoucher_end_date()));
                tv_condition.setText("满" + (int) Double.parseDouble(listData.get(position).getVoucher_limit()) + "可用");

            }
        };
        mListView.setAutoLoadEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        if (listData.size() == 0)
            getData();
    }

    @Override
    protected void initData() {
        super.initData();
        if (state == 1) {
            parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + COUPONLIST);
        } else {
            parsePanoList = new ParseStringProtocol(this, SERVER_URL_SHOP + ORDERCOUPONLIST);
        }

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
                            ModelCoupon modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelCoupon.class);
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
                LoadingD.hideDialog();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                isRefresh = false;
                onLoad();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityMyCoupon.this);
            }
        };
        if (listData.size() == 0)
            getData();

    }

    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("token", userModel.getToken());
        if (state == 2) {
            maps.put("store_goods_total", store_goods_total);
        }
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (state == 2) {
            Intent intent = new Intent();
            intent.putExtra("money", listData.get(position - 1).getVoucher_price());
            intent.putExtra("condition", listData.get(position - 1).getVoucher_limit());
            intent.putExtra("couponId", listData.get(position - 1).getVoucher_t_id());
            setResult(ActivityOrderConfirm.requestCoupon, intent);
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }

//    private void geneItems() {
//        for (int i = 0; i != 10; ++i) {
//            listData.add("Test XScrollView item " + (pageIndex));
//        }
//        Log.d("==", "Test XListView item " + (pageIndex));
//    }
}
