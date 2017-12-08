package com.heizi.jtshop.block.login;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heizi.jtshop.Constants;
import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.UserModel;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.utils.RefreshUtils;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.SharePreferenceUtil;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mycommon.utils.UtilsLog;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseObjectProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.xutils.common.util.MD5;

import java.util.HashMap;
import java.util.Map;


/**
 * 支付密码
 *
 * @author Administrator
 */
public class ActChangePwd extends BaseSwipeBackCompatActivity implements
        OnClickListener, Constants {
    private Button btn_ok, btn_get_code;
    private EditText et_paypwd, et_verification_code, et_repaypwd;
    private TextView tv_title;
    private long leftTime = 0;
    private long closeTime = 0L;
    private SharePreferenceUtil su;
    private MyCount mc;
    private ParseStringProtocol gcp;// 获取验证码接口
    private IResponseCallback<DataSourceModel<String>> gcpcb;
    //注册接口
    private ParseObjectProtocol rp;
    private IResponseCallback<DataSourceModel<UserModel>> rpcb;
    private Handler mHandler;

    @Override
    protected int getLayoutResource() {
        return R.layout.act_change_pwd;
    }


    @Override
    protected void initView() {
        super.initView();
        su = new SharePreferenceUtil(this);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_get_code.setOnClickListener(this);
        et_paypwd = (EditText) findViewById(R.id.et_paypwd);
        et_verification_code = (EditText)
                findViewById(R.id.et_verification_code);
        et_repaypwd = (EditText) findViewById(R.id.et_repaypwd);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("设置登录密码");

        mHandler = new Handler(this.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {

                ActChangePwd.this.finish();
            }
        };
    }

    // 初始化数据

    @Override
    protected void initData() {
        super.initData();
        rp = new ParseObjectProtocol(this, SERVER_URL_SHOP + "&method=jingtu.user_center.editPassword.post/", UserModel.class);
        rpcb = new IResponseCallback<DataSourceModel<UserModel>>() {

            @Override
            public void onSuccess(DataSourceModel<UserModel> t) {
                // TODO Auto-generated method stub
                LoadingD.hideDialog();
                Utils.toastShow(ActChangePwd.this, "登录密码修改成功!");
//                try {
//                    userModel = t.temp;
//                    if (!TextUtils.isEmpty(userModel.getMember_id())) {
                userModel.setMember_passwd(MD5.md5(et_paypwd.getText().toString()));
                SaveThread saveThread = new SaveThread();
                saveThread.start();
//                    }
//                    Utils.toastShow(ActRetrievePwd.this, "密码成功找回!");
//
//                } catch (Exception e) {
//                    // TODO: handle exception
//                }
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                LoadingD.showDialog(ActChangePwd.this);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                // TODO Auto-generated method stub
                LoadingD.hideDialog();
                Utils.toastShow(ActChangePwd.this, errorModel.getMsg());
            }
        };


        gcp = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.user_center.sendCodeMobile.post/");
        gcpcb = new IResponseCallback<DataSourceModel<String>>() {

            @Override
            public void onSuccess(DataSourceModel<String> t) {
                // TODO Auto-generated method stub
                LoadingD.hideDialog();
                Utils.toastShow(ActChangePwd.this, "发送成功");
                if (mc != null)
                    mc.cancel();
                mc = new MyCount(60 * 1000, 1000); // 第一参数是总的时间，第二个是间隔时间 都是毫秒为单位
                mc.start();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                // TODO Auto-generated method stub
                LoadingD.hideDialog();
                UtilsLog.d("====", "code==" + errorModel.getCode());
                Utils.toastShow(ActChangePwd.this, "" + errorModel.getMsg());
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                LoadingD.showDialog(ActChangePwd.this);
            }
        };
    }

    // 注册

    private void initRegister() {
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("token", userModel.getToken());
        maps.put("password", MD5.md5(et_paypwd.getText().toString()));
        maps.put("password_confirm", MD5.md5(et_paypwd.getText().toString()));
        maps.put("vcode", et_verification_code.getText().toString());
        rp.postData(maps, rpcb);
    }

    // 获取验证码
    private void getCode() {
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("token", userModel.getToken());
        maps.put("mes_tpl", "edit_pwd");
        gcp.postData(maps, gcpcb);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_ok:
                if (checkData()) {
                    initRegister();
                }

                break;
            case R.id.btn_get_code:
                if (checkData()) {
                    getCode();
                }
                break;

            case R.id.btn_back:
                finish();
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //恢复验证码相关状态
        leftTime = su.getRegisterLeftTiming();
        closeTime = su.getRegisterTime();
        if (leftTime < 3)
            return;

        long curT = System.currentTimeMillis();
        if (closeTime != 0 && curT - closeTime >= leftTime * 1000) {

        } else {
            mc = new MyCount(leftTime * 1000 - (curT - closeTime), 1000);
            mc.start();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
//		if (leftTime < 3) {
        su.setRegisterLeftTiming(0);
        su.setRegisterTime(0);
//		} else {
//			su.setRegisterLeftTiming(leftTime);
//			closeTime = System.currentTimeMillis();
//			su.setRegisterTime(closeTime);
        if (mc != null)
            mc.cancel();
//		}
    }

    // 倒计时
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_get_code.setEnabled(true);
            btn_get_code.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_get_code.setEnabled(false);
            btn_get_code.setText(String.format(
                    getString(R.string.register_countdown),
                    millisUntilFinished / 1000));
            leftTime = millisUntilFinished / 1000;
        }
    }

    // 验证输入信息是否正确
    private boolean checkData() {
        if (Utils.isNull(et_paypwd.getText().toString())) {
            Utils.toastShow(this, "请输入登录密码");
            return false;
        } else if (et_paypwd.getText().toString().length() < 6 || et_paypwd.getText().toString().length() > 20) {
            Utils.toastShow(this, "登录密码长度为6~20位");
            return false;
        } else if (Utils.isNull(et_repaypwd.getText().toString())) {
            Utils.toastShow(this, "请再次输入登录密码");
            return false;
        } else if (!et_repaypwd.getText().toString().equals(et_paypwd.getText().toString())) {
            Utils.toastShow(this, "两次密码不一致");
            return false;
        }
        return true;
    }

    /**
     * 加载用户信息
     */
    public class SaveThread extends Thread {
        public int msg = 0;

        @Override
        public void run() {
            application.setUserModel(userModel);
            MyApplication.getInstance().refreshUserModel();
            RefreshUtils.sendBroadcast(ActChangePwd.this, userModel);
            mHandler.sendEmptyMessage(msg);
        }
    }


}
