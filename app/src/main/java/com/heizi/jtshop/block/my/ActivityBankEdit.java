package com.heizi.jtshop.block.my;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.lljjcoder.citypickerview.widget.TypePicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by leo on 17/10/13.
 */

public class ActivityBankEdit extends BaseSwipeBackCompatActivity implements View.OnClickListener {

    @InjectView(R.id.et_name)
    EditText et_name;
    @InjectView(R.id.ll_bank)
    LinearLayout ll_bank;
    @InjectView(R.id.tv_bank)
    TextView tv_bank;
    @InjectView(R.id.ll_address)
    LinearLayout ll_address;
    @InjectView(R.id.tv_address)
    TextView tv_address;
    @InjectView(R.id.et_bank_info)
    EditText et_bank_info;
    @InjectView(R.id.et_bank_num)
    EditText et_bank_num;
    @InjectView(R.id.et_detail)
    EditText et_detail;

    @InjectView(R.id.tv_delete)
    TextView tv_delete;

    /**
     * 银行卡列表
     */
    TypePicker typePickerBank;

    boolean isAdd;
    ModelBank modelBank;

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
        return R.layout.activity_bank;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isAdd = getIntent().getExtras().getBoolean("isAdd");
        if (!isAdd) {
            modelBank = (ModelBank) getIntent().getExtras().getSerializable("model");
        } else {
            modelBank = new ModelBank();
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
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("保存");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    Utils.toastShow(ActivityBankEdit.this, "请输入持卡人姓名");
                }
                if (TextUtils.isEmpty(tv_bank.getText().toString())) {
                    Utils.toastShow(ActivityBankEdit.this, "选择银行");
                }
                if (TextUtils.isEmpty(tv_address.getText().toString())) {
                    Utils.toastShow(ActivityBankEdit.this, "请选择银行所在区域");
                }
                if (TextUtils.isEmpty(et_bank_info.getText().toString())) {
                    Utils.toastShow(ActivityBankEdit.this, "请填写支行信息");
                }
                if (TextUtils.isEmpty(et_bank_num.getText().toString())) {
                    Utils.toastShow(ActivityBankEdit.this, "请输入银行卡号");
                }
//                if (TextUtils.isEmpty(et_detail.getText().toString())) {
//                    Utils.toastShow(ActivityBankEdit.this, "请填写银行详细地址");
//                }
                else {
                    upload();
                }
            }
        });

        typePickerBank = new TypePicker(this);
        typePickerBank.setOnTypeItemClickListener(new TypePicker.OnTypeItemClickListener() {
            @Override
            public void onSelected(String agencyName, String agencyId) {
                modelBank.setPdc_bank_code(agencyId);
                modelBank.setPdc_bank_name(agencyName);
                tv_bank.setText(agencyName);
            }

            @Override
            public void onCancel() {

            }
        });

        if (isAdd) {
            tv_delete.setVisibility(View.GONE);
            tv_title.setText("绑定银行卡");
        } else {
            tv_title.setText("修改银行卡");
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
                modelBank.setPdc_province_id(mCitySelected[1]);
                modelBank.setPdc_city_id(mCitySelected[3]);
                modelBank.setPdc_city_area(mCitySelected[5]);
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

        if (!isAdd && modelBank != null) {
            et_name.setText(modelBank.getPdc_user_name());
            tv_bank.setText(modelBank.getPdc_bank_name());
            et_bank_info.setText(modelBank.getPdc_bank_branch_name());
            et_bank_num.setText(modelBank.getPdc_bank_no());
            et_detail.setText(modelBank.getPdc_city_area());
            tv_address.setText(modelBank.getPdc_area_info());
        }
    }

    @Override
    protected void initData() {
        super.initData();
        protocolGetProvinces = new ParseListProtocol<>(this, SERVER_URL_SHOP + "&method=jingtu.other.areaList.get/", ModelCityPicker.class);
        callbackGetProvinces = new IResponseCallback<DataSourceModel<ModelCityPicker>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelCityPicker> data) {
                isBusy=false;
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
                isBusy=false;
            }

            @Override
            public void onStart() {
                isBusy=true;
            }
        };
    }

    private void getData(String parentid) {
        Map<String, String> map = new HashMap<>();
        map.put("area_id", parentid);
        protocolGetProvinces.getData(map, callbackGetProvinces);
    }

    private void getBankList() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + BANKS);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                try {
                    JSONArray jsonArray = new JSONArray(data.json);
                    Map map = new HashMap();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        map.put(jsonArray.getJSONObject(i).getString("bank_code"), jsonArray.getJSONObject(i).getString("bank_name"));
                    }
                    typePickerBank.setData(map);
                    typePickerBank.show();
                    LoadingD.hideDialog();
                    isBusy = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                isBusy = false;
                Utils.toastShow(ActivityBankEdit.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityBankEdit.this);
            }
        });
    }

    /**
     * 上传
     */
    private void upload() {
        ParseStringProtocol parseStringProtocol;
        if (isAdd) {
            parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + BANKBINDADD);
        } else {
            parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + BANKBINDEDIT);
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("pdc_user_name", et_name.getText().toString());
        map.put("pdc_bank_code", modelBank.getPdc_bank_code());
        map.put("pdc_bank_branch_name", et_bank_info.getText().toString());
        map.put("pdc_province_id", modelBank.getPdc_province_id());
        map.put("pdc_city_id", modelBank.getPdc_city_id());
        map.put("pdc_city_area", modelBank.getPdc_city_area());
        map.put("pdc_area_info", tv_address.getText().toString());
        map.put("pdc_bank_no", et_bank_num.getText().toString());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityBankEdit.this, "成功绑定银行卡信息");
                setResult(100);
                isBusy = false;
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                isBusy = false;
                Utils.toastShow(ActivityBankEdit.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityBankEdit.this);
            }
        });
    }

    private void delete() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(this, SERVER_URL_SHOP + BANKDELETE);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        map.put("pdc_id", modelBank.getPdc_id());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                LoadingD.hideDialog();
                Utils.toastShow(ActivityBankEdit.this, "成功解绑银行卡");
                setResult(100);
                isBusy = false;
                finish();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                isBusy = false;
                Utils.toastShow(ActivityBankEdit.this, errorModel.getMsg());
            }

            @Override
            public void onStart() {
                isBusy = true;
                LoadingD.showDialog(ActivityBankEdit.this);
            }
        });
    }

    @OnClick({R.id.ll_bank, R.id.ll_address, R.id.tv_delete, R.id.tv_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bank:
                if (!typePickerBank.hasData())
                    getBankList();
                else
                    typePickerBank.show();
                break;

            case R.id.ll_address:
                if (!cityPicker.hasProvince())
                    getData("0");
                else
                    cityPicker.show();
                break;

            case R.id.tv_delete:
                if (isBusy) {
                    Utils.toastShow(ActivityBankEdit.this, getResources().getString(R.string.request_string));
                } else {
                    delete();
                }
                break;
            case R.id.tv_right:
                if (isBusy) {
                    Utils.toastShow(ActivityBankEdit.this, getResources().getString(R.string.request_string));
                } else {
                    upload();
                }
                break;
        }
    }
}
