package com.heizi.jtshop.block.maidan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseScrollListActivity;
import com.heizi.jtshop.block.my.ActivityAddressList;
import com.heizi.jtshop.block.my.ActivityMyCoupon;
import com.heizi.jtshop.block.my.ModelAddress;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.StringUtils;
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
 * Created by leo on 17/10/5.
 */

public class ActivityOrderConfirm extends BaseScrollListActivity implements View.OnClickListener {

    LinearLayout ll_address, ll_address2, ll_peisong, ll_coupon;
    TextView tv_name, tv_store, tv_tel, tv_address, tv_price, tv_coupon, tv_price3, tv_num, tv_num3, tv_peisong;
    EditText et_remark;
    Button btn_sure;
    ImageView iv_reduce, iv_plus;

    //listview
    private List<ModelOrderProductList> listData = new ArrayList<>();
    private CommonAdapter adapter;


    double couponMoney;//优惠金额
    double condition = -1;//优惠条件
    String couponId = "";//优惠券id

    ModelAddress modelAddress;

    public static int requestCoupon = 1;
    public static int requestAddress = 2;
    public static int requestAddAddress = 100;
    /**
     * //立即购买： 第一个数字为商品编号，第二个数字为购买数量，用竖线分割。例：232|1
     * //购物车购买：第一个数字为购物车编号，第二个数字为购买数量，用竖线分割。多组用半角逗号分割，例：232|1,110|2 232商品购买1个，110商品购买2个
     */
    private String cart_ids = "";
    private int ifcart = 0;//1：购物车立即购买 0：产品详情立即购买
    private ModelSize modelSize;
    ModelOrderConfirm modelOrderConfirm;

