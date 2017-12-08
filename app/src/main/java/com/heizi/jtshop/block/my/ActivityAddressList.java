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
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseListProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo on 17/10/6.
 */

public class ActivityAddressList extends BaseListActivity {
    private LinearLayout ll_notice;
    private List<ModelAddress> listData = new ArrayList<>();
    private CommonAdapter adapter;
    Button btn_addd;

    boolean isChoice;
    String address_id_orderconfirm = "";//订单确认页的地址id

    //获取列表
    private ParseListProtocol<ModelAddress> parsePanoList;
    private IResponseCallback<DataSourceModel<ModelAddress>> callbackPanoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            isChoice = getIntent().getExtras().getBoolean("isChoice");
            address_id_orderconfirm = getIntent().getExtras().getString("address_id");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_address_list;
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("收货地址");
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
                startActivityForResult(ActivityAddressList.this, ActivityAddressEdit.class, bundle, 100);
            }
        });
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        mListView.setAutoLoadEnable(false);
        mListView.setPullLoadEnable(false);
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_address) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                LinearLayout ll_address = holder.findViewById(R.id.ll_address);
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_tel = holder.findViewById(R.id.tv_tel);
                TextView tv_address = holder.findViewById(R.id.tv_address);
                ImageView iv_moren = holder.findViewById(R.id.iv_moren);
                TextView tv_moren = holder.findViewById(R.id.tv_moren);
                TextView tv_edit = holder.findViewById(R.id.tv_edit);
                TextView tv_delete = holder.findViewById(R.id.tv_delete);
                tv_name.setText(listData.get(position).getTrue_name());
                tv_tel.setText(listData.get(position).getMob_phone());
                tv_address.setText(listData.get(position).getArea_info() + "  " + listData.get(position).getAddress());
                if (listData.get(position).getIs_default() == 1) {
                    iv_moren.setImageDrawable(getResources().getDrawable(R.mipmap.dui));
                    tv_moren.setText("默认地址");
                } else {
                    iv_moren.setImageDrawable(getResources().getDrawable(R.mipmap.duik));
                    tv_moren.setText("设为默认");
                }

                if (isChoice) {
                    ll_address.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("modelAddress", listData.get(position));
                            setResult(ActivityOrderConfirm.requestAddress, intent);
                            finish();
                        }
                    });
                }
                tv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isAdd", false);
                        bundle.putSerializable("model", listData.get(position));
                        startActivityForResult(ActivityAddressList.this, ActivityAddressEdit.class, bundle, 100);
                    }
                });

                iv_moren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listData.get(position).getIs_default() != 1)
                            setMoren(listData.get(position).getAddress_id());
                    }
                });
                tv_moren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listData.get(position).getIs_default() != 1)
                            setMoren(listData.get(position).getAddress_id());
                    }
                });

                tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(listData.get(position).getAddress_id());
                    }
                });
            }
        };
        mListView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseListProtocol<>(this, SERVER_URL_SHOP + "&method=jingtu.address.addressList.get/", ModelAddress.class);
        callbackPanoList = new IResponseCallback<DataSourceModel<ModelAddress>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelAddress> data) {
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
                LoadingD.showDialog(ActivityAddressList.this);
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

    private void setMoren(String address_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.address.setDefAddress.post/");
        Map<String, String> maps = new HashMap<>();
        maps.put("token", userModel.getToken());
        maps.put("address_id", address_id);
        parseStringProtocol.postData(maps, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressList.this, "成功设置默认地址");
                onRefresh();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressList.this, "设置默认地址失败");
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityAddressList.this);
            }
        });
    }

    private void delete(final String address_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.address.addressDel.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("address_id", address_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressList.this, "删除成功!");
                //删除的地址是订单确认页的
                if (address_id_orderconfirm.equals(address_id)) {
                    Intent intent = new Intent();
                    setResult(ActivityOrderConfirm.requestAddress, intent);
                }
                onRefresh();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressList.this, "删除失败!");
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityAddressList.this);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 100) {
            //如果修改的地址是订单确认页的需通知订单确认页
            if (data != null) {
                ModelAddress modelAddress = (ModelAddress) data.getSerializableExtra("modelAddress");
                if (modelAddress != null && modelAddress.getAddress_id().equals(address_id_orderconfirm)) {
                    Intent intent = new Intent();
                    intent.putExtra("modelAddress", modelAddress);
                    setResult(ActivityOrderConfirm.requestAddress, intent);
                }
            }
            onRefresh();
        }
    }

    @Override
    protected void onDestroy() {
//        if (listData.size() == 0) {
//            Intent intent = new Intent();
//            setResult(ActivityOrderConfirm.requestAddress, intent);
//        }
        super.onDestroy();

    }
}
