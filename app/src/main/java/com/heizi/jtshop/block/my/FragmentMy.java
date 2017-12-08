package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.UserModel;
import com.heizi.jtshop.block.login.ActChangePwd;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.jtshop.block.login.ActPayPwd;
import com.heizi.jtshop.fragment.BaseFragment;
import com.heizi.jtshop.utils.RefreshUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.view.NoticeTextView;
import com.heizi.mycommon.view.RoundImageView;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by leo on 17/5/29.
 */

public class FragmentMy extends BaseFragment implements View.OnClickListener {
    @InjectView(R.id.ll_1)
    LinearLayout ll_1;//个人信息
    @InjectView(R.id.ll_2)
    LinearLayout ll_2;//优惠券
    @InjectView(R.id.ll_3)
    LinearLayout ll_3;//收货地址
    @InjectView(R.id.ll_4)
    LinearLayout ll_4;//绑定银行卡
    @InjectView(R.id.ll_5)
    LinearLayout ll_5;//我的商品收藏
    @InjectView(R.id.ll_6)
    LinearLayout ll_6;//我的全景VR收藏
    @InjectView(R.id.ll_7)
    LinearLayout ll_7;//密码管理
    @InjectView(R.id.ll_8)
    LinearLayout ll_8;//支付密码
    @InjectView(R.id.ll_9)
    LinearLayout ll_9;//注销登录


    @InjectView(R.id.ll_top1)
    LinearLayout ll_top1;//待付款
    @InjectView(R.id.ll_top2)
    LinearLayout ll_top2;//待发货
    @InjectView(R.id.ll_top3)
    LinearLayout ll_top3;//待收货
    @InjectView(R.id.ll_top4)
    LinearLayout ll_top4;//待评价
    @InjectView(R.id.ll_top5)
    LinearLayout ll_top5;//退换/售后

    @InjectView(R.id.ll_mid1)
    LinearLayout ll_mid1;//鲸途积分
    @InjectView(R.id.ll_mid2)
    LinearLayout ll_mid2;//交易记录
    @InjectView(R.id.ll_mid3)
    LinearLayout ll_mid3;//充值
    @InjectView(R.id.ll_mid4)
    LinearLayout ll_mid4;//提现

    @InjectView(R.id.iv_avatar)
    RoundImageView iv_avatar;
    @InjectView(R.id.nv_msg)
    NoticeTextView nv_msg;
    @InjectView(R.id.tv_name)
    TextView tv_name;
    @InjectView(R.id.tv_point)
    TextView tv_point;


