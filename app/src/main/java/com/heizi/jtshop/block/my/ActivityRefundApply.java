package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;
import com.lljjcoder.citypickerview.widget.TypePicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by leo on 17/10/24.
 */

public class ActivityRefundApply extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.ll_tuikuan)
    LinearLayout ll_tuikuan;
    @InjectView(R.id.iv_tuikuan)
    ImageView iv_tuikuan;
    @InjectView(R.id.ll_tuihuo)
    LinearLayout ll_tuihuo;
    @InjectView(R.id.v_line)
    View v_line;
    @InjectView(R.id.iv_tuihuo)
    ImageView iv_tuihuo;
    @InjectView(R.id.ll_reason)
    LinearLayout ll_reason;
    @InjectView(R.id.tv_reason)
    TextView tv_reason;
    @InjectView(R.id.et_price)
    EditText et_price;
    @InjectView(R.id.tv_price_most)
    TextView tv_price_most;
    @InjectView(R.id.tv_youfei)
    TextView tv_youfei;
    @InjectView(R.id.et_remark)
    EditText et_remark;
    @InjectView(R.id.btn_ok)
    Button btn_ok;

    ModelOrderList modelOrderList;
    /**
     * 订单类型
     * 20已付款 refund_type只能为1 产品全部退
     * 30已收货 refund_type可为1或2 产品可单退和全退
     */
    int type = 20;
    /**
     * 订单中退货的产品位置 -1为全退
     */
    int position = -1;

    /**
     * refund_type=1为退款
     * refund_type=2为退货
     */
    int refund_type = 1;
    String reason_id = "";
    double price_most = 0.00;

    //listview
    @InjectView(R.id.listview)
    ListView listview;
    private List<ModelProductList> listData = new ArrayList<>();

    TypePicker typePicker;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_refund_before;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        modelOrderList = (ModelOrderList) bundle.getSerializable("modelOrderList");
        type = bundle.getInt("type");
        position = bundle.getInt("position");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("申请退款");

        if (position == -1) {
            listData.addAll(modelOrderList.getOrder_goods());
        } else {
            listData.add(modelOrderList.getOrder_goods().get(position));
        }
        listview.setAdapter(new CommonAdapter(ActivityRefundApply.this, listData, R.layout.item_order_refund) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                ImageView iv_product = holder.findViewById(R.id.iv_product);
                TextView tv_product = holder.findViewById(R.id.tv_product);
                TextView tv_size = holder.findViewById(R.id.tv_size);
                ModelProductList modelProductList = listData.get(position);
                ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                tv_product.setText(modelProductList.getGoods_name());
                if (modelProductList.getSpec_info().size() > 0)
                    tv_size.setText(modelProductList.getSpec_info().get(0));

            }
        });

        if (type == 20) {
            ll_tuihuo.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
            et_price.setEnabled(false);
            et_price.setText(modelOrderList.getOrder_amount());
            tv_price_most.setText(modelOrderList.getOrder_amount());
            price_most = Double.parseDouble(modelOrderList.getOrder_amount());
        } else {
            price_most = Double.parseDouble(modelOrderList.getOrder_goods().get(position).getGoods_price()) * Integer.parseInt(modelOrderList.getOrder_goods().get(position).getGoods_num());
            tv_price_most.setText(price_most + "");
        }
        tv_youfei.setText(modelOrderList.getShipping_fee() + "");

        typePicker = new TypePicker(this);
        typePicker.setOnTypeItemClickListener(new TypePicker.OnTypeItemClickListener() {
            @Override
            public void onSelected(String agencyName, String agencyId) {
                tv_reason.setText(agencyName);
                reason_id = agencyId;
            }

            @Override
            public void onCancel() {

            }
        });

        getReason();
    }

    private void getReason() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + REFUNDREASONList);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    JSONArray jsonArray = new JSONArray(data.json);
                    Map<String, String> map = new HashMap<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        map.put(jsonArray.getJSONObject(i).getString("reason_id"), jsonArray.getJSONObject(i).getString("reason_info"));
                    }
                    typePicker.setData(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LoadingD.hideDialog();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityRefundApply.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                LoadingD.showDialog(ActivityRefundApply.this);
            }
        });
    }

    @OnClick({R.id.ll_tuihuo, R.id.ll_tuikuan, R.id.ll_reason, R.id.btn_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_tuihuo:
                iv_tuihuo.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
                iv_tuikuan.setImageDrawable(getResources().getDrawable(
                        R.mipmap.duik));
                refund_type = 2;
                break;
            case R.id.ll_tuikuan:
                iv_tuikuan.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
                iv_tuihuo.setImageDrawable(getResources().getDrawable(
                        R.mipmap.duik));
                refund_type = 1;
                break;
            case R.id.ll_reason:
                if (typePicker.size() > 0) {
                    typePicker.show();
                }
                break;
            case R.id.btn_ok:
                if (TextUtils.isEmpty(reason_id)) {
                    Utils.toastShow(ActivityRefundApply.this, "请选择退款原因");
                } else {
                    if (type == 20) {
                        refundAllBefore();
                    } else {
                        refundAfter();
                    }
                }
                break;
        }
    }

    /**
     * 发货前全部退款
     */
    private void refundAllBefore() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + REFUNDALLBEFORE);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", modelOrderList.getOrder_id());
        map.put("buyer_message", et_remark.getText().toString());
        map.put("reason_id", reason_id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
                Utils.toastShow(ActivityRefundApply.this, "退款申请提交成功");
                LoadingD.hideDialog();
                modelOrderList.setIs_refund(1);
                for (int i = 0; i < modelOrderList.getOrder_goods().size(); i++) {
                    modelOrderList.getOrder_goods().get(i).setRefund_state_des("退款中");
                }
                Intent intent = new Intent();
                intent.setAction(action_refresh_orderlist);
                intent.putExtra("modelOrderList", modelOrderList);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                Utils.toastShow(ActivityRefundApply.this, errorModel.getMsg());
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityRefundApply.this);
            }
        });
    }

    /**
     * 发货后退款或退货
     */
    private void refundAfter() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + REFUNDAFTER);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", modelOrderList.getOrder_id());
        map.put("buyer_message", et_remark.getText().toString());
        map.put("reason_id", reason_id);
        map.put("goods_id", modelOrderList.getOrder_goods().get(position).getGoods_id() + "");
        map.put("goods_num", modelOrderList.getOrder_goods().get(position).getGoods_num());
        map.put("refund_type", refund_type + "");
        map.put("refund_amount", et_price.getText().toString());
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Utils.toastShow(ActivityRefundApply.this, "退款申请提交成功");
                LoadingD.hideDialog();
                isBusy = false;
                modelOrderList.setIs_refund(1);
                modelOrderList.getOrder_goods().get(position).setRefund_state_des("退款中");
                modelOrderList.getOrder_goods().get(position).setIs_refund(1);
                Intent intent = new Intent();
                intent.setAction(action_refresh_orderlist);
                intent.putExtra("modelOrderList", modelOrderList);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                Utils.toastShow(ActivityRefundApply.this, errorModel.getMsg());
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityRefundApply.this);
            }
        });
    }
}
