package com.heizi.jtshop.block.my;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.heizi.mycommon.utils.Utils;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by leo on 17/9/26.
 */

public class ActivityGivePoint extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.et_phone)
    EditText et_phone;
    @InjectView(R.id.et_point)
    EditText et_point;
    @InjectView(R.id.et_pwd)
    EditText et_pwd;

    @InjectView(R.id.btn_ok)
    Button btn_ok;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_givepoint;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("积分转赠");
    }

    @OnClick({R.id.btn_back, R.id.btn_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                if (et_phone.getText().toString().trim().equals("")) {
                    Utils.toastShow(ActivityGivePoint.this, "请输入要转赠的手机帐号");
                } else if (!Utils.checkIsCellphone(et_phone.getText().toString().trim())) {
                    Utils.toastShow(ActivityGivePoint.this, "请输入正确的手机号");
                } else if (et_point.getText().toString().trim().equals("") || !Utils.isNumeric(et_point.getText().toString().trim()) && Integer.parseInt(et_point.getText().toString().trim()) > 0) {
                    Utils.toastShow(ActivityGivePoint.this, "转赠数量应为大于0的数字");
                } else if (et_pwd.getText().toString().trim().equals("")) {
                    Utils.toastShow(ActivityGivePoint.this, "请输入登录密码");
                } else {
                    finish();
                }
                break;
        }
    }
}
