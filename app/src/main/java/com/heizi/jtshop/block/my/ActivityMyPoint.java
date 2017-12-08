package com.heizi.jtshop.block.my;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseListActivity;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 积分记录
 * Created by leo on 17/9/26.
 */

public class ActivityMyPoint extends BaseListActivity {


    CommonAdapter adapter;
    List<String> listData = new ArrayList<>();
    LinearLayout ll_notice;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list;
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
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        //listview
        adapter = new CommonAdapter(this, listData, R.layout.item_point) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                TextView tv_status = holder.findViewById(R.id.tv_status);
                TextView tv_time = holder.findViewById(R.id.tv_time);
                TextView tv_type = holder.findViewById(R.id.tv_type);
                TextView tv_point = holder.findViewById(R.id.tv_point);
                if (position % 2 == 0) {
                    tv_point.setTextColor(getResources().getColor(R.color.red));
                    tv_point.setText("+100");
                    tv_type.setText("充值积分");
                    tv_status.setVisibility(View.INVISIBLE);
                } else {
                    tv_point.setTextColor(getResources().getColor(R.color.green1));
                    tv_point.setText("-100");
                    tv_type.setText("提现积分");
                    tv_status.setText("审核通过");
                }

            }
        };
        geneItems();
        mListView.setAdapter(adapter);
    }

    private void geneItems() {
        for (int i = 0; i != 10; ++i) {
            listData.add("Test XScrollView item " + (pageIndex));
        }
        Log.d("==", "Test XListView item " + (pageIndex));
    }
}
