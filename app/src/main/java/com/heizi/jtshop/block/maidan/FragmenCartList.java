package com.heizi.jtshop.block.maidan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.home.ActivityStore;
import com.heizi.jtshop.fragment.BaseListFragment;
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
 * 购物车列表
 * Created by leo on 17/5/17.
 */

public class FragmenCartList extends BaseListFragment {


    private LinearLayout ll_notice;
    private List<ModelCartList> listData = new ArrayList<>();
    private CommonAdapter adapter;
    double price = 0;//订单金额
    int num = 0;//订单数量
    TextView tv_price;
    Button btn_sure;

    ParseStringProtocol parseStringProtocol;
    IResponseCallback<DataSourceModel<String>> callback;


    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        registRefresh();
        return super.onCreateView(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cart_list;
    }


    @Override
    protected void initView(final View v) {
        super.initView(v);
        tv_title.setText("购物车");
        btn_back.setVisibility(View.GONE);
        ll_notice = (LinearLayout) v.findViewById(R.id.ll_notice);
        tv_price = (TextView) v.findViewById(R.id.tv_price);
        btn_sure = (Button) v.findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num == 0) {
                    Utils.toastShow(getActivity(), "未选中任何商品");
                } else {
                    String cart_ids = "";
                    for (int i = 0; i < listData.size(); i++) {
                        for (int j = 0; j < listData.get(i).getGoods_list().size(); j++) {
                            if (listData.get(i).getGoods_list().get(j).getDelete_state() == 2) {
                                cart_ids += "," + listData.get(i).getGoods_list().get(j).getCart_id() + "|" + listData.get(i).getGoods_list().get(j).getGoods_num();
                            }
                        }
                        if (!cart_ids.equals(""))
                            break;
                    }
                    if (cart_ids.length() > 0) {
                        cart_ids = cart_ids.substring(1, cart_ids.length());
                    }
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("cart_ids", cart_ids);
                    bundle1.putInt("ifcart", 1);
                    startActivity(getActivity(), ActivityOrderConfirm.class, bundle1);
                }
            }
        });
        //listview
        adapter = new CommonAdapter(getActivity(), listData, R.layout.item_cart) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                LinearLayout ll_shop = holder.findViewById(R.id.ll_shop);
                View view_divider = holder.findViewById(R.id.view_divider);
                if (position == 0) {
                    view_divider.setVisibility(View.GONE);
                } else {
                    view_divider.setVisibility(View.VISIBLE);
                }
                TextView tv_shop_name = holder.findViewById(R.id.tv_shop_name);
                tv_shop_name.setText(listData.get(position).getStore_name());

                final ImageView iv_dui = holder.findViewById(R.id.iv_dui);
                if (listData.get(position).getDelete_state() == 1) {
                    iv_dui.setImageDrawable(getResources().getDrawable(R.mipmap.duik));
                } else {
                    iv_dui.setImageDrawable(getResources().getDrawable(R.mipmap.dui));
                }
                iv_dui.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listData.get(position).getDelete_state() == 1) {
                            //店铺层保留一个选中
                            for (int i = 0; i < listData.size(); i++) {
                                //首先判断某个店铺是否有选中,有选中清除店铺和产品选中
                                if (listData.get(i).getDelete_state() == 2) {
                                    listData.get(i).setDelete_state(1);
                                    //清空店铺时,清空下面的的产品
                                    for (int j = 0; j < listData.get(i).getGoods_list().size(); j++) {
                                        listData.get(i).getGoods_list().get(j).setDelete_state(1);
                                    }
                                    break;
                                } else {
                                    //判断店铺下产品是否有选中
                                    boolean hasProduct = false;
                                    for (int j = 0; j < listData.get(i).getGoods_list().size(); j++) {
                                        if (listData.get(i).getGoods_list().get(j).getDelete_state() == 2) {
                                            listData.get(i).getGoods_list().get(j).setDelete_state(1);
                                            hasProduct = true;
                                        }
                                    }
                                    if (hasProduct)
                                        break;
                                }
                            }
                            listData.get(position).setDelete_state(2);
                            price = 0;
                            //选中店铺下所有产品
                            for (int i = 0; i < listData.get(position).getGoods_list().size(); i++) {
                                listData.get(position).getGoods_list().get(i).setDelete_state(2);
                                price += Double.parseDouble(listData.get(position).getGoods_list().get(i).getGoods_price());
                            }
                            num = listData.get(position).getGoods_list().size();

                        } else {
                            //清空店铺,及下面所有产品
                            listData.get(position).setDelete_state(1);
                            for (int i = 0; i < listData.get(position).getGoods_list().size(); i++) {
                                listData.get(position).getGoods_list().get(i).setDelete_state(1);
                            }
                            num = 0;
                            price = 0;
                            iv_dui.setImageDrawable(getResources().getDrawable(R.mipmap.duik));
                        }
                        adapter.notifyDataSetChanged();
                        refreshPrice();
                    }
                });

                ll_shop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(getActivity(), ActivityStore.class, null);
                    }
                });

                tv_shop_name.setText(listData.get(position).getStore_name());
                /**
                 * 产品列表
                 */
                ListView lv = holder.findViewById(R.id.lv);
