package com.heizi.jtshop.block.my;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.fragment.BaseFragment;
import com.heizi.mycommon.adapter.CommonFragmentPagerAdapter;
import com.hz.viewpagerindicator.indicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 积分记录
 * Created by leo on 17/10/11.
 */

public class ActivityPointRecord extends BaseSwipeBackCompatActivity implements ViewPager.OnPageChangeListener, TabPageIndicator.OnTabReselectedListener {

    // 选项卡
    @InjectView(R.id.pager)
    ViewPager vp_list;
    private CommonFragmentPagerAdapter pagerAdapter;
    @InjectView(R.id.indicator)
    TabPageIndicator mIndicator;
    private List<BaseFragment> mViews = new ArrayList<>();
    private String[] titles = {"充值记录", "提现记录"};
    //当前位置
    int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        current = getIntent().getExtras().getInt("type");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("积分记录");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 选项卡
        pagerAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), mViews);
        mIndicator.setOnPageChangeListener(this);
        mIndicator.setOnTabReselectedListener(this);
        setTabViewPager();
    }


    private void setTabViewPager() {
        if (mViews.size() == 0 && titles != null) {
            FragmentWithdrawList fragmentWithdrawList = new FragmentWithdrawList();
            mViews.add(fragmentWithdrawList);

            FragmentWithdrawList fragmentWithdrawList2 = new FragmentWithdrawList();
            mViews.add(fragmentWithdrawList2);

        }

        pagerAdapter.setTitles(titles);
        vp_list.setAdapter(pagerAdapter);
        mIndicator.setViewPager(vp_list);
        mIndicator.setOnPageChangeListener(this);
        mIndicator.setOnTabReselectedListener(this);
        mIndicator.setCurrentItem(current);

    }


    @Override
    public void onTabReselected(int position) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub

    }
}
