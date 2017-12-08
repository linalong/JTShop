package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.jtshop.block.maidan.ActivityOrderConfirm;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseListProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo on 17/10/6.
 */

public class ActivityBankList extends BaseListActivity {
    private LinearLayout ll_notice;
    private List<ModelBank> listData = new ArrayList<>();
    private CommonAdapter adapter;
    Button btn_addd;

    /**
     * 1为列表 2为选择状态，选择按钮出现
     */
    private int state = 1;


    //获取列表
    private ParseListProtocol<ModelBank> parsePanoList;
    private IResponseCallback<DataSourceModel<ModelBank>> callbackPanoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().getExtras() != null)
            state = getIntent().getExtras().getInt("state", 1);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_bank_list;
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("我的银行卡");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_addd = (Button) findViewById(R.id.btn_add);
        btn_addd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isAdd", true);
                startActivityForResult(ActivityBankList.this, ActivityBankEdit.class, bundle, 100);
            }
        });
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        mListView.setAutoLoadEnable(false);
        mListView.setPullLoadEnable(false);
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_bank) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                LinearLayout ll_bank = holder.findViewById(R.id.ll_bank);
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_no = holder.findViewById(R.id.tv_no);
                TextView tv_bank = holder.findViewById(R.id.tv_bank);
                ImageView iv_logo = holder.findViewById(R.id.iv_logo);
                tv_name.setText("持卡人: " + listData.get(position).getPdc_user_name());
                tv_no.setText("银行卡号: " + listData.get(position).getPdc_bank_no());
                tv_bank.setText(listData.get(position).getPdc_bank_name());
                ll_bank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (state == 2) {
                            Intent intent = new Intent();
                            intent.putExtra("id", listData.get(position).getPdc_id());
                            intent.putExtra("name", listData.get(position).getPdc_bank_name());
                            setResult(ActivityOrderConfirm.requestCoupon, intent);
                            finish();

                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isAdd", false);
                            bundle.putSerializable("model", listData.get(position));
                            startActivityForResult(ActivityBankList.this, ActivityBankEdit.class, bundle, 100);
                        }
                    }
                });
                ImageFactory.displayImage(listData.get(position).getBank_logo(), iv_logo, 0, 0);
            }
        };
        mListView.setAdapter(adapter);

    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseListProtocol<>(this, SERVER_URL_SHOP + MYBANKLIST, ModelBank.class);
        callbackPanoList = new IResponseCallback<DataSourceModel<ModelBank>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelBank> data) {
                isBusy = false;
                //先判断是否是刷新,刷新成功则清除数据
                if (isRefresh) {
                    pageIndex = 1;
                    listData.removeAll(listData);
                }
                if (data.list.size() > 0) {
                    listData.addAll(data.list);
                    adapter.notifyDataSetChanged();
                }

                if (listData.size() == 0) {
                    ll_notice.setVisibility(View.VISIBLE);
                } else {
                    ll_notice.setVisibility(View.GONE);
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
                LoadingD.showDialog(ActivityBankList.this);
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
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 100) {
            onRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
