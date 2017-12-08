package com.heizi.jtshop.block.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.home.ActivityStore;
import com.heizi.jtshop.block.maidan.ActivityCommit;
import com.heizi.jtshop.block.maidan.AlertPayPwd;
import com.heizi.jtshop.block.maidan.AlertRecharge;
import com.heizi.jtshop.block.maidan.AlertSetPaypwd;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.jtshop.fragment.BaseListFragment;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
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
 * Created by leo on 17/5/17.
 */

public class FragmentOrderList extends BaseListFragment {


    private LinearLayout ll_notice;
    private List<ModelOrderList> listData = new ArrayList<>();
    private CommonAdapter adapter;

    ParseStringProtocol parseStringProtocol;
    IResponseCallback<DataSourceModel<String>> callback;

    public static int requestRefresh = 100;//刷新列表

    /**
     * -1 全部
     * 0 取消订单
     * 10待付款
     * 20待发货
     * 30待收货
     * 40待评价
     */
    private int type;


    private Handler mHandler = new Handler();

    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        registRefresh();
        return super.onCreateView(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_orderlist;
    }


    @Override
    protected void initView(View v) {
        super.initView(v);
        ll_notice = (LinearLayout) v.findViewById(R.id.ll_notice);
        //listview
        adapter = new CommonAdapter(getActivity(), listData, R.layout.item_order) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                LinearLayout ll_shop = holder.findViewById(R.id.ll_shop);
                TextView tv_shop_name = holder.findViewById(R.id.tv_shop_name);
                TextView tv_order_status = holder.findViewById(R.id.tv_order_status);
                TextView tv_num3 = holder.findViewById(R.id.tv_num3);
                TextView tv_price = holder.findViewById(R.id.tv_price);
                TextView tv_yunfei = holder.findViewById(R.id.tv_yunfei);


                TextView tv_kefu = holder.findViewById(R.id.tv_kefu);
                TextView tv_cancel = holder.findViewById(R.id.tv_cancel);
                TextView tv_pay = holder.findViewById(R.id.tv_pay);
                TextView tv_tixing = holder.findViewById(R.id.tv_tixing);
                TextView tv_shouhuo = holder.findViewById(R.id.tv_shouhuo);
                TextView tv_delete = holder.findViewById(R.id.tv_delete);
                TextView tv_commit = holder.findViewById(R.id.tv_commit);
                if (listData.get(position).getOrder_state() == 10) {
                    tv_kefu.setVisibility(View.VISIBLE);
                    tv_cancel.setVisibility(View.VISIBLE);
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancleOrder(listData.get(position).getOrder_id());
                        }
                    });
                    tv_pay.setVisibility(View.VISIBLE);
                    tv_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(userModel.getMember_paypwd())) {
                                new AlertSetPaypwd(getActivity()).show();
                            } else if (userModel.getAvailable_predeposit() < Double.parseDouble(listData.get(position).getGoods_amount())) {
                                new AlertRecharge(getActivity()).show();
                            } else {
                                new AlertPayPwd(getActivity(), userModel.getMember_paypwd(), new AlertPayPwd.pwdCallback() {
                                    @Override
                                    public void call() {
                                        generateOrder(listData.get(position).getPay_sn());
                                    }

                                    @Override
                                    public void close() {

                                    }
                                }).show();
                            }
                        }
                    });
                } else if (listData.get(position).getOrder_state() == 20) {
                    tv_tixing.setVisibility(View.VISIBLE);
                    tv_tixing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "发送发货消息提醒成功!", Toast.LENGTH_SHORT).show();
                                }
                            }, 500);
                        }
                    });
                } else if (listData.get(position).getOrder_state() == 30) {
                    if (listData.get(position).getIs_refund() == 0) {
                        tv_shouhuo.setVisibility(View.VISIBLE);
                        tv_shouhuo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmOrder(listData.get(position).getOrder_id());
                            }
                        });
                    } else {
                        tv_shouhuo.setVisibility(View.GONE);
                    }
                } else if (listData.get(position).getOrder_state() == 40) {
                    tv_delete.setVisibility(View.VISIBLE);
                    tv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteOrder(listData.get(position).getOrder_id());
                        }
                    });
                    if (listData.get(position).getEvaluation_state() == 0) {
                        tv_commit.setVisibility(View.VISIBLE);
                    } else {
                        tv_commit.setVisibility(View.GONE);
                    }
                    tv_commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("modelOrderList", listData.get(position));
                            startActivity(getActivity(), ActivityCommit.class, bundle);
                        }
                    });
                }

                ll_shop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(getActivity(), ActivityStore.class, null);
                    }
                });

                tv_shop_name.setText(listData.get(position).getStore_name());
                tv_order_status.setText(listData.get(position).getState_desc());
                tv_price.setText(listData.get(position).getOrder_amount());
                tv_num3.setText(listData.get(position).getGoods_num_amount());
                tv_yunfei.setText(listData.get(position).getShipping_fee() + "");
                /**
                 * 产品列表
                 */
                ListView lv = holder.findViewById(R.id.lv);
                final List<ModelProductList> data = new ArrayList<>();
                data.addAll(listData.get(position).getOrder_goods());
                CommonAdapter adapter = new CommonAdapter(mActivity, data, R.layout.item_order_confirm) {
                    @Override
                    public void getView(int position2, ViewHolderHelper holder) {
                        ImageView iv_product = holder.findViewById(R.id.iv_product);
                        TextView tv_product = holder.findViewById(R.id.tv_product);
                        TextView tv_size = holder.findViewById(R.id.tv_size);
                        TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                        TextView tv_num2 = holder.findViewById(R.id.tv_num2);
                        //退款描述
                        TextView tv_tuikuan_state = holder.findViewById(R.id.tv_tuikuan_state);

                        ModelProductList modelProductList = listData.get(position).getOrder_goods().get(position2);
                        ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                        tv_num2.setText("x" + modelProductList.getGoods_num());
                        tv_price_single.setText(modelProductList.getGoods_price());
                        tv_product.setText(modelProductList.getGoods_name());
                        if (modelProductList.getSpec_info().size() > 0)
                            tv_size.setText(modelProductList.getSpec_info().get(0));

                        //退款相关
                        if (listData.get(position).getIs_refund() == 1) {
                            tv_tuikuan_state.setVisibility(View.VISIBLE);
                            tv_tuikuan_state.setText(listData.get(position).getOrder_goods().get(position2).getRefund_state_des());
                        } else {
                            tv_tuikuan_state.setVisibility(View.GONE);
                        }

                    }
                };
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position3, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modelOrderList", listData.get(position));
                        startActivityForResult(getActivity(), ActivityOrderDetail.class, bundle, requestRefresh);
                    }
                });

