package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/10.
 * 放大镜动画drawable
 */
public class MagnifierDrawable extends AnimDrawable {
    private Path mPath;
    private float mRadius;//半径长
    private float mAngle;//角度
    private int mColor;

    public MagnifierDrawable(int color) {
        mPath = new Path();
        this.mColor = color;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRadius = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / 2;
    }

    public void setFraction(float fraction) {
        this.mFraction = fraction;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        float padding = mRadius / 4;
        float size = width - padding;//矩阵尺寸
        float strokeWidth = Math.min(width, height) / 9;
        setPaintValue(mColor, Paint.Style.STROKE, strokeWidth);
        RectF rectF = new RectF(padding / 2, padding / 2, size, size);
        canvas.drawArc(rectF, 45, 360 * mFraction, false, mPaint);

        float radius = Math.min(rectF.centerX(), rectF.centerY()) / 2;
        //画线
        int dx = (int) (radius + padding + strokeWidth / 2 + Math.cos((float) 315 / 360f * 2 * Math.PI) * (radius + padding + strokeWidth / 2));
        int dy = (int) (radius + padding + strokeWidth / 2 - Math.sin((float) 315 / 360f * 2 * Math.PI) * (radius + padding + strokeWidth / 2));

        mPath.reset();
        mPath.moveTo(dx, dy);
        mPath.lineTo(width - 10, height - 10);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void startAnim(int count, int repeatMode, int value, int duration) {
        super.startAnim(count, repeatMode, value, duration);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(mRadius / 2);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

            }
        });
        valueAnimator.start();
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return null;
    }
}