    String pay_sn = "";//支付单号


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_confirm;
    }

    @Override
    protected int getLayoutTop() {
        return R.layout.activity_orderconfirm_scroll_top;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            cart_ids = getIntent().getExtras().getString("cart_ids");
            ifcart = getIntent().getExtras().getInt("ifcart");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        ll_address = (LinearLayout) view.findViewById(R.id.ll_address);
        ll_address2 = (LinearLayout) view.findViewById(R.id.ll_address2);
        ll_peisong = (LinearLayout) view.findViewById(R.id.ll_peisong);
        ll_coupon = (LinearLayout) view.findViewById(R.id.ll_coupon);
        tv_store = (TextView) view.findViewById(R.id.tv_store);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_tel = (TextView) view.findViewById(R.id.tv_tel);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_coupon = (TextView) view.findViewById(R.id.tv_coupon);
        tv_price3 = (TextView) view.findViewById(R.id.tv_price3);
        tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_num3 = (TextView) view.findViewById(R.id.tv_num3);
        tv_peisong = (TextView) view.findViewById(R.id.tv_peisong);
        ll_address.setOnClickListener(this);
        ll_address2.setOnClickListener(this);
        ll_coupon.setOnClickListener(this);

    }

    @Override
    protected void initViewTop() {
        super.initViewTop();
        tv_price = (TextView) findViewById(R.id.tv_price);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);
        tv_title.setText("确认订单");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mScrollView.setAutoLoadEnable(false);
        mScrollView.setPullLoadEnable(false);
        mScrollView.setPullRefreshEnable(false);
        adapter = new CommonAdapter(this, listData, R.layout.item_order_confirm) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                ImageView iv_product = holder.findViewById(R.id.iv_product);
                TextView tv_product = holder.findViewById(R.id.tv_product);
                TextView tv_size = holder.findViewById(R.id.tv_size);
                TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                TextView tv_num2 = holder.findViewById(R.id.tv_num2);
                ImageFactory.displayImage(listData.get(position).getGoods_image_url(), iv_product, 0, 0);
                tv_num2.setText("x" + listData.get(position).getGoods_num());
                tv_price_single.setText(listData.get(position).getGoods_price());
                tv_product.setText(listData.get(position).getGoods_name());
                tv_size.setText(listData.get(position).getSpec_info());
            }
        };
        mListView.setAdapter(adapter);
        postOrder1();
    }

    /**
     * 订单第一步,根据产品和数量获取收货地址,邮费等信息
     */
    private void postOrder1() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.orders.buyStep1.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("cart_ids", cart_ids);
        map.put("ifcart", ifcart + "");
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                if (isExist) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.json);
                        JSONObject jsonObject1 = jsonObject.getJSONArray("store_cart_list").getJSONObject(0);
                        JSONArray jsonArray = new JSONArray(jsonObject1.getString("goods_list"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelOrderProductList modelOrderProductList = new ModelOrderProductList();
                            modelOrderProductList.setGoods_id(jsonArray.getJSONObject(i).getString("goods_id"));
                            modelOrderProductList.setGoods_name(jsonArray.getJSONObject(i).getString("goods_name"));
                            modelOrderProductList.setGoods_image_url(jsonArray.getJSONObject(i).getString("goods_image_url"));
                            modelOrderProductList.setGoods_num(jsonArray.getJSONObject(i).getString("goods_num"));
                            modelOrderProductList.setGoods_price(jsonArray.getJSONObject(i).getString("goods_price"));
                            modelOrderProductList.setSpec_info(jsonArray.getJSONObject(i).getJSONArray("spec_info").getString(0));
                            listData.add(modelOrderProductList);
                        }
                        adapter.notifyDataSetChanged();
                        measureListHeight();
                        Gson gson = new Gson();
                        try {
                            modelAddress = gson.fromJson(jsonObject.getString("address_info"),
                                    ModelAddress.class);
                        } catch (Exception e) {

                        }

                        modelOrderConfirm = new ModelOrderConfirm();
                        modelOrderConfirm.setOrder_freight(jsonObject.getDouble("order_freight"));
                        modelOrderConfirm.setFreight_hash(jsonObject.getString("freight_hash"));
                        modelOrderConfirm.setGoods_total(jsonObject.getDouble("goods_total"));
                        modelOrderConfirm.setStore_name(jsonObject1.getString("store_name"));
                        modelOrderConfirm.setStore_id(jsonObject1.getString("store_id"));
                        modelOrderConfirm.setStore_goods_num(jsonObject1.getString("store_goods_num"));
                        modelOrderConfirm.setVat_hash(jsonObject.getString("vat_hash"));
                        if (modelOrderConfirm.getOrder_freight() == 0) {
                            tv_peisong.setText("快递 免邮");
                        } else {
                            tv_peisong.setText("运费: " + modelOrderConfirm.getOrder_freight() + " 元");
                        }
                        tv_store.setText(modelOrderConfirm.getStore_name());
                        tv_num3.setText(modelOrderConfirm.getStore_goods_num());
                        update();

                        if (modelAddress != null) {
                            tv_address.setText(modelAddress.getArea_info() + " " + modelAddress.getAddress());
                            tv_name.setText(modelAddress.getTrue_name());
                            tv_tel.setText(modelAddress.getMob_phone());
                            ll_address.setVisibility(View.VISIBLE);
                            ll_address2.setVisibility(View.GONE);
                        } else {
                            ll_address.setVisibility(View.GONE);
                            ll_address2.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setViewEnable(true);
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Utils.toastShow(ActivityOrderConfirm.this, errorModel.getMsg());
                setViewEnable(true);
            }

            @Override
            public void onStart() {
                setViewEnable(false);
            }
        });
    }

    /**
     * 订单第二步
     */
    private void postOrder2() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.orders.buyStep2.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("cart_ids", cart_ids);
        map.put("ifcart", ifcart + "");
        map.put("invoice_id", "0");
        if (!couponId.equals(""))
            map.put("voucher", couponId + "|" + modelOrderConfirm.getStore_id() + "|" + couponMoney);
        map.put("vat_hash", modelOrderConfirm.getVat_hash());
        map.put("address_id", modelAddress.getAddress_id());
        map.put("pay_name", "online");
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                if (isExist) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.json);
                        pay_sn = jsonObject.getString("pay_sn");
                        Toast.makeText(ActivityOrderConfirm.this, "订单创建成功", Toast.LENGTH_SHORT).show();
                        new AlertPayPwd(ActivityOrderConfirm.this, userModel.getMember_paypwd(), new AlertPayPwd.pwdCallback() {
                            @Override
                            public void call() {
                                generateOrder(pay_sn);
                            }

                            @Override
                            public void close() {
                                finish();
                            }
                        }).show();
                        //刷新购物车
                        if (ifcart == 1) {
                            Intent intent = new Intent();
                            intent.setAction(action_refresh_cartlist);
                            sendBroadcast(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setViewEnable(true);
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Utils.toastShow(ActivityOrderConfirm.this, errorModel.getMsg());
                setViewEnable(true);
            }

            @Override
            public void onStart() {
                setViewEnable(false);
            }
        });
    }


    /**
     * 生成订单
     *
     * @param pay_sn 支付单号
     */
    private void generateOrder(String pay_sn) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.orders.changePaymentMethod.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("pay_sn", pay_sn);
        map.put("payment_code", "predeposit");
        map.put("paypassword", userModel.getMember_paypwd());
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Utils.toastShow(ActivityOrderConfirm.this, "购买成功!");
                setViewEnable(true);
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                Utils.toastShow(ActivityOrderConfirm.this, errorModel.getMsg());
                setViewEnable(true);
            }

            @Override
            public void onStart() {
                setViewEnable(false);
            }
        });
    }

    /**
     * 修改收货地址
     */
    private void changeAddress() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.orders.changeAddress.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("freight_hash", modelOrderConfirm.getFreight_hash());
        map.put("city_id", modelAddress.getCity_id());
        map.put("area_id", modelAddress.getArea_id());
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                if (isExist) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.json);
                        modelOrderConfirm.setOrder_freight(jsonObject.getJSONArray("content").getJSONObject(0).getDouble("freight"));
                        update();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setViewEnable(true);
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                setViewEnable(true);
                Utils.toastShow(ActivityOrderConfirm.this, "地址修改失败");
            }

            @Override
            public void onStart() {
                setViewEnable(false);

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (modelOrderConfirm == null) {
            Utils.toastShow(ActivityOrderConfirm.this, "未获取到订单信息");
        } else {
            switch (v.getId()) {
                case R.id.btn_sure:
                    if (modelOrderConfirm == null) {
                        Utils.toastShow(ActivityOrderConfirm.this, "未获取到订单信息");
                        return;
                    } else if (modelAddress == null) {
                        Utils.toastShow(ActivityOrderConfirm.this, "未添加收货地址");
                        return;
                    } else if (TextUtils.isEmpty(userModel.getMember_paypwd())) {
                        new AlertSetPaypwd(ActivityOrderConfirm.this).show();
                        return;
                    } else if (userModel.getAvailable_predeposit() < modelOrderConfirm.getGoods_total()) {
                        new AlertRecharge(ActivityOrderConfirm.this).show();
                        return;
                    }
                    postOrder2();
                    break;
//            case R.id.iv_reduce:
//                if (num != 1) {
//                    --num;
//                    update();
//                }
//                break;
//            case R.id.iv_plus:
//                ++num;
//                update();
//                break;
                case R.id.ll_address:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isChoice", true);
                    if (modelAddress != null) {
                        bundle.putString("address_id", modelAddress.getAddress_id());
                    }
                    startActivityForResult(ActivityOrderConfirm.this, ActivityAddressList.class, bundle, requestAddress);
                    break;
                case R.id.ll_address2:
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean("isChoice", true);
                    startActivityForResult(ActivityOrderConfirm.this, ActivityAddressList.class, bundle2, requestAddress);
//                Bundle bundle2 = new Bundle();
//                bundle2.putBoolean("isAdd", true);
//                startActivityForResult(ActivityOrderConfirm.this, ActivityAddressEdit.class, bundle2, requestAddAddress);
                    break;

                case R.id.ll_coupon:
                    Bundle bundlel2 = new Bundle();
                    bundlel2.putInt("state", 2);
                    bundlel2.putString("store_goods_total", modelOrderConfirm.getStore_id() + "|" + modelOrderConfirm.getGoods_total());
                    startActivityForResult(this, ActivityMyCoupon.class, bundlel2, requestCoupon);
                    break;
            }
        }
    }

    /**
     * 获取订单详情和选择优惠券后更新价格
     */
    private void update() {
//        tv_num.setText(num + "");
//        tv_num3.setText(num + "");
        //无条件优惠
        if (condition == 0) {
            tv_price.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() - couponMoney + "") + "");
            tv_price3.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() - couponMoney + modelOrderConfirm.getOrder_freight() + "") + "");
        } else {
            tv_price.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() - couponMoney + modelOrderConfirm.getOrder_freight() + "") + "");
            tv_price3.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() - couponMoney + modelOrderConfirm.getOrder_freight() + "") + "");
