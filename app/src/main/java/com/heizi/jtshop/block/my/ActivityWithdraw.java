package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.xutils.common.util.MD5;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 提现
 * Created by leo on 17/10/13.
 */

public class ActivityWithdraw extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.tv_yue)
    TextView tv_yue;
    @InjectView(R.id.tv_bank)
    TextView tv_bank;
    @InjectView(R.id.ll_bank)
    LinearLayout ll_bank;
    @InjectView(R.id.et_money)
    EditText et_money;
    @InjectView(R.id.et_pwd)
    EditText et_pwd;
    @InjectView(R.id.btn_ok)
    Button btn_ok;
    public static int requestBank = 121;

    private String id = "", name = "";


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("提现");
        tv_yue.setText(userModel.getAvailable_predeposit() + "");
    }


    private void post() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + WITHDRAW);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("pdc_amount", et_money.getText().toString());
        map.put("password", MD5.md5(et_pwd.getText().toString()));
        map.put("pdc_id", id);
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
                LoadingD.hideDialog();
                Utils.toastShow(ActivityWithdraw.this, "申请成功,请等待审核");
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                LoadingD.hideDialog();
                Utils.toastShow(ActivityWithdraw.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityWithdraw.this);
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.btn_ok, R.id.ll_bank})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                if (isBusy) {
                    Utils.toastShow(ActivityWithdraw.this, getResources().getString(R.string.request_string));
                } else if (TextUtils.isEmpty(et_money.getText().toString())) {
                    Utils.toastShow(ActivityWithdraw.this, "请输入提现金额");
                } else if (Double.parseDouble(et_money.getText().toString()) > userModel.getAvailable_predeposit()) {
                    Utils.toastShow(ActivityWithdraw.this, "提现金额需小于账户余额");
                } else if (TextUtils.isEmpty(et_pwd.getText().toString())) {
                    Utils.toastShow(ActivityWithdraw.this, "请输入提现密码");
                } else if (TextUtils.isEmpty(id)) {
                    Utils.toastShow(ActivityWithdraw.this, "请选择银行卡");
                } else {
                    post();
                }
                break;
            case R.id.ll_bank:
                Bundle bundlel2 = new Bundle();
                bundlel2.putInt("state", 2);
                startActivityForResult(this, ActivityBankList.class, bundlel2, requestBank);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestBank) {
            id = data.getStringExtra("id");
            name = data.getStringExtra("name");
            tv_bank.setText(name);
        }
    }
}
