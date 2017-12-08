package com.heizi.jtshop.block.pano;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.mycommon.utils.DipPixUtils;
import com.heizi.jtshop.R;

import java.util.List;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by leo on 17/5/18.
 */

public class ViewKeyWords extends LinearLayout {
    Context mContext;

    public ViewKeyWords(Context context) {
        super(context);
        this.mContext = context;
    }

    public ViewKeyWords(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public void setDatas(List<String> keyWords) {
        if (getChildCount() > 0)
            removeAllViews();
        for (String s :
                keyWords) {
            TextKeyWords textView = new TextKeyWords(mContext);
            textView.setText(s);
            LayoutParams layoutParam = new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1);
            layoutParam.setMargins(0, 0, 10, 0);
            addView(textView, layoutParam);
        }
    }

    private class TextKeyWords extends TextView {
        public TextKeyWords(Context context) {
            super(context);
            setTextSize(DipPixUtils.getsp(context, 5));
            setTextColor(getResources().getColor(R.color.white));
            setBackgroundDrawable(getResources().getDrawable(R.drawable.home_keywords_bg));
            setPadding(10, 10, 10, 10);
        }
    }
}
