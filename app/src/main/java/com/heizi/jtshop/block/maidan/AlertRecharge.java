package com.heizi.jtshop.block.maidan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.block.my.ActivityRecharge;

/**
 * 充值弹窗
 * <p>
 * Created by leo on 17/10/20.
 */

public class AlertRecharge extends AlertDialog {

    Context mContext;
    Button btn_sure, btn_cancle;

    public AlertRecharge(Context context) {
        super(context);
        mContext = context;
    }

    public AlertRecharge(Context context, boolean cancelable, OnCancelListener cancelListener, TextView tvContent) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public AlertRecharge(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_recharge);
        setCanceledOnTouchOutside(false);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ActivityRecharge.class);
                mContext.startActivity(intent);
                cancel();
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

}
