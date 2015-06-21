package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/11.
 * 用户图标
 */
public class PersonalDrawable extends AnimDrawable {
    private int mColor;

    public PersonalDrawable(int mColor) {
        this.mColor = mColor;
    }

    @Override
    public void draw(Canvas canvas) {
        float itemSize = Math.min(width, height) / 9;
        setPaintValue(mColor, Paint.Style.STROKE, itemSize);
        //画头像 ,高一半,宽1/3
        float corverWidth = width / 3 + width / 9 * mFraction;

        float centerY = height / 2;
        //头像左距离
        float left = (width - corverWidth) / 2;
        canvas.drawArc(new RectF(left,
                        itemSize + height / 12 * mFraction,
                        left + corverWidth,
                        itemSize + centerY),
                0, 360, false, mPaint);

        canvas.drawArc(new RectF(itemSize, itemSize + centerY, width - itemSize, height + centerY - itemSize * 2), 180, 180, false, mPaint);


        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width - itemSize, itemSize, itemSize / 2, mPaint);
    }

    public void setFraction(float fraction) {
        this.mFraction = fraction;
        invalidateSelf();
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return null;
    }
}
