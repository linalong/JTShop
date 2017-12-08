package com.heizi.jtshop.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.UserModel;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.mycommon.adapter.CommonPagerAdapter;
import com.heizi.mycommon.utils.SharePreferenceUtil;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseObjectProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashActivity extends BaseSwipeBackCompatActivity {


    SharePreferenceUtil sharePreferenceUtil;
    CommonPagerAdapter pagerAdapter;
    List<View> viewList = new ArrayList<>();
    RelativeLayout rl_splash;
    ViewPager vp;

    private ParseObjectProtocol loginProtocol;
    private IResponseCallback<DataSourceModel<UserModel>> cb;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    startMainActivity();
                    break;
                case 2:
//                    startMainActivity();
                    startLoginActivity();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }


    @Override
    protected void initView() {
        super.initView();
        rl_splash = (RelativeLayout) findViewById(R.id.splash);
        vp = (ViewPager) findViewById(R.id.viewpager);
        sharePreferenceUtil = new SharePreferenceUtil(this);

    }

    private void test() {
        Map<String, String> map = new HashMap<>();
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.index.homeGoodsClass.get/");
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {

            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        });
    }

    // 初始化数据
    protected void initData() {
        loginProtocol = new ParseObjectProtocol(this, SERVER_URL_SHOP + LOGIN, UserModel.class);
        cb = new IResponseCallback<DataSourceModel<UserModel>>() {

            @Override
            public void onSuccess(DataSourceModel<UserModel> t) {
                try {
                    if (t.status == 1) {//成功
                        MyApplication.getInstance().setUserModel(t.temp);
                        SystemClock.sleep(2000);
                        handler.sendEmptyMessage(1);
                    } else {
                        MyApplication.getInstance().logout();
                        SystemClock.sleep(2000);
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    MyApplication.getInstance().logout();
                    SystemClock.sleep(2000);
                    handler.sendEmptyMessage(2);
                }

            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                // TODO Auto-generated method stub
                MyApplication.getInstance().logout();
                SystemClock.sleep(2000);
                handler.sendEmptyMessage(2);
            }
        };

        LayoutInflater inflater = LayoutInflater.from(this);
        if (!sharePreferenceUtil.getBooleanData("hasOpenApp")) {
            vp.setVisibility(View.VISIBLE);
            rl_splash.setVisibility(View.GONE);
            pagerAdapter = new CommonPagerAdapter();
            viewList.add(inflater.inflate(R.layout.view_guide_1, null));
            viewList.add(inflater.inflate(R.layout.view_guide_2, null));
            viewList.add(inflater.inflate(R.layout.view_guide_3, null));
            pagerAdapter.setViews(viewList);
            vp.setAdapter(pagerAdapter);
            viewList.get(viewList.size() - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePreferenceUtil.setBooleanData("hasOpenApp", true);
                    startLoginActivity();
//                    prcessLogic();
                }
            });
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    prcessLogic();
                }
            }.start();
        }

    }


    private void getData() {
        Map<String, String> maps = new HashMap<>();
        maps.put("mobile", userModel.getMember_mobile());
        maps.put("password", userModel.getMember_passwd());
        loginProtocol.postData(maps, cb);
    }

    private void prcessLogic() {
//        showLoadingDialog();
        if (MyApplication.getInstance().refreshUserModel() != null) {
            userModel = MyApplication.getInstance().getUserModel();
            if (userModel != null) {
                getData();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SystemClock.sleep(3000);
                        handler.sendEmptyMessage(2);
                    }
                }.start();
            }
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    SystemClock.sleep(3000);
                    handler.sendEmptyMessage(2);
                }
            }.start();
        }

    }


    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainTabsActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, ActLogin.class);
        startActivity(intent);
        finish();
    }


}
