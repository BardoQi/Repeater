package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/10.
 * 系统设置图标Drawable
 */
public class SettingDrawable extends AnimDrawable {
    private float[] mItemFractions;
    private float mDegress;
    private int mColor;
    private Path mPath;

    public SettingDrawable(int color) {
        mPath = new Path();
        mItemFractions = new float[6];
        this.mColor = color;
    }

    public void setDegress(float degress) {
        this.mDegress = degress;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        float size = Math.min(width, height) / 9 * 2;
        setPaintValue(mColor, Paint.Style.STROKE, size / 2);
        RectF rectF = new RectF(size, size, width - size, height - size);
        canvas.drawArc(rectF, 310, (360 * mFraction), false, mPaint);

        float radius = Math.min(rectF.centerX(), rectF.centerY()) / 2 + size / 2;
        float strokeWidth = (float) (2 * Math.PI * radius) / 12;
        float translateSize = size - strokeWidth / 4 * 3;
        mPaint.setStrokeWidth(strokeWidth);

        // 齿轮高:
        for (int i = 0; i < 6; i++) {
            canvas.save();
            canvas.translate(translateSize, translateSize);
            int dx = (int) (radius + Math.cos((float) (30 - i * 60) / 360f * 2 * Math.PI) * radius);
            int dy = (int) (radius - Math.sin((float) (30 - i * 60) / 360f * 2 * Math.PI) * radius);
            float itemHeight = size / 2 * mItemFractions[i];
//            //起点
            RectF rect = new RectF(dx, dy, dx + itemHeight, dy + itemHeight);
            mPath.reset();
            mPath.moveTo(rect.left, rect.top);
            mPath.lineTo(rect.right, rect.bottom);
            canvas.rotate((45 + (i + 1) * 60) % 360, rect.centerX(), rect.centerY());
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
        }
    }

    @Override
    public void startAnim(int count, int repeatMode, int value, int duration) {
        super.startAnim(count, repeatMode, value, duration);
        AnimatorSet set = new AnimatorSet();
        ValueAnimator lastAnimator = null;
        for (int i = 0; i < 6; i++) {
            ValueAnimator animator = getAnimByPosition(i);
            if (null != lastAnimator) {
                set.play(animator).after(lastAnimator);
            }
            lastAnimator = animator;
        }
        set.start();
    }

    /**
     * 获得指定变化动画
     */
    private ValueAnimator getAnimByPosition(final int position) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mItemFractions[position] = valueAnimator.getAnimatedFraction();
                invalidateSelf();
            }
        });
        return valueAnimator;
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return null;
    }
}
