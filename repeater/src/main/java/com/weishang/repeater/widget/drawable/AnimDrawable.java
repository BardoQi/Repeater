package com.weishang.repeater.widget.drawable;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by Administrator on 2015/3/10.
 * //动画drawable对象
 */
public abstract class AnimDrawable extends Drawable {
    protected Paint mPaint;
    protected float width;
    protected float height;
    protected float mFraction;//动画进度

    public AnimDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        width = bounds.width();
        height = bounds.height();
    }

    /**
     * 设置paint属性
     *
     * @param color 颜色
     * @param style 填充样式
     * @param width 画笔宽
     */
    public void setPaintValue(int color, Paint.Style style, float width) {
        this.mPaint.reset();
        this.mPaint.setColor(color);
        this.mPaint.setStyle(style);
        this.mPaint.setStrokeWidth(width);
        this.mPaint.setAntiAlias(true);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return 0xFF - mPaint.getAlpha();
    }

    /**
     * 只执行一次动画
     */
    public void startAnim() {
        startAnim(0, ValueAnimator.INFINITE, 1, 1 * 1000);
    }

    /**
     * 根据指定模式执行动
     *
     * @param count      动作次数
     * @param repeatMode 动画执行模式  {@ValueAnimator#ValueAnimator.INFINITE/ValueAnimator.RESTART/ValueAnimator.REVERSE}
     */
    public void startAnim(int count, int repeatMode, int value, int duration) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(value);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(count);
        valueAnimator.setRepeatMode(repeatMode);
        valueAnimator.setInterpolator(new LinearInterpolator());
        ValueAnimator.AnimatorUpdateListener listener = getAnimatorUpdateListener();
        valueAnimator.addUpdateListener((null == listener) ? new SimpleAnimatorUpdateListener() : listener);
        valueAnimator.start();
    }

    /**
     * 获得动画更新监听器,实现自定义的属性变幻
     *
     * @return ValueAnimator.AnimatorUpdateListener 动画更新监听
     */
    public abstract ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener();

    public class SimpleAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mFraction = valueAnimator.getAnimatedFraction();
            invalidateSelf();
        }
    }
}
