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
 * 订单页
 * Created by leo on 17/10/11.
 */

public class ActivityOrder extends BaseSwipeBackCompatActivity implements ViewPager.OnPageChangeListener, TabPageIndicator.OnTabReselectedListener {

    // 选项卡
    @InjectView(R.id.pager)
    ViewPager vp_list;
    private CommonFragmentPagerAdapter pagerAdapter;
    @InjectView(R.id.indicator)
    TabPageIndicator mIndicator;
    private List<BaseFragment> mViews = new ArrayList<>();
    private String[] titles = {"全部", "待付款", "待发货", "待收货", "待评价"};
    //当前位置
    int current = 1;

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
        tv_title.setText("我的订单");
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
            FragmentOrderList fragmentOrderList1 = new FragmentOrderList();
            Bundle bundle1 = new Bundle();
            bundle1.putInt("type", -1);
            fragmentOrderList1.setArguments(bundle1);
            mViews.add(fragmentOrderList1);

            FragmentOrderList fragmentOrderList2 = new FragmentOrderList();
            Bundle bundle2 = new Bundle();
            bundle2.putInt("type", 10);
            fragmentOrderList2.setArguments(bundle2);
            mViews.add(fragmentOrderList2);

            FragmentOrderList fragmentOrderList3 = new FragmentOrderList();
            Bundle bundle3 = new Bundle();
            bundle3.putInt("type", 20);
            fragmentOrderList3.setArguments(bundle3);
            mViews.add(fragmentOrderList3);

            FragmentOrderList fragmentOrderList4 = new FragmentOrderList();
            Bundle bundle4 = new Bundle();
            bundle4.putInt("type", 30);
            fragmentOrderList4.setArguments(bundle4);
            mViews.add(fragmentOrderList4);

            FragmentOrderList fragmentOrderList5 = new FragmentOrderList();
            Bundle bundle5 = new Bundle();
            bundle5.putInt("type", 40);
            fragmentOrderList5.setArguments(bundle5);
            mViews.add(fragmentOrderList5);
        }


        pagerAdapter.setTitles(titles);
        vp_list.setAdapter(pagerAdapter);
        mIndicator.setViewPager(vp_list);
        mIndicator.setOnPageChangeListener(this);
        mIndicator.setOnTabReselectedListener(this);
        mIndicator.setCurrentItem(current);

    }

    public void refrenshAll() {
        for (int i = 0; i < mViews.size(); i++) {
            FragmentOrderList fragmentOrderList = (FragmentOrderList) mViews.get(i);
            fragmentOrderList.onRefresh();
        }
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
