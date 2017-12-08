package com.heizi.jtshop.block.my;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by leo on 17/10/23.
 */

public class TextViewDrawableCenter extends TextView {
    public TextViewDrawableCenter(Context context) {
        this(context, null);
    }

    public TextViewDrawableCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable leftDrawable = drawables[0]; //drawableLeft
            Drawable rightDrawable = drawables[2];//drawableRight
            if (leftDrawable != null || rightDrawable != null) {
                //1,获取text的width
                float textWidth = getPaint().measureText(getText().toString());
                //2,获取padding
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth;
                float bodyWidth;
                if (leftDrawable != null) {
                    //3,获取drawable的宽度
                    drawableWidth = leftDrawable.getIntrinsicWidth();
                    //4,获取绘制区域的总宽度
                    bodyWidth = textWidth + drawablePadding + drawableWidth;
                } else {
                    drawableWidth = rightDrawable.getIntrinsicWidth();
                    bodyWidth = textWidth + drawablePadding + drawableWidth;
                    //图片居右设置padding
                    setPadding(0, 0, (int) (getWidth() - bodyWidth), 0);
                }
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