//                LinearLayout ll_main = holder.findViewById(R.id.ll_main);
//                ll_main.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("modelOrderList", listData.get(position));
//                        startActivityForResult(getActivity(), ActivityOrderDetail.class, bundle, requestRefresh);
//                    }
//                });

            }
        };
        mListView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        super.initData();
        parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.orders.memberOrderList.get/");
        callback = new IResponseCallback<DataSourceModel<String>>() {
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
                            ModelOrderList modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelOrderList.class);
                            modelPano.setAddress(jsonArray.getJSONObject(i).getJSONObject("reciver_info").getString("address"));
                            modelPano.setReciver_name(jsonArray.getJSONObject(i).getJSONObject("reciver_info").getString("reciver_name"));
                            modelPano.setMob_phone(jsonArray.getJSONObject(i).getJSONObject("reciver_info").getString("mob_phone"));
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

            }

            @Override
            public void onStart() {

            }
        };
        if (listData.size() == 0)
            getData();

        //fragment重新加载
        if (!hasMore) {
            mListView.setPullLoadEnable(false);
        }


    }


    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("token", userModel.getToken());
        if (type != -1) {
            maps.put("order_state", type + "");
        }
        maps.put("pagesize", pageSize + "");
        maps.put("curpage", pageIndex + "");
        if (parseStringProtocol != null)
            parseStringProtocol.getData(maps, callback);
    }

    /**
     * 取消订单
     *
     * @param order_id
     */
    private void cancleOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.orders.cancelOrder.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(getActivity(), "订单取消成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                onRefresh();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                Toast.makeText(getActivity(), errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(getActivity());
            }
        });
    }

    /**
     * 删除订单
     *
     * @param order_id
     */
    private void deleteOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + DELETEORDER);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(getActivity(), "订单删除成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                onRefresh();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                Toast.makeText(getActivity(), errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(getActivity());
            }
        });
    }

    /**
     * 确认收货
     *
     * @param order_id
     */
    private void confirmOrder(String order_id) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.orders.confirmOrder.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", order_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(getActivity(), "收货成功!", Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
                refreshAll();
            }


            @Override
            public void onFailure(ErrorModel errorModel) {
                Toast.makeText(getActivity(), errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(getActivity());
            }
        });
    }

    /**
     * 生成订单
     *
     * @param pay_sn 支付单号
     */
    private void generateOrder(String pay_sn) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.orders.changePaymentMethod.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("pay_sn", pay_sn);
        map.put("payment_code", "predeposit");
        map.put("paypassword", userModel.getMember_paypwd());
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Toast.makeText(getActivity(), "购买成功!", Toast.LENGTH_SHORT).show();
                isBusy = false;
                LoadingD.hideDialog();
                refreshAll();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Toast.makeText(getActivity(), errorModel.getMsg(), Toast.LENGTH_SHORT).show();
                isBusy = false;
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(getActivity());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestRefresh) {
            onRefresh();
        }
    }

    public void refreshAll() {
        Intent intent = new Intent();
        intent.setAction(action_refresh_orderlist);
        getActivity().sendBroadcast(intent);
    }

    public void registRefresh() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(action_refresh_orderlist);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 刷新用户信息的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(action_refresh_orderlist)) {
                onRefresh();
            }
        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
