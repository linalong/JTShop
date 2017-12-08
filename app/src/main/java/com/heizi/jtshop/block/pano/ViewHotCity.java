package com.heizi.jtshop.block.pano;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heizi.mycommon.utils.DipPixUtils;
import com.heizi.jtshop.R;

import java.util.ArrayList;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by leo on 17/5/18.
 */

public class ViewHotCity extends LinearLayout {
    Context mContext;
    private OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick(String key, String value);
    }

    public void setOnItemClickLinsener(OnItemClick onItemClickLinsener) {
        this.onItemClick = onItemClickLinsener;
    }

    public ViewHotCity(Context context) {
        super(context);
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public ViewHotCity(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }


    public void setDatas(final ArrayList<String[]> keyWords) {
        if (getChildCount() <= 0) {
            if (keyWords.size() % 3 == 1) {
                keyWords.add(new String[2]);
                keyWords.add(new String[2]);
            } else if (keyWords.size() % 3 == 2) {
                keyWords.add(new String[2]);
            }
            for (int i = 0; i < keyWords.size() / 3; i++) {
                //新建线性布局
                LinearLayout hLinearLayout = new LinearLayout(mContext);
                LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                layoutParams.setMargins(0, 20, 0, 0);
                hLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                hLinearLayout.setLayoutParams(layoutParams);
                //添加textview
                for (int j = 0; j < 3; j++) {
                    MyTextView textView = new MyTextView(mContext);
                    textView.setText(keyWords.get(i * 3 + j)[1]);
                    LayoutParams textLayoutParam = new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1);
                    textLayoutParam.setMargins(0, 0, 20, 0);
                    hLinearLayout.addView(textView, textLayoutParam);
                    if (TextUtils.isEmpty(textView.getText()))
                        textView.setVisibility(INVISIBLE);

                    final int finalI = i;
                    final int finalJ = j;
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClick != null) {
                                onItemClick.onClick(keyWords.get(finalI * 3 + finalJ)[0],keyWords.get(finalI * 3 + finalJ)[1]);
                            }
                        }
                    });
                }
                addView(hLinearLayout);
            }
        }

    }

    private class MyTextView extends TextView {
        public MyTextView(Context context) {
            super(context);
            setTextSize(14);
            setTextColor(getResources().getColor(R.color.gray11));
            setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
            setPadding(0, 20, 0, 20);
            setGravity(Gravity.CENTER);
        }
    }
}
