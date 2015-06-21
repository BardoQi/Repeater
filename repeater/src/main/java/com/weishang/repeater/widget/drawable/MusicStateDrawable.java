package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/10.
 * 音乐播放状态drawable对象
 */
public class MusicStateDrawable extends AnimDrawable {
    private Path mLeftPath;
    private Path mRightPath;
    private float mPadding;
    private float mCenterPadding;
    private float mDegrees;
    private float mCenterRadius;//中间圆角
    private int mColor;
    private boolean isPlay;//播放状态

    public MusicStateDrawable() {
        mLeftPath = new Path();
        mRightPath = new Path();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int width = bounds.width();
        int height = bounds.height();
        int size = Math.min(width, height) / 2;
        mPadding = 0;
        mCenterPadding = size / 10;
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidateSelf();
    }

    public void setCenterPadding(float centerPadding) {
        this.mCenterPadding = centerPadding;
        invalidateSelf();
    }

    public void setPadding(float padding) {
        this.mPadding = padding;
        invalidateSelf();
    }

    public void setDegress(float degress) {
        this.mDegrees = degress;
        invalidateSelf();
    }

    public void setCenterRadius(float radius) {
        this.mCenterRadius = radius;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;
        int size = (int) (Math.sqrt(radius * radius + radius * radius) - mPadding * 2);// 圆内切正方形边长

        int left = (width - size) / 2;
        int top = (height - size) / 2;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        //边缘圆角
        mPaint.setPathEffect(new CornerPathEffect(mCenterRadius * (mDegrees * 1f / 90)));

        mLeftPath.reset();
        mRightPath.reset();


        //内边距
        float padding = mCenterPadding * mDegrees * 1f / 90;
        //上边距
        float marginRight = top * mDegrees * 1f / 90f;

        //右边止点
        float rightX = width - marginRight - mPadding * (90 - mDegrees) * 1f / 90;
        mLeftPath.moveTo(left, top);
        mLeftPath.lineTo(rightX, top + (centerY - top) * (90 - mDegrees) * 1f / 90);
        mLeftPath.lineTo(rightX, centerY - padding);
        mLeftPath.lineTo(left, centerY - padding);
        //之所以会有这段,多余的绘制,是因为想让CornerPathEffect四边生效,只有lineTo绘过之后,才能圆角化
        mLeftPath.lineTo(left, top);
        mLeftPath.lineTo(rightX, top + (centerY - top) * (90 - mDegrees) * 1f / 90);

        mRightPath.moveTo(left, top + size);
        mRightPath.lineTo(rightX, centerY + size / 2 * mDegrees * 1f / 90);
        mRightPath.lineTo(rightX, centerY + padding);
        mRightPath.lineTo(left, centerY + padding);


        mRightPath.lineTo(left, top + size);
        mRightPath.lineTo(rightX, centerY + size / 2 * mDegrees * 1f / 90);

        canvas.save();
        canvas.rotate(mDegrees, width / 2, centerY);
        canvas.drawPath(mLeftPath, mPaint);
        canvas.drawPath(mRightPath, mPaint);
        canvas.restore();
    }

    /**
     * 设置播放状态
     *
     * @param isPlay
     */
    public void setPlay(boolean isPlay) {
        this.isPlay = isPlay;
        startAnim(0, -1, 90, 300);
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float degress = Float.valueOf(valueAnimator.getAnimatedValue().toString());
                setDegress(isPlay ? degress : (90 - degress));
            }
        };
    }
}
