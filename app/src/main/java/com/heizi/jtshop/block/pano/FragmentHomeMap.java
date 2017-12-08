package com.heizi.jtshop.block.pano;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.block.home.ModelAddress;
import com.heizi.jtshop.block.login.ActLogin;
import com.heizi.jtshop.block.maidan.ActivityScanCode;
import com.heizi.jtshop.utils.UtilDialog;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.SharePreferenceUtil;
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

import static com.heizi.jtshop.Constants.GETVRBYCITYID;
import static com.heizi.jtshop.Constants.SERVER_URL_SHOP;


/**
 * 进入后定位,定位成功用定位的数据,否则用上一次的,刷新时重新定位
 * Created by leo on 17/9/26.
 */

public class FragmentHomeMap extends Fragment implements View.OnClickListener {

    private MapView mapView;
    private AMap aMap;
    private View mapLayout;
    TextView tv_local;
    TextView tv_saosao;
    TextView tv_search;
    ImageView iv_refresh;

    //高德定位
    private static AMapLocationClient mlocationClient;
    private static AMapLocationClientOption mLocationOption;

    ModelAddress modelAddress;
    //定位位置
    double longitude, latitude = 0;
    String currentCity = "";

    //获取列表
    private ParseStringProtocol parsePanoList;
    private IResponseCallback<DataSourceModel<String>> callbackPanoList;
    private List<ModelPano> listData = new ArrayList<>();
    protected boolean isBusy = false;
    public static int codeGetCityMap = 0x009999;

    SharePreferenceUtil sharePreferenceUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mapLayout == null) {
            mapLayout = inflater.inflate(R.layout.fragment_pano_home_map, null);
            mapView = (MapView) mapLayout.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
            }
        } else {

            if (mapLayout.getParent() != null) {
                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
            }
        }
        sharePreferenceUtil = new SharePreferenceUtil(getActivity());
        initData();
        tv_local = (TextView) mapLayout.findViewById(R.id.tv_local);
        tv_saosao = (TextView) mapLayout.findViewById(R.id.tv_saosao);
        tv_search = (TextView) mapLayout.findViewById(R.id.tv_search);
        iv_refresh = (ImageView) mapLayout.findViewById(R.id.iv_refresh);
        tv_local.setOnClickListener(this);
        tv_saosao.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        modelAddress = (ModelAddress) sharePreferenceUtil.readObject("ModelAddress");
        if (modelAddress == null) {
            modelAddress = new ModelAddress();
            modelAddress.setCity("成都");
            modelAddress.setCity_id("385");
//            modelAddress.setLongitude(30.668765);
//            modelAddress.setLatitude(104.071791);

        }
        tv_local.setText(modelAddress.getCity());
        getData();
        reload();


        return mapLayout;
    }

    private void addMarker(ModelPano modelPano, int position) {
        try {
            if (!TextUtils.isEmpty(modelPano.getLatitude()) && !TextUtils.isEmpty(modelPano.getLongitude())) {
                MarkerOptions markerOption = new MarkerOptions();
                LatLng latLng = new LatLng(Double.parseDouble(modelPano.getLatitude()), Double.parseDouble(modelPano.getLongitude()));
                markerOption.position(latLng);
                markerOption.draggable(true);
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.iv_yu)));
                if (modelPano.getStore_id().equals("0")) {
                    markerOption.title(modelPano.getTitle());
                } else {
                    markerOption.title(modelPano.getStore_name());
                }
                Marker addMarker = aMap.addMarker(markerOption);
                addMarker.setObject(position);
                //绑定信息窗点击事件
                aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {
                        int position = (int) arg0.getObject();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", listData.get(position).getPano_url());
                        bundle.putString("pano_id", listData.get(position).getId());
                        bundle.putString("title", listData.get(position).getTitle());
                        bundle.putString("des", listData.get(position).getArea_info());
                        bundle.putString("img", listData.get(position).getPreview_img());
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), ActivityPanoDetail.class);
                        startActivity(intent);
                    }
                });
