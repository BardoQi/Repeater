package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/10.
 * 演示PathEffect效果
 */
public class PathEffectDrawable extends AnimDrawable {
    private Path mPath;
    private DashPathEffect mPathEffect;
    private float mPathLength;//线总长度

    public PathEffectDrawable() {
        super();
        mPath = new Path();
        setPaintValue(Color.RED, Paint.Style.STROKE, 10);
    }

    /**
     * 添加路径绘制点
     *
     * @param points 路径点
     */
    public void addPathPoints(PointF... points) {
        if (null != points) {
            mPath.reset();
            for (PointF point : points) {
                mPath.lineTo(point.x, point.y);
            }
            mPathLength = new PathMeasure(mPath, false).getLength();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setPathEffect(new DashPathEffect(new float[]{mPathLength, mPathLength}, mPathLength - (mFraction * mPathLength)));
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return 0xFF - this.mPaint.getAlpha();
    }

    /**
     * 返回自定义的animatorUpdateListener监听器
     *
     * @return null 如果返回null则会实现自定义的监听器,改变父类的mFraction取值{#link AnimDrawable#SimpleAnimatorUpdateListener }
     */
    @Override
    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return null;
    }

}
