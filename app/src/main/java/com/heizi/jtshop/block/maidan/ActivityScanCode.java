package com.heizi.jtshop.block.maidan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.heizi.jtshop.zxing.activity.CodeUtils;
import com.heizi.jtshop.zxing.activity.ScanCodeFragment;
import com.heizi.mycommon.utils.LoadingD;
import com.heizi.mycommon.utils.Utils;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;


/**
 * Created by leo on 17/7/11.
 */

public class ActivityScanCode extends BaseSwipeBackCompatActivity implements View.OnClickListener {
    private ScanCodeFragment scanCodeFragment;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("扫一扫");
        btn_back.setOnClickListener(this);
        scanCodeFragment = new ScanCodeFragment();
        // 为二维码扫描界面设置定制化界面
//        CodeUtils.setFragmentArgs(scanCodeFragment, R.layout.fragment_scan_code2);
        scanCodeFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, scanCodeFragment).commit();
//        test();
    }


    @Override
    protected void initData() {
        super.initData();
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result, String scanType) {
            Log.d("====","扫码结果: "+result);
//            String[] str = result.split(ActivityMaidan.spilt);
//            if (str.length == 4) {
//                Intent intent = new Intent();
//                intent.setClass(ActivityScanCode.this, ActivityMaidan.class);
//                intent.putExtra("content", result);
//                startActivity(intent);
//                finish();
//            } else {
//                Utils.toastShow(ActivityScanCode.this, "不是正确的二维码").show();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ScanCodeFragment.getInstance().repeateScan();
//                    }
//                }, 2000);
//
//            }

        }

        @Override
        public void onAnalyzeFailed() {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }

}