//            if (modelOrderConfirm.getGoods_total() > condition) {
//
//            } else {
//                tv_price.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() + "") + "");
//                tv_price3.setText(StringUtils.getDouble(modelOrderConfirm.getGoods_total() + "") + "");
//            }
        }
    }

    private void setViewEnable(boolean enable) {
        if (isExist) {
            if (enable) {
                isBusy = false;
                ll_address.setEnabled(true);
                ll_address2.setEnabled(true);
                btn_sure.setEnabled(true);
                LoadingD.hideDialog();
            } else {
                isBusy = true;
                ll_address.setEnabled(false);
                ll_address2.setEnabled(false);
                btn_sure.setEnabled(false);
                LoadingD.showDialog(ActivityOrderConfirm.this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCoupon) {
            if (data != null) {
                couponMoney = Double.parseDouble(data.getStringExtra("money"));
                condition = Double.parseDouble(data.getStringExtra("condition"));
                if (condition == 0) {
                    tv_coupon.setText("省" + couponMoney);
                } else {
                    tv_coupon.setText("省" + couponMoney + "   满" + condition + "可用");
                }
                couponId = data.getStringExtra("couponId");
                update();
            }
        } else if (requestCode == requestAddress) {
            if (data != null) {
                //选择了地址会返回实体,删除了当前地址地址会返回null
                modelAddress = (ModelAddress) data.getSerializableExtra("modelAddress");
                if (modelAddress != null) {
                    ll_address2.setVisibility(View.GONE);
                    ll_address.setVisibility(View.VISIBLE);
                    tv_address.setText(modelAddress.getArea_info() + " " + modelAddress.getAddress());
                    tv_name.setText(modelAddress.getTrue_name());
                    tv_tel.setText(modelAddress.getMob_phone());
                    changeAddress();
                } else {
                    ll_address2.setVisibility(View.VISIBLE);
                    ll_address.setVisibility(View.GONE);
                }
            }
        }
//        else if (requestCode == requestAddAddress) {
//            if (data != null) {
//                ll_address2.setVisibility(View.GONE);
//                ll_address.setVisibility(View.VISIBLE);
//                modelAddress = (ModelAddress) data.getSerializableExtra("modelAddress");
//                tv_address.setText(modelAddress.getArea_info() + " " + modelAddress.getAddress());
//                tv_name.setText(modelAddress.getTrue_name());
//                tv_tel.setText(modelAddress.getMob_phone());
//            }
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
