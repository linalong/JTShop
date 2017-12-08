package com.heizi.jtshop.block.maidan;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.my.ModelOrderList;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

import static com.heizi.jtshop.block.my.FragmentOrderList.requestRefresh;


/**
 * 评论页
 * Created by leo on 17/9/18.
 */

public class ActivityCommit extends BaseSwipeBackCompatActivity {
    @InjectView(R.id.rb_star)
    RatingBar rb_star;
    @InjectView(R.id.rb_star2)
    RatingBar rb_star2;
    @InjectView(R.id.rb_star3)
    RatingBar rb_star3;

    ModelOrderList modelOrderList;
    LinearLayout container;
    boolean isBusy = false;
    int successNum;//成功拼接的数量
    JSONArray jsonArray;

    ArrayList<FragmentCommit> fragmentCommits = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_commit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            modelOrderList = (ModelOrderList) getIntent().getExtras().getSerializable("modelOrderList");
//            modelOrderList.getOrder_goods().add(modelOrderList.getOrder_goods().get(0));
//            modelOrderList.getOrder_goods().add(modelOrderList.getOrder_goods().get(0));

        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("发表评论");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("发布");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) rb_star.getRating() == 0) {
                    Utils.toastShow(ActivityCommit.this, "请给产品描述相符打分");
                    return;
                } else if ((int) rb_star2.getRating() == 0) {
                    Utils.toastShow(ActivityCommit.this, "请给物流服务打分");
                    return;
                } else if ((int) rb_star3.getRating() == 0) {
                    Utils.toastShow(ActivityCommit.this, "请给服务态度打分");
                    return;
                }

                if (isBusy) {
                    Utils.toastShow(ActivityCommit.this, getResources().getString(R.string.request_string));
                } else {
                    isBusy = true;
                    LoadingD.showDialog(ActivityCommit.this);
                    successNum = 0;
                    for (FragmentCommit fragment : fragmentCommits) {
                        fragment.post();
                    }
                }
            }
        });
        container = (LinearLayout) findViewById(R.id.container);
        for (int i = 0; i < modelOrderList.getOrder_goods().size(); i++) {

            FragmentCommit fragmentCommit = new FragmentCommit();
            Bundle bundle = new Bundle();
            bundle.putSerializable("modelProductList", modelOrderList.getOrder_goods().get(0));
            bundle.putInt("position", i);
            fragmentCommit.setArguments(bundle);
            fragmentCommit.setCallback(new FragmentCommit.CommitCallback() {
                @Override
                public void onsuccess(JSONObject json) {
                    successNum++;
                    jsonArray.put(json);
                    if (successNum == modelOrderList.getOrder_goods().size()) {
                        LoadingD.hideDialog();
                        upload(jsonArray.toString());
                    }
                }

                @Override
                public void onfailed() {
                    isBusy = false;
                    LoadingD.hideDialog();
                    successNum = 0;
                    Utils.toastShow(ActivityCommit.this, "评论失败,请稍后重试");
                }
            });

            fragmentCommits.add(fragmentCommit);

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentCommit).commit();
        }
        jsonArray = new JSONArray();
    }


    private void upload(String json) {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + ADDEVALUATE);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("order_id", modelOrderList.getOrder_id());
        map.put("goods_score", json);
        map.put("store_desccredit", rb_star.getRating() + "");
        map.put("store_servicecredit", rb_star2.getRating() + "");
        map.put("store_deliverycredit", rb_star3.getRating() + "");

        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                Utils.toastShow(ActivityCommit.this, "感谢您的评价");
                setResult(requestRefresh);
                LoadingD.hideDialog();
                isBusy = false;
                finish();

            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                Utils.toastShow(ActivityCommit.this, "评价失败");
                LoadingD.hideDialog();
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityCommit.this);
            }
        });
    }
}
