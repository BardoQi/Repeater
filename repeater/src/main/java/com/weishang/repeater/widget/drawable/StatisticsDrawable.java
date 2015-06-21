package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/10.
 * 统计drawable
 */
public class StatisticsDrawable extends AnimDrawable {
    private int mCount;//排版柱体总数
    private int mGridCount;//网格数
    private int mRaw;//柱体总数
    private float[] mRawFractions;//单个柱体进度
    private Path mPath;
    private int mColor;
    private float mPathLength;//绘制长度
    private float mSize;//单个柱体长度


    public StatisticsDrawable(int color) {
        super();
        mPath = new Path();
        mCount = 8;
        mGridCount = mCount - 2;
        mRaw = mGridCount / 2;
        mRawFractions = new float[mRaw];
        this.mColor = color;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int width = bounds.width();
        int height = bounds.height();
        int size = Math.min(width, height) / 9;
        mPath.moveTo(size, size);
        mPath.lineTo(width - size, size);
        mPath.lineTo(width - size, height - size);
        mPath.lineTo(size, height - size);
        mPath.lineTo(size, size);
        mSize = width / mCount;
        mPathLength = new PathMeasure(mPath, false).getLength();
    }

    @Override
    public void draw(Canvas canvas) {
        //更新绘制长度
        setPaintValue(mColor, Paint.Style.STROKE, mSize);
        //边缘圆角
        mPaint.setPathEffect(new CornerPathEffect(10));
        mPaint.setStrokeWidth(mSize);
        //绘外层边框
        mPaint.setPathEffect(new DashPathEffect(new float[]{mPathLength, mPathLength}, mPathLength - (mFraction * mPathLength)));
        canvas.drawPath(mPath, mPaint);

        mPaint.setStrokeWidth(mSize);
        Rect bounds = getBounds();
        int height = bounds.height();
        float startX, startY;
        //绘内层柱体
        for (int i = 0; i < mRaw; i++) {
            float fraction = mRawFractions[i];
            //当前绘制柱体数
            int currentCount = i % mGridCount - mCount / 2;
            startX = mSize / 2 + mSize * 2 + i * mSize * 2;
            startY = height - mSize * 2;
            canvas.drawLine(startX, startY + (currentCount * mSize * fraction), startX, startY, mPaint);
        }
    }


    @Override
    public void startAnim(int count, int repeatMode, int value, int duration) {
        //外框绘制动画
        super.startAnim(count, repeatMode, value, duration);
        //柱体绘制动画组
        AnimatorSet set = new AnimatorSet();
        ValueAnimator lastAnimator = null;
        for (int i = 0; i < mRaw; i++) {
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
        valueAnimator.setDuration(400);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRawFractions[position] = valueAnimator.getAnimatedFraction();
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
