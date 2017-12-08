package com.heizi.jtshop.block.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.maidan.ActivityProductDetail;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.jtshop.fragment.BaseListFragment;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo on 17/10/3.
 */

public class FragmentProductList extends BaseListFragment {


    private LinearLayout ll_notice;
    private List<ModelProductList> listData = new ArrayList<>();
    private CommonAdapter adapter;

    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;
    private String title = "";

    private String keyword = "";//搜索关键字
    private String gc_id = "";//分类id


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_no_title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString("title") != null) {
            title = bundle.getString("title");
        }
        if (bundle != null && bundle.getString("keyword") != null) {
            keyword = bundle.getString("keyword");
        }
        if (bundle != null && bundle.getString("gc_id") != null) {
            gc_id = bundle.getString("gc_id");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(final View v) {
        super.initView(v);
        ll_notice = (LinearLayout) v.findViewById(R.id.ll_notice);
        //listview
        adapter = new CommonAdapter(getActivity(), listData, R.layout.item_home_store) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                LinearLayout ll_baoyou = holder.findViewById(R.id.ll_baoyou);
                ImageView iv_store = holder.findViewById(R.id.iv_store);
                ImageFactory.displayImage(listData.get(position).getGoods_image_url(), iv_store, 0, 0);
                TextView tv_name = holder.findViewById(R.id.tv_name);
                TextView tv_store = holder.findViewById(R.id.tv_store);
                TextView tv_guanggao = holder.findViewById(R.id.tv_guanggao);
                TextView tv_baoyou = holder.findViewById(R.id.tv_baoyou);
                TextView tv_price = holder.findViewById(R.id.tv_price);
                TextView tv_num = holder.findViewById(R.id.tv_num);
                tv_name.setText(listData.get(position).getGoods_name());
                tv_price.setText(listData.get(position).getGoods_price());
                tv_num.setText("销量:" + listData.get(position).getGoods_salenum());
                tv_store.setText(listData.get(position).getStore_name());
                if (listData.get(position).getGoods_freight() == 0) {
                    tv_baoyou.setText("包邮");
                    tv_baoyou.setVisibility(View.VISIBLE);
                } else {
                    tv_baoyou.setVisibility(View.INVISIBLE);
                }

            }
        };
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("goods_id", listData.get(position - 1).getGoods_id());
                startActivity(getActivity(), ActivityProductDetail.class, bundle);
            }
        });
        mListView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        super.initData();
        parsePanoList = new ParseStringProtocol(mActivity, SERVER_URL_SHOP + "&method=jingtu.goods.goodsList.get/");
        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    JSONObject jsonObject = new JSONObject(data.json);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("page_data"));
                    hasMore = jsonObject1.getBoolean("hasmore");
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("items"));

                    //先判断是否是刷新,刷新成功则清除数据
                    if (isRefresh) {
                        pageIndex = 1;
                        listData.removeAll(listData);
                    }
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            ModelProductList modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelProductList.class);
                            listData.add(modelPano);
                        }
                        adapter.notifyDataSetChanged();
                        //无更多数据
                        if (!hasMore) {
                            mListView.setPullLoadEnable(false);
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listData.size() == 0) {
                    ll_notice.setVisibility(View.VISIBLE);
                    mListView.setPullRefreshEnable(false);
                } else {
                    ll_notice.setVisibility(View.GONE);
                    mListView.setPullRefreshEnable(true);
                }
                isRefresh = false;
                onLoad();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                isRefresh = false;
                onLoad();
            }

            @Override
            public void onStart() {
                isBusy = true;
            }
        };

        if (listData.size() == 0)
            getData();
    }

    @Override
    protected void getData() {
        super.getData();
        Map<String, String> maps = new HashMap<>();
        maps.put("gc_id", gc_id);
        maps.put("keyword", keyword);
        maps.put("key", "");
        maps.put("pagesize", pageSize + "");
        maps.put("curpage", pageIndex + "");
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);
    }

//    private void geneItems() {
//        for (int i = 0; i != 10; ++i) {
//            listData.add("Test XScrollView item " + (pageIndex));
//        }
//        Log.d("==", "Test XListView item " + (pageIndex));
//    }

}
