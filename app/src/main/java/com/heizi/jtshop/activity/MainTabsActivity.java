package com.heizi.jtshop.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.heizi.jtshop.Constants;
import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.home.FragmentHome;
import com.heizi.jtshop.block.home.FragmentHongbao;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.jtshop.block.maidan.FragmenCartList;
import com.heizi.jtshop.block.my.FragmentMy;
import com.heizi.jtshop.block.pano.FragmentHomeMap;
import com.heizi.mycommon.adapter.CommonFragmentPagerAdapter;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mycommon.view.NoScrollViewPager;
import com.heizi.mycommon.view.NoticeTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


public class MainTabsActivity extends BaseActivity implements OnClickListener,
        Constants {

    private static final String TAG = MainTabsActivity.class.getSimpleName();
    private static MainTabsActivity instance;
    public FragmentManager manager;
    private long time;
    @InjectViews({R.id.tabs_guide1, R.id.tabs_guide2, R.id.tabs_guide3, R.id.tabs_guide4, R.id.tabs_guide5})
    public List<NoticeTextView> rbtns;

    @InjectView(R.id.vp_main)
    NoScrollViewPager vp_main;

    List<Fragment> fragments;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();
        instance = this;
        if (userModel != null && !TextUtils.isEmpty(userModel.getRongyun_token())) {
            connect(userModel.getRongyun_token());
        }
    }

    public static MainTabsActivity getInstance() {
        return instance;
    }

    @Override
    protected void initView() {
        super.initView();

        setTitleVisible(false);


        fragments = new ArrayList<Fragment>() {{
            add(new FragmentHomeMap());
            add(new FragmentHome());
            add(new FragmentHongbao());
            add(new FragmenCartList());
            add(new FragmentMy());
        }};

        if (null != fragments && !fragments.isEmpty()) {
            vp_main.setNoScroll(true);
            vp_main.setOffscreenPageLimit(fragments.size());
            vp_main.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        }

        rbtns.get(0).performClick();
        registerBoradcastReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        userModel = application.refreshUserModel();
//        if (userModel != null)
//            connect(userModel.getRongyun_token());
    }

    public void changeToMain() {
        changeTabState(rbtns.get(0));
    }

    public void changeToMy() {
        changeTabState(rbtns.get(2));
    }

    public void exit() {
        finish();
    }

    private void changeTabState(View v) {
        int b = R.color.red;
        int g = R.color.black1;


        rbtns.get(0).setTextColor(v.getId() == R.id.tabs_guide1 ? getResources()
                .getColor(b) : getResources().getColor(g));
        rbtns.get(1).setTextColor(v.getId() == R.id.tabs_guide2 ? getResources()
                .getColor(b) : getResources().getColor(g));
        rbtns.get(3).setTextColor(v.getId() == R.id.tabs_guide4 ? getResources()
                .getColor(b) : getResources().getColor(g));
        rbtns.get(4).setTextColor(v.getId() == R.id.tabs_guide5 ? getResources()
                .getColor(b) : getResources().getColor(g));

        float alpha = 1;
        float alpha6 = 0.8f;
        // 设置透明度
        rbtns.get(0).setAlpha(v.getId() == R.id.tabs_guide1 ? alpha : alpha6);
        rbtns.get(1).setAlpha(v.getId() == R.id.tabs_guide2 ? alpha : alpha6);
        rbtns.get(3).setAlpha(v.getId() == R.id.tabs_guide3 ? alpha : alpha6);
        rbtns.get(4).setAlpha(v.getId() == R.id.tabs_guide4 ? alpha : alpha6);

        // 设置文字显示
        rbtns.get(0).setText(v.getId() == R.id.tabs_guide1 ? "VR界" : "VR界");
        rbtns.get(1).setText(v.getId() == R.id.tabs_guide2 ? "商城" : "商城");
        rbtns.get(2).setText(v.getId() == R.id.tabs_guide3 ? "" : "");
        rbtns.get(3).setText(v.getId() == R.id.tabs_guide4 ? "购物车" : "购物车");
        rbtns.get(4).setText(v.getId() == R.id.tabs_guide5 ? "我的" : "我的");


        //设置顶部距离
//        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams1.setMargins(0, 13, 0, 0);
//        layoutParams1.weight = 1;
//
//        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams2.setMargins(0, 0, 0, 0);
//        layoutParams2.weight = 1;
//        rbtns.get(0).setLayoutParams(v.getId() == R.id.tabs_guide1 ? layoutParams1 : layoutParams2);
//        rbtns.get(1).setLayoutParams(v.getId() == R.id.tabs_guide2 ? layoutParams1 : layoutParams2);
//        rbtns.get(2).setLayoutParams(v.getId() == R.id.tabs_guide3 ? layoutParams1 : layoutParams2);


        rbtns.get(0).setCompoundDrawablesWithIntrinsicBounds(
                null,
                getResources().getDrawable(
                        v.getId() == R.id.tabs_guide1 ? R.mipmap.home_1_03
                                : R.mipmap.home_0_03), null, null);
        rbtns.get(1).setCompoundDrawablesWithIntrinsicBounds(
                null,
                getResources().getDrawable(
                        v.getId() == R.id.tabs_guide2 ? R.mipmap.home_1_05
                                : R.mipmap.home_0_05), null, null);
        rbtns.get(3).setCompoundDrawablesWithIntrinsicBounds(
                null,
                getResources().getDrawable(
                        v.getId() == R.id.tabs_guide4 ? R.mipmap.home_1_07
                                : R.mipmap.home_0_07), null, null);
        rbtns.get(4).setCompoundDrawablesWithIntrinsicBounds(
                null,
                getResources().getDrawable(
                        v.getId() == R.id.tabs_guide5 ? R.mipmap.home_1_09
                                : R.mipmap.home_0_09), null, null);

        if (v.getId() == R.id.tabs_guide1) {
            vp_main.setCurrentItem(0);
        } else if (v.getId() == R.id.tabs_guide2) {
            vp_main.setCurrentItem(1);
        } else if (v.getId() == R.id.tabs_guide3) {
            vp_main.setCurrentItem(2);
            FragmentHongbao fragmentHongbao = (FragmentHongbao) fragments.get(2);
            fragmentHongbao.reload();
        } else if (v.getId() == R.id.tabs_guide4) {
            vp_main.setCurrentItem(3);
        } else if (v.getId() == R.id.tabs_guide5) {
            vp_main.setCurrentItem(4);
            FragmentMy fragmentMy = (FragmentMy) fragments.get(4);
            fragmentMy.getUserInfo();
        }

    }


    @OnClick({R.id.tabs_guide1, R.id.tabs_guide2, R.id.tabs_guide3, R.id.tabs_guide4, R.id.tabs_guide5})
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tabs_guide1:// 首页
                changeTabState(v);
                break;
            case R.id.tabs_guide2:// 买单
                changeTabState(v);

                break;
            case R.id.tabs_guide3:// 我的
                if (application.getUserModel() == null && application.refreshUserModel() == null) {
                    Intent intent = new Intent();
                    intent.setClass(MainTabsActivity.this, ActLogin.class);
                    startActivity(intent);
                } else {
                    changeTabState(v);
                }
                break;
            case R.id.tabs_guide4:// 首页
                changeTabState(v);
                break;
            case R.id.tabs_guide5:// 首页
                if (application.getUserModel() == null && application.refreshUserModel() == null) {
                    Intent intent = new Intent();
                    intent.setClass(MainTabsActivity.this, ActLogin.class);
                    startActivity(intent);
                } else {
                    changeTabState(v);
                }
                break;
            default:
                break;
        }
    }

    // 得到广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MyApplication.action_token_error)) {
                Utils.toastShow(MainTabsActivity.this, "登录过期，请重新登录!");
                MyApplication.getInstance().logout();
                //打开main 将其余activity弹出,然后打开login,关闭main
                startActivity(MainTabsActivity.this, MainTabsActivity.class, null);
                Intent intent1 = new Intent();
                intent1.setClass(MainTabsActivity.this, ActLogin.class);
                startActivity(intent1);
                MainTabsActivity.getInstance().exit();
            }
            // 刷新底部五个按钮的显示数量
            else if (action.equals(MyApplication.action_refresh_login)) {
            }
        }
    };

    // 注册广播 接收未登录状态 底部按钮刷新
    private void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(MyApplication.action_token_error);
        myIntentFilter.addAction(MyApplication.action_refresh_login);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onBackPressed() {
        // 是WebViewFragment有回退功能
        if (System.currentTimeMillis() - time > 2500) {
            Toast.makeText(this, getString(R.string.string_exit),
                    Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        RongIM.getInstance().disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connect(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("====", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.d("====", "--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("====", "--onError" + errorCode);
                }
            });
        }
    }


}
