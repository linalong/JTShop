package com.heizi.jtshop.block.pano;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.block.home.ModelAddress;
import com.heizi.mycommon.sortlistview.CharacterParser;
import com.heizi.mycommon.sortlistview.ClearEditText;
import com.heizi.mycommon.sortlistview.PinyinComparator;
import com.heizi.mycommon.sortlistview.SideBar;
import com.heizi.mycommon.sortlistview.SortAdapter;
import com.heizi.mycommon.sortlistview.SortModel;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Bob on 15/8/18.
 * 会话页面
 */
public class ActivityCity extends BaseSwipeBackCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    ArrayList<SortModel> arrayList = new ArrayList<SortModel>();

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;


    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList = new ArrayList<SortModel>();

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /**
     * listview header
     *
     * @return
     */
    LinearLayout headerView;
    ViewHotCity viewHotCity, viewLocalCity;

    TextView tv_current;//当前城市
    //是否定位成功
    private boolean isLocationSuccess = false;
    //城市名获取城市id
    private ParseStringProtocol parseCityidProtocol;
    private IResponseCallback<DataSourceModel<String>> callbackCityid;

    ParseStringProtocol parseStringProtocol;
    private IResponseCallback<DataSourceModel<String>> callback;

    //高德定位
    private static AMapLocationClient mlocationClient;
    private static AMapLocationClientOption mLocationOption;

    ModelAddress modelCurrent;//当前城市
    ModelAddress modelLocation;//定位的城市

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pano_city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        modelCurrent = (ModelAddress) getIntent().getSerializableExtra("modelAddress");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("选择城市");
        btn_back.setOnClickListener(this);


        // TODO Auto-generated method stub
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnScrollListener(this);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
//                Toast.makeText(ActivityCity.this,
//                        ((SortModel) adapter.getItem(position)).getName(),
//                        Toast.LENGTH_SHORT).show();
                ModelAddress modelAddress = new ModelAddress();
                modelAddress.setCity(((SortModel) adapter.getItem(position - 1)).getName());
                modelAddress.setCity_id(((SortModel) adapter.getItem(position - 1)).getId() + "");
                Bundle bundle = new Bundle();
                bundle.putSerializable("modelAddress", modelAddress);
                finishForResult(bundle);
            }
        });

        adapter = new SortAdapter(ActivityCity.this, SourceDateList);
        adapter.setImageVisible(false);
        sortListView.setAdapter(adapter);

        headerView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.header_pano_city, null);
        sortListView.addHeaderView(headerView);
        sortListView.setHeaderDividersEnabled(false);

        tv_current = (TextView) headerView.findViewById(R.id.tv_current);
        tv_current.setText(modelCurrent.getCity());
        tv_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewLocalCity = (ViewHotCity) headerView.findViewById(R.id.viewLocalCity);
        ArrayList<String[]> arrayLocalCity = new ArrayList<>();
        arrayLocalCity.add(new String[]{"-1", "定位中"});
        viewLocalCity.setDatas(arrayLocalCity);
        viewLocalCity.setOnItemClickLinsener(new ViewHotCity.OnItemClick() {
            @Override
            public void onClick(String key, String value) {
                if (isLocationSuccess) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("modelAddress", modelLocation);
                    finishForResult(bundle);
                }

            }
        });

        viewHotCity = (ViewHotCity) headerView.findViewById(R.id.viewHotCity);
        ArrayList<String[]> arrayHotCity = new ArrayList<>();
        String[] stringHot1 = {"39", "上海"};
        arrayHotCity.add(stringHot1);
        String[] stringHot2 = {"36", "北京"};
        arrayHotCity.add(stringHot2);
        String[] stringHot3 = {"289", "广州"};
        arrayHotCity.add(stringHot3);
        String[] stringHot4 = {"291", "深圳"};
        arrayHotCity.add(stringHot4);
        String[] stringHot5 = {"258", "武汉"};
        arrayHotCity.add(stringHot5);
        String[] stringHot6 = {"40", "天津"};
        arrayHotCity.add(stringHot6);
        String[] stringHot7 = {"438", "西安"};
        arrayHotCity.add(stringHot7);
        String[] stringHot8 = {"162", "南京"};
        arrayHotCity.add(stringHot8);
        String[] stringHot9 = {"175", "杭州"};
        arrayHotCity.add(stringHot9);
        String[] stringHot10 = {"385", "成都"};
        arrayHotCity.add(stringHot10);
        String[] stringHot11 = {"62", "重庆"};
        arrayHotCity.add(stringHot11);
        viewHotCity.setDatas(arrayHotCity);
        viewHotCity.setOnItemClickLinsener(new ViewHotCity.OnItemClick() {
            @Override
            public void onClick(String key, String value) {
                ModelAddress modelAddress = new ModelAddress();
                modelAddress.setCity(value);
                modelAddress.setCity_id(key);
                Bundle bundle = new Bundle();
                bundle.putSerializable("modelAddress", modelAddress);
                finishForResult(bundle);
            }
        });


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


