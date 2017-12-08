package com.heizi.jtshop.block.my;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.home.ActivityStore;
import com.heizi.jtshop.block.maidan.ActivityCommit;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.DateUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 订单详情
 * Created by leo on 17/10/21.
 */

public class ActivityOrderDetail extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.ll_order_status)
    LinearLayout ll_order_status;
    @InjectView(R.id.ll_remark)
    LinearLayout ll_remark;
    @InjectView(R.id.tv_order_status1)
    TextView tv_order_status1;
    @InjectView(R.id.tv_order_status2)
    TextView tv_order_status2;
    @InjectView(R.id.tv_name)
    TextView tv_name;
    @InjectView(R.id.tv_tel)
    TextView tv_tel;
    @InjectView(R.id.tv_address)
    TextView tv_address;

    @InjectView(R.id.tv_remark)
    TextView tv_remark;
    @InjectView(R.id.tv_store)
    TextView tv_store;
    @InjectView(R.id.ll_shop)
    LinearLayout ll_shop;
    @InjectView(R.id.tv_peisong)
    TextView tv_peisong;
    @InjectView(R.id.tv_price_order)
    TextView tv_price_order;//订单金额
    @InjectView(R.id.tv_price_order_true)
    TextView tv_price_order_true;//实际支付

    @InjectView(R.id.tv_order_sn)
    TextView tv_order_sn;//订单编号
    @InjectView(R.id.tv_pay_sn)
    TextView tv_pay_sn;//支付编号
    @InjectView(R.id.tv_time_add)
    TextView tv_time_add;//创建时间
    @InjectView(R.id.tv_time_pay)
    TextView tv_time_pay;//支付时间
    @InjectView(R.id.tv_time_send)
    TextView tv_time_send;//发货时间
    @InjectView(R.id.tv_time_finish)
    TextView tv_time_finish;//结束时间
    @InjectView(R.id.tv_copy)
    TextView tv_copy;//复制订单号

    @InjectView(R.id.tv_store_im)
    TextView tv_store_im;//店铺客服
    @InjectView(R.id.tv_store_tel)
    TextView tv_store_tel;//店铺电话
    @InjectView(R.id.tv_cancel)
    TextView tv_cancel;//取消订单
    @InjectView(R.id.tv_refund)
    TextView tv_refund;//申请退款
    @InjectView(R.id.tv_pay)
    TextView tv_pay;//支付
    @InjectView(R.id.tv_tixing)
    TextView tv_tixing;//提醒发货
    @InjectView(R.id.tv_shouhuo)
    TextView tv_shouhuo;//确认收货
    @InjectView(R.id.tv_delete)
    TextView tv_delete;//删除订单
    @InjectView(R.id.tv_commit)
    TextView tv_commit;//评价

    @InjectView(R.id.ll_youhui)
    LinearLayout ll_youhui;
    @InjectView(R.id.tv_price_youhui)
    TextView tv_price_youhui;//优惠金额

    //listview
    @InjectView(R.id.listview)
    ListView listview;
    private List<ModelProductList> listData = new ArrayList<>();
    private CommonAdapter adapter;

    ModelOrderList modelOrderList;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        modelOrderList = (ModelOrderList) getIntent().getExtras().getSerializable("modelOrderList");
        registRefresh();
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("订单详情");
        refresh();

    }

    private void refresh() {
        if (listData.size() > 0)
            listData.removeAll(listData);
        listData.addAll(modelOrderList.getOrder_goods());
        listview.setAdapter(new CommonAdapter(ActivityOrderDetail.this, listData, R.layout.item_order_confirm) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                ImageView iv_product = holder.findViewById(R.id.iv_product);
                TextView tv_product = holder.findViewById(R.id.tv_product);
                TextView tv_size = holder.findViewById(R.id.tv_size);
                TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                TextView tv_num2 = holder.findViewById(R.id.tv_num2);
                //退款描述
                TextView tv_tuikuan_state = holder.findViewById(R.id.tv_tuikuan_state);
                TextView tv_tuikuan_btn = holder.findViewById(R.id.tv_tuikuan_btn);

                ModelProductList modelProductList = listData.get(position);
                ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                tv_num2.setText("x" + modelProductList.getGoods_num());
                tv_price_single.setText(modelProductList.getGoods_price());
                tv_product.setText(modelProductList.getGoods_name());
                if (modelProductList.getSpec_info().size() > 0)
                    tv_size.setText(modelProductList.getSpec_info().get(0));


                //退款相关
                if (modelOrderList.getIs_refund() == 1) {
                    tv_tuikuan_state.setVisibility(View.VISIBLE);
                    tv_tuikuan_state.setText(listData.get(position).getRefund_state_des());
                } else {
                    tv_tuikuan_state.setVisibility(View.GONE);
                }

                //待收货
                if (modelOrderList.getOrder_state() == 30) {
                    if (listData.get(position).getIs_refund() == 1) {
                        tv_tuikuan_btn.setVisibility(View.GONE);
                        tv_tuikuan_state.setVisibility(View.VISIBLE);
                        tv_tuikuan_state.setText(listData.get(position).getRefund_state_des());
                    } else {
                        tv_tuikuan_state.setVisibility(View.GONE);
                        tv_tuikuan_btn.setVisibility(View.VISIBLE);
                        tv_tuikuan_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("modelOrderList", modelOrderList);
                                bundle.putInt("position", position);
                                bundle.putInt("type", 30);
                                startActivity(ActivityOrderDetail.this, ActivityRefundApply.class, bundle);
                            }
                        });
                    }
                }
            }
        });

        if (modelOrderList.getVoucher_price() > 0) {
            ll_youhui.setVisibility(View.VISIBLE);
            tv_price_youhui.setText(modelOrderList.getVoucher_price() + "");
        } else {
            ll_youhui.setVisibility(View.GONE);
        }

        tv_name.setText(modelOrderList.getReciver_name());
        tv_address.setText(modelOrderList.getAddress());
        tv_tel.setText(modelOrderList.getMob_phone());
        tv_store.setText(modelOrderList.getStore_name());

        if (TextUtils.isEmpty(modelOrderList.getOrder_message())) {
            ll_remark.setVisibility(View.GONE);
        } else {
            ll_remark.setVisibility(View.VISIBLE);
            tv_remark.setText(modelOrderList.getOrder_message());
        }

        ll_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActivityOrderDetail.this, ActivityStore.class, null);
            }
        });

        if (modelOrderList.getShipping_fee() == 0) {
            tv_peisong.setText("免邮");
        } else {
            tv_peisong.setText(modelOrderList.getShipping_fee() + "元");
        }

        tv_price_order.setText(modelOrderList.getGoods_amount());
        tv_price_order_true.setText(modelOrderList.getOrder_amount());


        tv_order_sn.setText("订单编号: " + modelOrderList.getOrder_sn());
        tv_pay_sn.setText("支付编号: " + modelOrderList.getPay_sn());
        tv_time_add.setText("创建时间: " + DateUtils.timedate(modelOrderList.getAdd_time()));
        tv_time_pay.setText("付款时间: " + DateUtils.timedate(modelOrderList.getPayment_time()));
        tv_time_send.setText("发货时间: " + DateUtils.timedate(modelOrderList.getShipping_time()));
        tv_time_finish.setText("完成时间: " + DateUtils.timedate(modelOrderList.getFinnshed_time()));
        int type = modelOrderList.getOrder_state();
        if (type == 10) {
            tv_cancel.setVisibility(View.VISIBLE);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancleOrder(modelOrderList.getOrder_id());
                }
            });
            tv_pay.setVisibility(View.VISIBLE);
            tv_order_status1.setText("等待买家付款");
            tv_order_status2.setVisibility(View.VISIBLE);
            tv_order_status2.setText("剩余" + DateUtils.getDistanceTime(DateUtils.getCurrent(), Long.parseLong(modelOrderList.getOrder_cancel_day())) + "自动关闭");
        } else if (type == 20) {
            tv_order_status1.setText("买家已付款");
            tv_tixing.setVisibility(View.VISIBLE);
            tv_time_pay.setVisibility(View.VISIBLE);
            //无退款时可退款
            if (modelOrderList.getIs_refund() == 0) {
                tv_refund.setVisibility(View.VISIBLE);
                tv_refund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modelOrderList", modelOrderList);
                        bundle.putInt("position", -1);
                        bundle.putInt("type", 20);
                        startActivity(ActivityOrderDetail.this, ActivityRefundApply.class, bundle);
                    }
                });
            } else {
                tv_refund.setVisibility(View.GONE);
            }
        } else if (type == 30) {
            tv_order_status1.setText("卖家已发货");
            tv_order_status2.setVisibility(View.VISIBLE);
            tv_order_status2.setText("剩余" + DateUtils.getDistanceTime(DateUtils.getCurrent(), Long.parseLong(modelOrderList.getOrder_confirm_day())) + "自动收货");
            tv_time_pay.setVisibility(View.VISIBLE);
            tv_time_send.setVisibility(View.VISIBLE);
            if (modelOrderList.getIs_refund() == 0) {
                tv_shouhuo.setVisibility(View.VISIBLE);
            } else {
                tv_shouhuo.setVisibility(View.GONE);
            }
            tv_shouhuo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmOrder(modelOrderList.getOrder_id());
                }
            });
        } else if (type == 40) {
            tv_order_status1.setText("交易完成");
            tv_delete.setVisibility(View.VISIBLE);
            tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrder(modelOrderList.getOrder_id());
                }
            });
            if (modelOrderList.getEvaluation_state() == 0) {
                tv_commit.setVisibility(View.VISIBLE);
            } else {
                tv_commit.setVisibility(View.GONE);
            }
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ActivityOrderDetail.this, ActivityCommit.class, null);
                }
            });
            tv_time_pay.setVisibility(View.VISIBLE);
            tv_time_send.setVisibility(View.VISIBLE);
            tv_time_finish.setVisibility(View.VISIBLE);
        } else if (type == 0) {
            tv_order_status1.setText("交易取消");
            tv_delete.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 取消订单
     *
     * @param order_id
     */
    private void cancleOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(ActivityOrderDetail.this, SERVER_URL_SHOP + "&method=jingtu.orders.cancelOrder.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(ActivityOrderDetail.this, "订单取消成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                isBusy=false;
                setResult(FragmentOrderList.requestRefresh);
                finish();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy=false;
                Toast.makeText(ActivityOrderDetail.this, errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy=true;
                LoadingD.showDialog(ActivityOrderDetail.this);
            }
        });
    }

    /**
     * 删除订单
     *
     * @param order_id
     */
    private void deleteOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(ActivityOrderDetail.this, SERVER_URL_SHOP + DELETEORDER);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(ActivityOrderDetail.this, "订单取消成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                isBusy=false;
                setResult(FragmentOrderList.requestRefresh);
                finish();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy=false;
                Toast.makeText(ActivityOrderDetail.this, errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy=true;
                LoadingD.showDialog(ActivityOrderDetail.this);
            }
        });
    }

    /**
     * 确认收货
     *
     * @param order_id
     */
    private void confirmOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(ActivityOrderDetail.this, SERVER_URL_SHOP + "&method=jingtu.orders.confirmOrder.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(ActivityOrderDetail.this, "收货成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                isBusy=false;
                setResult(FragmentOrderList.requestRefresh);
                finish();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy=false;
                Toast.makeText(ActivityOrderDetail.this, errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy=true;
                LoadingD.showDialog(ActivityOrderDetail.this);
            }
        });
    }

    @OnClick({R.id.btn_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(tv_order_sn.getText());
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
        }
    }


    public void registRefresh() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(action_refresh_orderlist);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 刷新订单信息
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(action_refresh_orderlist)) {
                if (intent.getSerializableExtra("modelOrderList") != null) {
                    modelOrderList = (ModelOrderList) intent.getSerializableExtra("modelOrderList");
                    refresh();
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
