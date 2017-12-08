package com.heizi.jtshop.block.my;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.jtshop.block.maidan.ActivityCommit;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
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
 * Created by leo on 17/5/17.
 */

public class ActivityRefundList extends BaseListActivity {


    private LinearLayout ll_notice;
    private List<ModelRefundList> listData = new ArrayList<>();
    private CommonAdapter adapter;

    ParseStringProtocol parseStringProtocol;
    IResponseCallback<DataSourceModel<String>> callback;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list;
    }


    @Override
    protected void initView() {
        super.initView();
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        tv_title.setText("退款/售后");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_refund) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                LinearLayout ll_shop = holder.findViewById(R.id.ll_shop);
                TextView tv_shop_name = holder.findViewById(R.id.tv_shop_name);
                TextView tv_refund_stata = holder.findViewById(R.id.tv_refund_stata);
                TextView tv_refund_type = holder.findViewById(R.id.tv_refund_type);
                TextView tv_detail = holder.findViewById(R.id.tv_detail);
                TextView tv_refund_price = holder.findViewById(R.id.tv_refund_price);
                tv_shop_name.setText(listData.get(position).getStore_name());
                tv_refund_stata.setText(listData.get(position).getRefund_state_des());

                if (listData.get(position).getRefund_type() == 1) {
                    tv_refund_type.setText("仅退款");
                } else {
                    tv_refund_type.setText("退款退货");
                }
                tv_refund_price.setText("退款金额:  ¥" + listData.get(position).getRefund_amount());
                /**
                 * 产品列表
                 */
                ListView lv = holder.findViewById(R.id.lv);
                final List<ModelProductList> data = new ArrayList<>();
                data.addAll(listData.get(position).getGoods_info());
                CommonAdapter adapter = new CommonAdapter(ActivityRefundList.this, data, R.layout.item_order_confirm) {
                    @Override
                    public void getView(int position2, ViewHolderHelper holder) {
                        ImageView iv_product = holder.findViewById(R.id.iv_product);
                        TextView tv_product = holder.findViewById(R.id.tv_product);
                        TextView tv_size = holder.findViewById(R.id.tv_size);
                        TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                        TextView tv_num2 = holder.findViewById(R.id.tv_num2);
                        //退款描述
                        TextView tv_tuikuan_state = holder.findViewById(R.id.tv_tuikuan_state);

                        ModelProductList modelProductList = listData.get(position).getGoods_info().get(position2);
                        ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                        tv_num2.setText("x" + modelProductList.getGoods_num());
                        tv_price_single.setText(modelProductList.getGoods_price());
                        tv_product.setText(modelProductList.getGoods_name());
                        if (modelProductList.getSpec_info().size() > 0)
                            tv_size.setText(modelProductList.getSpec_info().get(0));


                    }
                };
                lv.setAdapter(adapter);

                tv_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modelRefundList", listData.get(position));
                        startActivity(ActivityRefundList.this, ActivityRefundDetail.class, bundle);
                    }
                });


            }
        };
        mListView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        super.initData();
        parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + REFUNDLIST);
        callback = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy=false;
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
                            ModelRefundList modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelRefundList.class);
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
                isBusy=false;
            }

            @Override
            public void onStart() {
                isBusy=true;
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
        if (parseStringProtocol != null)
            parseStringProtocol.getData(maps, callback);
    }


}