//        String[] strings = getResources().getStringArray(R.array.date);
//        ArrayList<SortModel> arrayList = new ArrayList<SortModel>();
//        for (int i = 0; i < strings.length; i++) {
//            SortModel model = new SortModel();
//            model.setName(strings[i]);
//            arrayList.add(model);
//        }
//
//        setList(arrayList);
        mlocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(mapLocationListener);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(new AMapLocationClientOption());
        mlocationClient.startLocation();

    }

    AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            ModelAddress model = new ModelAddress();
            model.setCity(aMapLocation.getCity());
            model.setLatitude(aMapLocation.getLatitude());
            model.setLongitude(aMapLocation.getLongitude());
            modelLocation = model;
            //定位成功,获取城市id
            Map<String, String> maps = new HashMap<String, String>();
            maps.put("area_name", aMapLocation.getCity());
            parseCityidProtocol.getData(maps, callbackCityid);
            mlocationClient.stopLocation();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.unRegisterLocationListener(mapLocationListener);
        }
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
        parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.other.getCityList.get/");
        callback = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    JSONObject jsonObject = new JSONObject(data.json);

                    JSONArray jsonArray = jsonObject.getJSONArray("area_list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SortModel model = new SortModel();
                        model.setId(jsonArray.getJSONObject(i).getInt("area_id"));
                        model.setName(jsonArray.getJSONObject(i).getString("area_name"));
                        SourceDateList.add(model);
                    }
                    setList(SourceDateList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        };
        Map<String, String> maps = new HashMap<String, String>();
        parseStringProtocol.getData(maps, callback);


        parseCityidProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.other.getCityByName.get/");
        callbackCityid = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    //定位成功获取城市id,城市id获取成功 定位城市内容由定位中变为定位的城市
                    JSONObject jsonObject = new JSONObject(data.json);
                    modelLocation.setCity_id(jsonObject.getString("area_id"));
                    modelLocation.setCity(jsonObject.getString("area_name"));
                    LinearLayout hLinearLayout = (LinearLayout) viewLocalCity.getChildAt(0);
                    TextView tv = (TextView) hLinearLayout.getChildAt(0);
                    tv.setText(modelLocation.getCity());
                    isLocationSuccess = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        };
    }

    public void setList(List<SortModel> SourceDateList) {
        this.SourceDateList = filledData(SourceDateList);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter.updateListView(SourceDateList);
    }

    /**
     * 将数据重新转化一下
     *
     * @return
     */
    private List<SortModel> filledData(List<SortModel> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {
            SortModel sortModel = arrayList.get(i);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(sortModel
                    .getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
        }
        return arrayList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
        switch (arg1) {

            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 滚动状态
                Utils.hideInputMethod(ActivityCity.this);
                mClearEditText.clearFocus();
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
                Utils.hideInputMethod(ActivityCity.this);
                mClearEditText.clearFocus();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ModelAddress modelAddress = new ModelAddress();
        modelAddress.setCity(SourceDateList.get(arg2 - 1).getName());
        modelAddress.setCity_id(SourceDateList.get(arg2 - 1).getId() + "");
        modelAddress.setLatitude(modelLocation.getLatitude());
        modelAddress.setLongitude(modelLocation.getLongitude());
        Bundle bundle = new Bundle();
        bundle.putSerializable("modelAddress", modelAddress);
        finishForResult(bundle);

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
