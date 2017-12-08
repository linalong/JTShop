package com.heizi.jtshop.block.home;

import android.os.Bundle;
import android.view.View;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;

/**
 * 分类和搜索进入的产品列表
 * Created by leo on 17/9/20.
 */

public class ActivityProductList extends BaseSwipeBackCompatActivity {

    FragmentProductList fragmentProductList;

    private String title = "搜索结果";

    private String keyword = "";//搜索关键字
    private String gc_id = "";//分类id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.getString("title") != null) {
                title = bundle.getString("title");
            }
            if (bundle != null && bundle.getString("keyword") != null) {
                keyword = bundle.getString("keyword");
            }
            if (bundle != null && bundle.getString("gc_id") != null) {
                gc_id = bundle.getString("gc_id");
            }
        }
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scan_code;
    }


    @Override
    protected void initView() {
        super.initView();
        tv_title.setText(title);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fragmentProductList = new FragmentProductList();
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        bundle.putString("gc_id", gc_id);
        fragmentProductList.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, fragmentProductList).commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }

}
