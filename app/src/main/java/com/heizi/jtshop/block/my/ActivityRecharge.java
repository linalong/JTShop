package com.heizi.jtshop.block.my;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;

/**
 * Created by leo on 17/10/13.
 */

public class ActivityRecharge extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    private ImageView iv_zfb, iv_yl;
    private TextView tv_zfb, tv_yl;
    private LinearLayout ll_zfb, ll_wx;
    private EditText et_money;
    private Button btn_next;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_recharge;
    }


    @Override
    protected void initView() {
        super.initView();

        tv_title.setText("充值");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_yl = (TextView) findViewById(R.id.tv_yl);
        tv_zfb = (TextView) findViewById(R.id.tv_zfb);

        ll_zfb = (LinearLayout) findViewById(R.id.ll_zfb);
        ll_zfb.setOnClickListener(this);
        ll_wx = (LinearLayout) findViewById(R.id.ll_wx);
        ll_wx.setOnClickListener(this);

        iv_zfb = (ImageView) findViewById(R.id.iv_zfb);
        iv_yl = (ImageView) findViewById(R.id.iv_yl);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:

                break;
            case R.id.ll_zfb:
                tv_zfb.setTextColor(getResources().getColor(R.color.black2));
                iv_zfb.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
                tv_yl.setTextColor(getResources().getColor(
                        R.color.gray10));
                iv_yl.setImageDrawable(getResources().getDrawable(
                        R.mipmap.duik));
                break;
            case R.id.ll_wx:
                tv_zfb.setTextColor(getResources().getColor(R.color.gray10));
                iv_zfb.setImageDrawable(getResources().getDrawable(
                        R.mipmap.duik));
                tv_yl.setTextColor(getResources().getColor(
                        R.color.black2));
                iv_yl.setImageDrawable(getResources().getDrawable(
                        R.mipmap.dui));
                break;
            default:
                break;
        }
    }
}
