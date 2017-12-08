package com.heizi.jtshop.block.home;

import android.view.View;

import com.heizi.jtshop.R;
import com.heizi.jtshop.fragment.BaseFragment;

/**
 * Created by leo on 17/10/3.
 */

public class FragmentFind extends BaseFragment {

    FragmentProductList fragmentProductList;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scan_code;
    }


    @Override
    protected void initView(View v) {
        super.initView(v);
        tv_title.setText("发现");
        btn_back.setVisibility(View.INVISIBLE);
        fragmentProductList = new FragmentProductList();
        getChildFragmentManager().beginTransaction().replace(R.id.fl_my_container, fragmentProductList).commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