//                final List<ModelProductList> data = new ArrayList<>();
//                data.addAll(listData.get(position).getGoods_list());
                CommonAdapter adapter2 = new CommonAdapter(mActivity, listData.get(position).getGoods_list(), R.layout.item_order_confirm) {
                    @Override
                    public void getView(final int position2, ViewHolderHelper holder) {
                        ImageView iv_product = holder.findViewById(R.id.iv_product);
                        final ImageView iv_dui_product = holder.findViewById(R.id.iv_dui);
                        iv_dui_product.setVisibility(View.VISIBLE);
                        TextView tv_product = holder.findViewById(R.id.tv_product);
                        TextView tv_size = holder.findViewById(R.id.tv_size);
                        TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                        final TextView tv_num2 = holder.findViewById(R.id.tv_num2);
                        LinearLayout ll_product = holder.findViewById(R.id.ll_product);

                        final ModelProductList modelProductList = listData.get(position).getGoods_list().get(position2);
                        ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                        tv_num2.setText("x" + modelProductList.getGoods_num());
                        tv_price_single.setText(modelProductList.getGoods_price());
                        tv_product.setText(modelProductList.getGoods_name());
                        if (modelProductList.getSpec_info().size() > 0)
                            tv_size.setText(modelProductList.getSpec_info().get(0));

                        ll_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("goods_id", modelProductList.getGoods_id());
                                startActivity(getActivity(), ActivityProductDetail.class, bundle);
                            }
                        });
                        if (listData.get(position).getGoods_list().get(position2).getDelete_state() == 1) {
                            iv_dui_product.setImageDrawable(getResources().getDrawable(R.mipmap.duik));
                        } else {
                            iv_dui_product.setImageDrawable(getResources().getDrawable(R.mipmap.dui));
                        }
                        iv_dui_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listData.get(position).getGoods_list().get(position2).getDelete_state() == 1) {

                                    //判断此店铺中是否还有被选中的(除了自己),无选中的的话,选中此产品,并将其他店铺状态清空。有选中说明别的店铺无选中,不需要清空
                                    boolean has = false;//是否有被选中
                                    boolean hasAll = true;//是否全部选中
                                    for (int i = 0; i < listData.get(position).getGoods_list().size(); i++) {
                                        if (listData.get(position).getGoods_list().get(i).getDelete_state() == 2) {
                                            has = true;
                                        } else {
                                            if (i != position2)
                                                hasAll = false;
                                        }
                                    }
                                    //此店铺中产品全部选中,则将店铺也选中
                                    if (hasAll) {
                                        listData.get(position).setDelete_state(2);
                                        iv_dui.setImageDrawable(getResources().getDrawable(R.mipmap.dui));
                                    }
                                    if (!has) {
                                        for (int i = 0; i < listData.size(); i++) {
                                            //不是当前点击店铺
                                            if (i != position) {
                                                //首先判断某个店铺是否有选中,有选中清除店铺和产品选中
                                                if (listData.get(i).getDelete_state() == 2) {
                                                    listData.get(i).setDelete_state(1);
                                                    //清空店铺时,清空下面的的产品
                                                    for (int j = 0; j < listData.get(i).getGoods_list().size(); j++) {
                                                        listData.get(i).getGoods_list().get(j).setDelete_state(1);
                                                    }
                                                    break;
                                                } else {
                                                    //判断店铺下产品是否有选中
                                                    boolean hasProduct = false;
                                                    for (int j = 0; j < listData.get(i).getGoods_list().size(); j++) {
                                                        if (listData.get(i).getGoods_list().get(j).getDelete_state() == 2) {
                                                            listData.get(i).getGoods_list().get(j).setDelete_state(1);
                                                            hasProduct = true;
                                                        }
                                                    }
                                                    if (hasProduct)
                                                        break;
                                                }
                                            }
                                        }
                                        num = 0;
                                        price = 0;
                                        adapter.notifyDataSetChanged();
                                    }

                                    iv_dui_product.setImageDrawable(getResources().getDrawable(R.mipmap.dui));
                                    listData.get(position).getGoods_list().get(position2).setDelete_state(2);
                                    price += Double.parseDouble(listData.get(position).getGoods_list().get(position2).getGoods_price());
                                    num++;
                                } else {
                                    iv_dui_product.setImageDrawable(getResources().getDrawable(R.mipmap.duik));
                                    listData.get(position).getGoods_list().get(position2).setDelete_state(1);
                                    price -= Double.parseDouble(listData.get(position).getGoods_list().get(position2).getGoods_price());
                                    num--;
                                    //店铺中产品不是全部选中时,清空店铺状态
                                    iv_dui.setImageDrawable(getResources().getDrawable(R.mipmap.duik));

                                }
                                refreshPrice();
                            }
                        });


                    }
                };
                lv.setAdapter(adapter2);
                measureListHeight(lv);
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
        mListView.setAutoLoadEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setAdapter(adapter);
    }

    /**
     * 刷新价格和数量
     */
    private void refreshPrice() {
        tv_price.setText(price + "");
        btn_sure.setText("结算(" + num + ")");
    }


    @Override
    protected void initData() {
        super.initData();
        parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + CARTLIST);
        callback = new IResponseCallback<DataSourceModel<String>>() {
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
                            ModelCartList modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelCartList.class);
                            listData.add(modelPano);
                        }
                        adapter.notifyDataSetChanged();
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


    }


    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("token", userModel.getToken());
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
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Toast.makeText(getActivity(), errorModel.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
            }
        });
    }


    public void registRefresh() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(action_refresh_cartlist);
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
            if (action.equals(action_refresh_cartlist)) {
                onRefresh();
                num = 0;
                price = 0;
                refreshPrice();
            }
        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