//        addMarker.showInfoWindow();
//
//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(latLng).build();
//        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(10));}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkerLocation(double longitude, double latitude) {
        MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(latitude, longitude);
        markerOption.position(latLng);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.location_marker)));
        Marker addMarker = aMap.addMarker(markerOption);
        addMarker.showInfoWindow();

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(latLng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }


    private void moveLocation(double longitude, double latitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(latLng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }


    AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            ModelAddress model = new ModelAddress();
            model.setCity(aMapLocation.getCity());
            model.setLatitude(aMapLocation.getLatitude());
            model.setLongitude(aMapLocation.getLongitude());
            addMarkerLocation(model.getLongitude(), model.getLatitude());
            modelAddress = model;
            longitude = aMapLocation.getLongitude();
            latitude = aMapLocation.getLatitude();
            tv_local.setText(modelAddress.getCity());
            currentCity = modelAddress.getCity();
            //定位成功,获取城市id
            getCityByid(aMapLocation.getCity());
            mlocationClient.stopLocation();

        }
    };

    private void getCityByid(String city) {
        ParseStringProtocol parseCityidProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + "&method=jingtu.other.getCityByName.get/");
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("area_name", city);
        parseCityidProtocol.getData(maps, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    //定位成功获取城市id,城市id获取成功 定位城市内容由定位中变为定位的城市
                    JSONObject jsonObject = new JSONObject(data.json);
                    modelAddress.setCity_id(jsonObject.getString("area_id"));
                    getLatlon(modelAddress.getCity());
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
        });

    }

    private void initData() {
        parsePanoList = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + GETVRBYCITYID);
        callbackPanoList = new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
                listData.clear();
                aMap.clear();

                try {
                    JSONArray jsonArray = new JSONArray(data.json);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            ModelPano modelPano = gson.fromJson(jsonArray.getString(i),
                                    ModelPano.class);
//                            modelPano.setLongitude(30.637135);
//                            modelPano.setLatitude(104.049598);
                            addMarker(modelPano, i);
//                            addMarkerLocation(modelPano.getLongitude(), modelPano.getLatitude());
                            listData.add(modelPano);
                        }
                    } else {
//                        mScrollView.setPullLoadEnable(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (modelAddress.getCity().equals(currentCity)) {
                    addMarkerLocation(longitude, latitude);
                } else {
                    moveLocation(modelAddress.getLongitude(), modelAddress.getLatitude());
                }
                LoadingD.hideDialog();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                isBusy = false;
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(getActivity());
            }
        };

    }

    /**
     */
    protected void getData() {
        Map<String, String> maps = new HashMap<>();
        if (modelAddress != null)
            maps.put("city_id", modelAddress.getCity_id());
        if (parsePanoList != null)
            parsePanoList.getData(maps, callbackPanoList);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_local:
                Intent intent = new Intent();
                intent.setClass(getActivity(), ActivityCity.class);
                if (modelAddress != null) {
                    intent.putExtra("modelAddress", modelAddress);
                }
                startActivityForResult(intent, codeGetCityMap);
                break;

            case R.id.tv_saosao:
                if (MyApplication.getInstance().getUserModel() == null) {
                    Intent intent2 = new Intent();
                    intent2.setClass(getActivity(), ActLogin.class);
                    startActivity(intent2);
                } else {
                    Intent intent2 = new Intent(getActivity(), ActivityScanCode.class);
                    startActivity(intent2);
                }
                break;

            case R.id.iv_refresh:
                reload();
                break;
            case R.id.tv_search:
                Intent intent2 = new Intent(getActivity(), ActivitySearch.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //在城市页选择城市后的处理,和上次不是同一个城市则重新获取
        if (requestCode == codeGetCityMap) {
            if (data != null) {
                if (!((ModelAddress) data.getExtras().getSerializable("modelAddress")).getCity_id().equals(modelAddress.getCity_id())) {
                    modelAddress = (ModelAddress) data.getSerializableExtra("modelAddress");
                    tv_local.setText(modelAddress.getCity());
                    getLatlon(modelAddress.getCity());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getLatlon(String cityName) {

        GeocodeSearch geocodeSearch = new GeocodeSearch(getActivity());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null &&
                            geocodeResult.getGeocodeAddressList().size() > 0) {

                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                        double latitude = geocodeAddress.getLatLonPoint().getLatitude();//纬度
                        double longititude = geocodeAddress.getLatLonPoint().getLongitude();//经度
                        String adcode = geocodeAddress.getAdcode();//区域编码

                        modelAddress.setLatitude(latitude);
                        modelAddress.setLongitude(longititude);
                        sharePreferenceUtil.saveObject("ModelAddress", modelAddress);
                        getData();
                        Log.e("地理编码", geocodeAddress.getAdcode() + "");
                        Log.e("纬度latitude", latitude + "");
                        Log.e("经度longititude", longititude + "");

                    }
                }
            }
        });

        GeocodeQuery geocodeQuery = new GeocodeQuery(cityName.trim(), "29");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);

    }

    /**
     * 重新定位
     */
    private void reload() {
        mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(mapLocationListener);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(new AMapLocationClientOption());
        mlocationClient.startLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UtilDialog.dismiss();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.unRegisterLocationListener(mapLocationListener);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(Constants.MAP_FRAGMENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
