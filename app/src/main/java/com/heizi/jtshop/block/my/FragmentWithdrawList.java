package com.heizi.jtshop.block.my;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.fragment.BaseListFragment;
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
 * Created by leo on 17/10/6.
 */

public class FragmentWithdrawList extends BaseListFragment {
    private LinearLayout ll_notice;
    private List<ModeWithdraw> listData = new ArrayList<>();
    private CommonAdapter adapter;


    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_no_title;
    }


    @Override
    protected void initView(View v) {
        super.initView(v);
        ll_notice = (LinearLayout) v.findViewById(R.id.ll_notice);
        //listview
        adapter = new CommonAdapter(getActivity(), listData, R.layout.item_withdraw) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_no = holder.findViewById(R.id.tv_no);
                TextView tv_bank = holder.findViewById(R.id.tv_bank);
                TextView tv_price = holder.findViewById(R.id.tv_price);
                TextView tv_state = holder.findViewById(R.id.tv_state);
                TextView tv_time = holder.findViewById(R.id.tv_time);
                tv_name.setText("持卡人: " + listData.get(position).getPdc_bank_user());
                tv_no.setText("银行卡号: " + listData.get(position).getPdc_bank_no());
                tv_bank.setText(listData.get(position).getPdc_bank_name());
                tv_price.setText("提现金额: "+listData.get(position).getPdc_amount());
                tv_time.setText(DateUtils.timedate2(listData.get(position).getPdc_add_time()));
                if (listData.get(position).getPdc_payment_state() == 0) {
                    tv_state.setText("审核中");
                    tv_state.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    tv_state.setText("已通过");
                    tv_state.setTextColor(getResources().getColor(R.color.green1));
                }

            }
        };
        mListView.setAdapter(adapter);

    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + WITHDRAWLIST);
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
                            ModeWithdraw modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModeWithdraw.class);
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
                isBusy = false;
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
        if (listData.size() == 0)
            getData();

    }

    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("token", userModel.getToken());
        maps.put("pagesize", pageSize + "");
        maps.put("curpage", pageIndex + "");
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);
    }
}
