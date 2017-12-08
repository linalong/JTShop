package com.heizi.jtshop.block.maidan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heizi.jtshop.R;
import com.heizi.mycommon.utils.Utils;

import org.xutils.common.util.MD5;

/**
 * 支付密码弹窗
 * <p>
 * Created by leo on 17/10/20.
 */

public class AlertPayPwd extends AlertDialog {

    Activity mContext;
    Button btn_sure, btn_cancle;
    EditText et_paypwd;
    String paypwd = "";
    pwdCallback callback;


    public interface pwdCallback {
        void call();
        void close();
    }

    public AlertPayPwd(Activity context, String pwd, pwdCallback pwdcallback) {
        super(context);
        mContext = context;
        this.paypwd = pwd;
        callback = pwdcallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_paypwd);
        setCanceledOnTouchOutside(false);
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //加上下面这一行弹出对话框时软键盘随之弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        et_paypwd = (EditText) findViewById(R.id.et_pwd);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MD5.md5(et_paypwd.getText().toString().trim()).equals(paypwd) && callback != null) {
                    mContext.runOnUiThread(new Runnable() {
                        public void run() {
                            closehideSoftInput(et_paypwd);
                        }
                    });
                    callback.call();
                    cancel();
                } else {
                    Toast.makeText(mContext, "支付密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        closehideSoftInput(et_paypwd);
                    }
                });
                Utils.hideInputMethod(mContext);
                callback.close();
                cancel();
            }
        });
    }

    private void closehideSoftInput(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
