package com.heizi.jtshop.utils;

import android.content.Context;
import android.content.Intent;

import com.heizi.jtshop.UserModel;
import com.heizi.jtshop.block.login.ActLogin;


/**
 * Created by leo on 17/6/27.
 */

public class Utils {

    public static boolean checkLogin(Context mContext, UserModel userModel) {
        if (userModel != null)
            return true;
        else {
            Intent intent = new Intent();
            intent.setClass(mContext, ActLogin.class);
            mContext.startActivity(intent);
            return false;
        }
    }
}