    RefreshUtils refreshUtils;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my;
    }


    @Override
    protected void initView(View v) {
        super.initView(v);

        if (userModel != null)
            setData(userModel);
        refreshUtils = new RefreshUtils();
        refreshUtils.registerBoradcastReceiver(mActivity, new RefreshUtils.refreshCallback() {
            @Override
            public void call(UserModel userModel) {
                setData(userModel);
            }
        });
    }

    private void setData(UserModel userModel) {
        tv_name.setText(userModel.getMember_name());
        tv_point.setText(userModel.getAvailable_predeposit() + "");
        if (!TextUtils.isEmpty(userModel.getMember_avatar())) {
            ImageFactory.displayImage(userModel.getMember_avatar(), iv_avatar, R.mipmap.photo, R.mipmap.photo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshUtils.unregisterBoradcastReceiver(getActivity());
    }

    @OnClick({R.id.ll_1, R.id.ll_2, R.id.ll_3, R.id.ll_4, R.id.ll_5, R.id.ll_6, R.id.ll_7, R.id.ll_8, R.id.ll_9, R.id.ll_top1, R.id.ll_top2, R.id.ll_top3, R.id.ll_top4, R.id.ll_top5, R.id.ll_mid1, R.id.ll_mid2, R.id.ll_mid3, R.id.ll_mid4})
    @Override
    public void onClick(View v) {
        if (application.getUserModel() == null) {
            Intent intent = new Intent();
            intent.setClass(mActivity, ActLogin.class);
            startActivity(intent);
        } else {
            switch (v.getId()) {

                case R.id.ll_1:
                    startActivity(mActivity, ActivityUserInfo.class, null);
                    break;
                case R.id.ll_2:
                    Bundle bundlel2 = new Bundle();
                    bundlel2.putInt("state", 1);
                    startActivity(mActivity, ActivityMyCoupon.class, bundlel2);
//                new ShareAction(getActivity())
//                            .withText("hello")
//                            .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
//                            .setCallback(new UMShareListener() {
//                                @Override
//                                public void onStart(SHARE_MEDIA share_media) {
//
//                                }
//
//                                @Override
//                                public void onResult(SHARE_MEDIA share_media) {
//                                    Toast.makeText(getActivity(), "成功了", Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                                    Toast.makeText(getActivity(), "失败" + throwable.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void onCancel(SHARE_MEDIA share_media) {
//                                    Toast.makeText(getActivity(), "取消了", Toast.LENGTH_LONG).show();
//                                }
//                            })
//                            .open();

                    break;

                case R.id.ll_3:
                    startActivity(mActivity, ActivityAddressList.class, null);
                    break;

                case R.id.ll_4:

                    startActivity(mActivity, ActivityBankList.class, null);
                    break;

                case R.id.ll_5:
                    startActivity(mActivity, ActivityMyProductCollect.class, null);
                    break;

                case R.id.ll_6:
                    startActivity(mActivity, ActivityMyVRCollect.class, null);
                    break;

                case R.id.ll_7:
                    startActivity(mActivity, ActChangePwd.class, null);
                    break;
                case R.id.ll_8:
                    startActivity(mActivity, ActPayPwd.class, null);
                    break;
                case R.id.ll_9:
                    application.logout();
                    Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_SHORT).show();
                    startActivity(mActivity, ActLogin.class, null);
                    getActivity().finish();
                    break;
//
//            case R.id.ll_mypoint:
//                startActivity(mActivity, ActivityMyPoint.class, null);
//                break;
//
//            case R.id.ll_givepoint:
//                startActivity(mActivity, ActivityGivePoint.class, null);
//                break;
//            case R.id.ll_rule:
//                break;

                case R.id.ll_top1:
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    startActivity(mActivity, ActivityOrder.class, bundle);
                    break;
                case R.id.ll_top2:
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("type", 2);
                    startActivity(mActivity, ActivityOrder.class, bundle2);
                    break;
                case R.id.ll_top3:
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("type", 3);
                    startActivity(mActivity, ActivityOrder.class, bundle3);
                    break;
                case R.id.ll_top4:
                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("type", 4);
                    startActivity(mActivity, ActivityOrder.class, bundle4);
                    break;
                case R.id.ll_top5:
                    startActivity(mActivity, ActivityRefundList.class, null);
                    break;
                case R.id.ll_mid2:
                    Bundle bundle5 = new Bundle();
                    bundle5.putInt("type", 0);
                    startActivity(mActivity, ActivityPointRecord.class, bundle5);
                    break;
                case R.id.ll_mid3:
                    startActivity(mActivity, ActivityRecharge.class, null);
                    break;

                case R.id.ll_mid4:
                    startActivity(mActivity, ActivityWithdraw.class, null);
                    break;


            }
        }

    }

    /**
     * 刷新用户余额
     */
    public void getUserInfo() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + GETUSERINFO);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("fields", "available_predeposit");
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    JSONObject jsonObject = new JSONObject(data.json);
                    double available_predeposit = jsonObject.getDouble("available_predeposit");
                    if (userModel.getAvailable_predeposit() != available_predeposit) {
                        userModel.setAvailable_predeposit(available_predeposit);
                        SaveThread saveThread = new SaveThread();
                        saveThread.start();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        });
    }

    /**
     * 加载用户信息
     */
    public class SaveThread extends Thread {
        public int msg;

        @Override
        public void run() {
            application.setUserModel(userModel);
            MyApplication.getInstance().refreshUserModel();
            RefreshUtils.sendBroadcast(getActivity(), userModel);
        }
    }
}
