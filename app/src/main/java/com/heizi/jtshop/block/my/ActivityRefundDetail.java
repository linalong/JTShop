package com.heizi.jtshop.block.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.DateUtils;
import com.heizi.mycommon.utils.ImageFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 订单详情
 * Created by leo on 17/10/21.
 */

public class ActivityRefundDetail extends BaseSwipeBackCompatActivity implements View.OnClickListener {
    @InjectView(R.id.tv_order_status1)
    TextView tv_order_status1;
    @InjectView(R.id.tv_order_status2)
    TextView tv_order_status2;
    @InjectView(R.id.tv_price1)
    TextView tv_price1;
    @InjectView(R.id.tv_price2)
    TextView tv_price2;

    @InjectView(R.id.tv_remark)
    TextView tv_remark;
    @InjectView(R.id.tv_refund_sn)
    TextView tv_refund_sn;
    @InjectView(R.id.tv_time_add)
    TextView tv_time_add;
    @InjectView(R.id.tv_refund_reason)
    TextView tv_refund_reason;

    @InjectView(R.id.tv_store_im)
    TextView tv_store_im;//店铺客服
    @InjectView(R.id.tv_store_tel)
    TextView tv_store_tel;//店铺电话

    //listview
    @InjectView(R.id.listview)
    ListView listview;
    private List<ModelProductList> listData = new ArrayList<>();

    ModelRefundList modelRefundList;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_refund_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        modelRefundList = (ModelRefundList) getIntent().getExtras().getSerializable("modelRefundList");
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("退款详情");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listData.addAll(modelRefundList.getGoods_info());
        listview.setAdapter(new CommonAdapter(ActivityRefundDetail.this, listData, R.layout.item_order_confirm) {
            @Override
            public void getView(final int position, ViewHolderHelper holder) {
                ImageView iv_product = holder.findViewById(R.id.iv_product);
                TextView tv_product = holder.findViewById(R.id.tv_product);
                TextView tv_size = holder.findViewById(R.id.tv_size);
                TextView tv_price_single = holder.findViewById(R.id.tv_price_single);
                TextView tv_num2 = holder.findViewById(R.id.tv_num2);

                ModelProductList modelProductList = listData.get(position);
                ImageFactory.displayImage(modelProductList.getGoods_image_url(), iv_product, 0, 0);
                tv_num2.setText("x" + modelProductList.getGoods_num());
                tv_price_single.setText(modelProductList.getGoods_price());
                tv_product.setText(modelProductList.getGoods_name());
                if (modelProductList.getSpec_info().size() > 0)
                    tv_size.setText(modelProductList.getSpec_info().get(0));


            }
        });


        if (TextUtils.isEmpty(modelRefundList.getBuyer_message())) {
            tv_remark.setVisibility(View.GONE);
        } else {
            tv_remark.setVisibility(View.VISIBLE);
            tv_remark.setText("用户备注: " + modelRefundList.getBuyer_message());
        }

        tv_price1.setText(modelRefundList.getRefund_amount());


        tv_order_status1.setText(modelRefundList.getRefund_state_des());
        tv_order_status2.setText(DateUtils.timedate(modelRefundList.getSeller_time()));

        tv_refund_sn.setText("退款编号: " + modelRefundList.getRefund_sn());
        tv_time_add.setText("申请时间: " + DateUtils.timedate(modelRefundList.getAdd_time()));
        tv_refund_reason.setText("退款原因: " + modelRefundList.getReason_info());
        tv_price2.setText("退款金额: " + modelRefundList.getRefund_amount());
    }


    @OnClick({R.id.btn_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }


}
