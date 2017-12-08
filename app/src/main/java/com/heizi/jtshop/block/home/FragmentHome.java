package com.heizi.jtshop.block.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heizi.jtshop.Constants;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.jtshop.block.login.ActivityScanCodeRegist;
import com.heizi.jtshop.block.maidan.ActivityProductDetail;
import com.heizi.jtshop.block.maidan.ActivityScanCode;
import com.heizi.jtshop.block.maidan.ModelProductList;
import com.heizi.jtshop.block.pano.ActivityCity;
import com.heizi.jtshop.fragment.BaseScrollListFragment;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.DipPixUtils;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.TimerAlarm;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseListProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;
import com.markmao.pulltorefresh.widget.OnScrollChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 首页
 * 1.首页列表默认关联城市id和分类id  百度定位变化后获取城市id,id获取成功重新刷新列表,从activityCity更改城市返回后重新刷新列表
 * Created by leo on 17/5/16.
 */

public class FragmentHome extends BaseScrollListFragment implements
        View.OnClickListener, AdapterView.OnItemClickListener, Constants, OnScrollChangedListener {
    LinearLayout ll_title;
    TextView tv_local, tv_saosao;
    TextView tv_search;

    private LinearLayout ll_notice, ll_bottom, ll_top;
    //listview
    private List<ModelProductList> listData = new ArrayList<>();
    private CommonAdapter adapter;


    //viewpager_top
    private ViewPager vp_top;
    private LinearLayout pointlayout;
    private List<ModelHome> dataPager = new ArrayList<>();
    private List<ImageView> listImg = new ArrayList<ImageView>();
    private PagerAdapterHome pagerAdapterHome;

    //viewpager_mid
    private ViewPager vp_mid;
    private LinearLayout pointlayout_mid;
    private List<ModelCategory> dataPagerMid = new ArrayList<>();
    private List<ImageView> listImgMid = new ArrayList<ImageView>();
    private PagerAdapterCategory pagerAdapterCategory;

    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;


    ModelAddress modelAddress;
    //轮播图定时器
    TimerAlarm timerAlarm;


    public static int codeGetCity = 0x009888;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }


    @Override
    public void onScrollChanged(int top, int oldTop) {
        int mBuyLayout2ParentTop = Math.max(top, ll_top.getTop() + Utils.typedValueDP(mActivity, 150));
        ll_bottom.layout(0, mBuyLayout2ParentTop, ll_bottom.getWidth(), mBuyLayout2ParentTop + ll_bottom.getHeight());

        if (top > Utils.typedValueDP(mActivity, 150)) {
            ll_bottom.setVisibility(View.VISIBLE);
        } else {
            ll_bottom.setVisibility(View.GONE);
        }


    }

    @Override
    protected void initView(View v) {

        super.initView(v);

        ll_bottom = (LinearLayout) v.findViewById(R.id.ll_bottom);
        ll_bottom.setOnClickListener(this);
        ll_top = (LinearLayout) v.findViewById(R.id.ll_top);

        tv_local = (TextView) v.findViewById(R.id.tv_local);
        tv_local.setOnClickListener(this);
        tv_saosao = (TextView) v.findViewById(R.id.tv_saosao);
        tv_saosao.setOnClickListener(this);
        tv_search = (TextView) v.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(this);
        ll_title = (LinearLayout) v.findViewById(R.id.ll_title);
        mScrollView.setOnScrollChangedListener(this);
        modelAddress = new ModelAddress();
        modelAddress.setCity_id("0");
        modelAddress.setCity("全球");
        tv_local.setText(modelAddress.getCity());

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
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        measureListHeight();


        //轮播图
        vp_top = (ViewPager) v.findViewById(R.id.vp_top);
        pointlayout = (LinearLayout) v.findViewById(R.id.pointlayout);
        pagerAdapterHome = new PagerAdapterHome(mActivity);
        vp_top.setAdapter(pagerAdapterHome);

        //分类
        vp_mid = (ViewPager) v.findViewById(R.id.vp_mid);
        pointlayout_mid = (LinearLayout) v.findViewById(R.id.pointlayout_mid);
        pagerAdapterCategory = new PagerAdapterCategory(mActivity);
        vp_mid.setAdapter(pagerAdapterCategory);

        timerAlarm = new TimerAlarm(new TimerAlarm.CallLisener() {
            @Override
            public void call(int code) {
                if (dataPager.size() > 0) {
                    if (vp_top.getCurrentItem() == dataPager.size() - 1) {
                        vp_top.setCurrentItem(0);
                    } else {
                        vp_top.setCurrentItem(vp_top.getCurrentItem() + 1);
                    }

                    setdefault(listImg);
                    if (vp_top.getCurrentItem() < listImg.size())
                        listImg.get(vp_top.getCurrentItem()).setBackgroundResource(
                                R.drawable.point_home_normal_select);
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerAlarm != null)
            timerAlarm.stop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (timerAlarm != null)
            timerAlarm.start();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("goods_id", listData.get(position).getGoods_id());
        startActivity(getActivity(), ActivityProductDetail.class, bundle);
    }


    @Override
    protected void initData() {
        super.initData();

        getDataPager();

        getDataCateroty();

        parsePanoList = new ParseStringProtocol(mActivity, SERVER_URL_SHOP + "&method=jingtu.index.homeGoods.get/");
        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
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

                        //无更多数据
                        if (!hasMore) {
                            mScrollView.setPullLoadEnable(false);
                        }
                    } else {
                        mScrollView.setPullLoadEnable(false);
                    }
                    adapter.notifyDataSetChanged();
                    measureListHeight();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listData.size() == 0) {
                    ll_notice.setVisibility(View.VISIBLE);
                } else {
                    ll_notice.setVisibility(View.GONE);
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

    /**
     * TODO 城市名称目前用老接口传的id
     */
    protected void getData() {
        Map<String, String> maps = new HashMap<>();
        if (modelAddress != null)
            maps.put("city_id", "36");
        maps.put("pagesize", pageSize + "");
        maps.put("page", pageIndex + "");
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_local:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), ActivityCity.class);
                if (modelAddress != null) {
                    intent2.putExtra("modelAddress", modelAddress);
                }
                startActivityForResult(intent2, codeGetCity);
                break;
            case R.id.tv_saosao:
                if (application.getUserModel() == null) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ActLogin.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ActivityScanCode.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_search:
//                startActivity(mActivity, ActivityCommit.class, null);
                startActivity(mActivity, ActivitySearch.class, null);
                break;
            case R.id.ll_bottom:
//                startActivity(mActivity, ActivityCommit.class, null);
                startActivity(mActivity, ActivitySearch.class, null);
                break;
        }
    }

    //获取轮播图数据
    private void getDataPager() {
        ParseListProtocol parseListProtocol = new ParseListProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.index.homeSlider.get/", ModelHome.class);
        parseListProtocol.getData(new HashMap(), new IResponseCallback<DataSourceModel<ModelHome>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelHome> data) {
                if (data.list.size() > 0) {
                    dataPager.addAll(data.list);
                    showDataPager(dataPager);
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        });

    }

    //获取分类
    private void getDataCateroty() {
        ParseListProtocol parseListProtocol = new ParseListProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.index.homeGoodsClass.get/", ModelCategory.class);
        parseListProtocol.getData(new HashMap(), new IResponseCallback<DataSourceModel<ModelCategory>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelCategory> data) {
                if (data.list.size() > 0) {
                    dataPagerMid.addAll(data.list);
                    showDataMid(dataPagerMid);
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        });

    }

    //viewpager填充
    private void showDataPager(List<ModelHome> list) {

        if (list.size() != listImg.size()) {
            pointlayout.removeAllViews();
            listImg.removeAll(listImg);
            for (int i = 0; i < list.size(); i++) {
                ImageView img = new ImageView(mActivity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        15, 15);
                img.setLayoutParams(lp);

                if (i == 0)
                    img.setBackgroundResource(R.drawable.point_home_normal_select);
                else
                    img.setBackgroundResource(R.drawable.point_home_normal);

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        15, 15);
                lp1.setMargins(0, 0, DipPixUtils.dip2px(mActivity, 5), 0);
                pointlayout.addView(img, lp1);
                listImg.add(img);
            }

        }

        //一个时不显示圆点
        if (list.size() == 1) {
            pointlayout.setVisibility(View.GONE);
        }

        pagerAdapterHome.setDatas(list);
        vp_top.setCurrentItem(0);
        vp_top.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                setdefault(listImg);
                if (position < listImg.size())
                    listImg.get(position).setBackgroundResource(
                            R.drawable.point_home_normal_select);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    //分类填充
    private void showDataMid(List<ModelCategory> list) {

        if (list.size() != listImgMid.size()) {
            pointlayout_mid.removeAllViews();
            listImgMid.removeAll(listImgMid);
            for (int i = 0; i < Math.ceil((float) list.size() / 10); i++) {
                ImageView img = new ImageView(mActivity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        15, 15);
                img.setLayoutParams(lp);

                if (i == 0)
                    img.setBackgroundResource(R.drawable.point_home_normal_select);
                else
                    img.setBackgroundResource(R.drawable.point_home_normal);

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        15, 15);
                lp1.setMargins(0, 0, DipPixUtils.dip2px(mActivity, 5), 0);
                pointlayout_mid.addView(img, lp1);
                listImgMid.add(img);
            }

        }

        //一个时不显示圆点
        if (list.size() == 1) {
            pointlayout_mid.setVisibility(View.GONE);
        }

        pagerAdapterCategory.setDatas(list);
        vp_mid.setCurrentItem(0);
        vp_mid.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                setdefault(listImgMid);
                if (position < listImgMid.size())
                    listImgMid.get(position).setBackgroundResource(
                            R.drawable.point_home_normal_select);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setdefault(List<ImageView> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setBackgroundResource(R.drawable.point_home_normal);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //在城市页选择城市后的处理,和上次不是同一个城市则重新获取
        if (requestCode == codeGetCity) {
            if (data != null && data.getSerializableExtra("modelAddress") != null) {
                modelAddress = (ModelAddress) data.getSerializableExtra("modelAddress");
                tv_local.setText(modelAddress.getCity());
                onRefresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
