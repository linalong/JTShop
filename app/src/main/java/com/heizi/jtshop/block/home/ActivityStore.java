package com.heizi.jtshop.block.home;

import android.os.Bundle;
import android.view.View;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.home.FragmentProductList;

/**
 * 店铺
 * Created by leo on 17/9/20.
 */

public class ActivityStore extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    FragmentProductList fragmentProductList;

    private String title = "软件";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        title = getIntent().getStringExtra("title");
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_store;
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText(title);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        fragmentProductList = new FragmentProductList();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, fragmentProductList).commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
