package com.heizi.jtshop.block.my;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseListProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;
import com.lljjcoder.citypickerview.model.CityModel;
import com.lljjcoder.citypickerview.model.DistrictModel;
import com.lljjcoder.citypickerview.model.ProvinceModel;
import com.lljjcoder.citypickerview.widget.CityPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.heizi.jtshop.block.maidan.ActivityOrderConfirm.requestAddAddress;

/**
 * Created by leo on 17/10/7.
 */

public class ActivityAddressEdit extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.et_name)
    EditText et_name;
    @InjectView(R.id.et_tel)
    EditText et_tel;
    @InjectView(R.id.et_detail)
    EditText et_detail;


    @InjectView(R.id.tv_address)
    TextView tv_address;
    //    @InjectView(R.id.tv_street)
//    TextView tv_street;
    @InjectView(R.id.tv_delete)
    TextView tv_delete;

    boolean isAdd;
    ModelAddress modelAddress;

    private CityPicker cityPicker;
    private CityPicker.Builder builder;
    private int type = 0;//0获取全部省,1根据某省获取市,2根据市获取区
    /**
     * 省市区
     */
    private ParseListProtocol<ModelCityPicker> protocolGetProvinces;
    private IResponseCallback<DataSourceModel<ModelCityPicker>> callbackGetProvinces;
    private String[] mCitySelected = new String[6];
    private String[] mCityScrolled = new String[2], mProvinceScrolled = new String[2];

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_address_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isAdd = getIntent().getExtras().getBoolean("isAdd");
        if (!isAdd) {
            modelAddress = (ModelAddress) getIntent().getExtras().getSerializable("model");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isAdd) {
            tv_delete.setVisibility(View.GONE);
            tv_title.setText("添加新地址");
        } else {
            tv_title.setText("修改收货地址");
            tv_delete.setVisibility(View.VISIBLE);
        }
        builder = new CityPicker.Builder(this);
        cityPicker = builder.textSize(20)
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {

            @Override
            public void onSelected(String... citySelected) {
                mCitySelected = citySelected;
//                if (citySelected[4] == "获取中") {
//                    mCitySelected[4] = citySelected[2];
//                }
                tv_address.setText(mCitySelected[0] + " " + mCitySelected[2] + " "
                        + mCitySelected[4]);
//
//
//                Log.d("====", citySelected[0] + " " + citySelected[1] + " "
//                        + citySelected[2] + citySelected[3] + " " + citySelected[4] + " "
//                        + citySelected[5]);
            }

            @Override
            public void onCancel() {
            }
        });
        //滑动到某城市,获取区
        cityPicker.setOnScrollToCityListener(new CityPicker.OnScrollToCityListener() {
            @Override
            public void onScrolled(String[] cityScrolled) {
                mCityScrolled = cityScrolled;
                type = 2;
                getData(cityScrolled[1]);
            }
        });
        //滑动到某省,获取市
        cityPicker.setOnScrollToProvinceListener(new CityPicker.OnScrollToProvinceListener() {
            @Override
            public void onScrolled(String[] provinceScrolled) {
                mProvinceScrolled = provinceScrolled;
                type = 1;
                getData(provinceScrolled[1]);
            }
        });

        if (!isAdd && modelAddress != null) {
            tv_address.setText(modelAddress.getArea_info());
            et_name.setText(modelAddress.getTrue_name());
            et_tel.setText(modelAddress.getMob_phone());
            et_detail.setText(modelAddress.getAddress());
        }
    }

    @Override
    protected void initData() {
        super.initData();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("保存");
        protocolGetProvinces = new ParseListProtocol<>(this, SERVER_URL_SHOP + "&method=jingtu.other.areaList.get/", ModelCityPicker.class);
        callbackGetProvinces = new IResponseCallback<DataSourceModel<ModelCityPicker>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelCityPicker> data) {
                if (data.list.size() > 0) {
                    if (type == 0) {
                        List<ProvinceModel> listProvince = new ArrayList<>();
                        for (ModelCityPicker regionInfo : data.list) {
                            ProvinceModel model = new ProvinceModel();
                            model.setId(regionInfo.getArea_id());
                            model.setName(regionInfo.getArea_name());
                            listProvince.add(model);
                        }
                        cityPicker.initProvince(listProvince);
                        cityPicker.show();
                    } else if (type == 1) {
                        List<CityModel> listCity = new ArrayList<>();
                        for (ModelCityPicker regionInfo : data.list) {
                            CityModel model = new CityModel();
                            model.setId(regionInfo.getArea_id());
                            model.setName(regionInfo.getArea_name());
                            listCity.add(model);
                        }
                        cityPicker.setCityData(mProvinceScrolled[0], listCity);
                    } else if (type == 2) {
                        List<DistrictModel> listDistrict = new ArrayList<>();
                        for (ModelCityPicker regionInfo : data.list) {
                            DistrictModel model = new DistrictModel();
                            model.setId(regionInfo.getArea_id());
                            model.setName(regionInfo.getArea_name());
                            listDistrict.add(model);
                        }
                        cityPicker.setAreasData(mCityScrolled[0], listDistrict);
                    }

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

    private void getData(String parentid) {
        Map<String, String> map = new HashMap<>();
        map.put("area_id", parentid);
        protocolGetProvinces.getData(map, callbackGetProvinces);
    }

    @OnClick({R.id.tv_delete, R.id.tv_address, R.id.tv_street, R.id.tv_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete:
                delete();
                break;
            case R.id.tv_address:
                if (!cityPicker.hasProvince())
                    getData("0");
                else
                    cityPicker.show();
                break;
            case R.id.tv_street:
                break;
            case R.id.tv_right:
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    Utils.toastShow(ActivityAddressEdit.this, "请填写收货人");
                    return;
                } else if (TextUtils.isEmpty(et_tel.getText().toString())) {
                    Utils.toastShow(ActivityAddressEdit.this, "请填写联系电话");
                    return;
                } else if (!Utils.checkIsCellphone(et_tel.getText().toString())) {
                    Utils.toastShow(ActivityAddressEdit.this, "手机号填写有误");
                    return;
                } else if (TextUtils.isEmpty(et_detail.getText().toString())) {
                    Utils.toastShow(ActivityAddressEdit.this, "请输入详细地址");
                    return;
                }
                if (isAdd) {
                    if (mCitySelected[0] == null || mCitySelected[1] == null || mCitySelected[2] == null || mCitySelected[3] == null || mCitySelected[4] == null || mCitySelected[5] == null) {
                        Utils.toastShow(ActivityAddressEdit.this, "请选择收货地址");
                        return;
                    }
                }

                postData();
                break;
        }
    }

    private void postData() {

        if (isAdd) {
            ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.address.addressAdd.post/");
            Map<String, String> map = new HashMap<>();
            map.put("token", userModel.getToken());
            map.put("true_name", et_name.getText().toString());
            map.put("area_id", mCitySelected[5]);
            map.put("city_id", mCitySelected[3]);
            map.put("area_info", tv_address.getText().toString());
            map.put("address", et_detail.getText().toString());
            map.put("mob_phone", et_tel.getText().toString());
            map.put("is_default", "1");
            parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
                @Override
                public void onSuccess(DataSourceModel<String> data) {
                    isBusy = false;
                    try {
                        JSONObject jsonObject = new JSONObject(data.json);
                        ModelAddress modelAddress = new ModelAddress();
                        modelAddress.setAddress_id(jsonObject.getString("address_id"));
                        modelAddress.setAddress(et_detail.getText().toString());
                        modelAddress.setArea_info(tv_address.getText().toString());
                        modelAddress.setTrue_name(et_name.getText().toString());
                        modelAddress.setMob_phone(et_tel.getText().toString());

                        Intent intent = new Intent();
                        intent.putExtra("modelAddress", modelAddress);
                        LoadingD.hideDialog();
                        Utils.toastShow(ActivityAddressEdit.this, "添加收货地址成功");
                        setResult(requestAddAddress, intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(ErrorModel errorModel) {
                    isBusy = false;
                    tv_right.setEnabled(true);
                    LoadingD.hideDialog();
                    Utils.toastShow(ActivityAddressEdit.this, "添加收货地址失败");
                }

                @Override
                public void onStart() {
                    isBusy = true;
                    tv_right.setEnabled(false);
                    LoadingD.showDialog(ActivityAddressEdit.this);
                }
            });
        } else {
            ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.address.addressEdit.post/");
            Map<String, String> map = new HashMap<>();
            map.put("token", userModel.getToken());
            map.put("address_id", modelAddress.getAddress_id());
            map.put("true_name", et_name.getText().toString());
            if (mCitySelected[5] != null && mCitySelected[3] != null) {
                map.put("area_id", mCitySelected[5]);
                map.put("city_id", mCitySelected[3]);
            } else {
                map.put("area_id", modelAddress.getAddress_id());
                map.put("city_id", modelAddress.getCity_id());
            }
            map.put("area_info", tv_address.getText().toString());
            map.put("address", et_detail.getText().toString());
            map.put("mob_phone", et_tel.getText().toString());
            map.put("is_default", "1");
            parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
                @Override
                public void onSuccess(DataSourceModel<String> data) {
                    isBusy = false;
                    try {
                        JSONObject jsonObject = new JSONObject(data.json);
                        ModelAddress modelAddress = new ModelAddress();
                        modelAddress.setAddress_id(jsonObject.getString("address_id"));
                        modelAddress.setAddress(et_detail.getText().toString());
                        modelAddress.setArea_info(tv_address.getText().toString());
                        modelAddress.setTrue_name(et_name.getText().toString());
                        modelAddress.setMob_phone(et_tel.getText().toString());

                        LoadingD.hideDialog();
                        Utils.toastShow(ActivityAddressEdit.this, "修改收货地址成功");
                        Intent intent = new Intent();
                        intent.putExtra("modelAddress", modelAddress);
                        setResult(requestAddAddress, intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(ErrorModel errorModel) {
                    isBusy = false;
                    LoadingD.hideDialog();
                    Utils.toastShow(ActivityAddressEdit.this, "修改收货地址失败");
                }

                @Override
                public void onStart() {
                    isBusy = true;
                    LoadingD.showDialog(ActivityAddressEdit.this);
                }
            });
        }
    }

    private void delete() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + "&method=jingtu.address.addressDel.post/");
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("address_id", modelAddress.getAddress_id());
        parseStringProtocol.postData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                isBusy = false;
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressEdit.this, "删除成功!");
                setResult(requestAddAddress);
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                isBusy = false;
                LoadingD.hideDialog();
                Utils.toastShow(ActivityAddressEdit.this, "删除失败!");
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityAddressEdit.this);
            }
        });
    }
}
