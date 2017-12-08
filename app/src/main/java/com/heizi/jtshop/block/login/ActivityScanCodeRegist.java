package com.heizi.jtshop.block.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.jtshop.zxing.activity.CodeUtils;
import com.heizi.jtshop.zxing.activity.ScanCodeFragment;

import static com.heizi.jtshop.block.login.ActRegister.REQUEST_REGIST;


/**
 * Created by leo on 17/7/11.
 */

public class ActivityScanCodeRegist extends BaseSwipeBackCompatActivity implements View.OnClickListener {
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
            String[] str = result.split(result);
//            if (str.length == 4) {
            Intent intent = new Intent();
            intent.putExtra("code", result);
            setResult(REQUEST_REGIST, intent);
            finish();
//            } else {
//                Utils.toastShow(ActivityScanCodeRegist.this, "不是正确的二维码").show();
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

    private void test() {
        Intent intent = new Intent();
        intent.setClass(ActivityScanCodeRegist.this, ActivityScanCodeRegist.class);
        intent.putExtra("content", "12jfnmuhgpbv春风十里jfnmuhgpbv20jfnmuhgpbv100.01");
        startActivity(intent);
        finish();
    }
}
