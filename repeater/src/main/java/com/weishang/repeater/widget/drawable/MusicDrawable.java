package com.weishang.repeater.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by momo on 2015/3/9.
 *
 * @author momo
 *         仿QQ音乐播放效果
 */
public class MusicDrawable extends Drawable {

    private final MusicDrawableConfig mConfig;
    private ValueAnimator mValueAnimator;
    private int mDegress;
    private Xfermode mXfermode;
    private Paint mPaint;

    /**
     * {#link MusicDrawable#MusicDrawableBuild}
     */
    private MusicDrawable() {
        this(null);
    }

    public MusicDrawable(MusicDrawableBuild build) {
        mPaint = new Paint();
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mConfig = null == build ? MusicDrawableBuild.createDefault().getConfig() : build.getConfig();
    }

    public void startAnim() {
        if (null == mValueAnimator) {
            mValueAnimator = ObjectAnimator.ofInt(360);
            mValueAnimator.setDuration(10 * 1000);
            mValueAnimator.setRepeatCount(-1);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setInterpolator(new LinearInterpolator());
        }
        final int degress = mDegress;
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDegress = degress + Integer.valueOf(valueAnimator.getAnimatedValue().toString());
                invalidateSelf();
            }
        });
        mValueAnimator.start();
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        if (null != mValueAnimator) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mConfig.mWidth);
        mPaint.setColor(mConfig.mBackgroudColor);
        mPaint.setStyle(Paint.Style.FILL);
        //画背景
        canvas.drawArc(new RectF(bounds), 0, 360, true, mPaint);

        int radius = Math.min(width, height) / 2;
        int size = (0 == mConfig.mSize) ? radius / 5 : mConfig.mSize;
        //画外圈
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mConfig.mColor);
        canvas.drawArc(new RectF(size, size, width - size, height - size), 0, 360, true, mPaint);
        //绘移动环
        canvas.drawArc(new RectF(size * 2, size * 2, width - size * 2, height - size * 2), 270 + mDegress, 30, false, mPaint);

        mPaint.setStrokeWidth(mConfig.mWidth);
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        canvas.drawArc(new RectF(centerX - size, centerY - size, centerX + size, centerY + size), 0, 360, true, mPaint);
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
     * drawable 属性配置对象
     */
    private static class MusicDrawableConfig {
        public int mSize;//内环宽
        private int mWidth;//线宽
        public int mColor;//环颜色
        public int mBackgroudColor;//背景色
    }

    /**
     * drawable建造对象
     */
    public static class MusicDrawableBuild {
        public MusicDrawableConfig mConfig;

        public MusicDrawableBuild() {
            mConfig = new MusicDrawableConfig();
            mConfig.mColor = Color.GRAY;
            mConfig.mWidth = 1;
            mConfig.mBackgroudColor = Color.DKGRAY;
        }

        /**
         * 设置边距
         *
         * @param size 大小
         * @return
         */
        public MusicDrawableBuild setSize(int size) {
            this.mConfig.mSize = size;
            return this;
        }

        public MusicDrawableBuild setColor(int color) {
            this.mConfig.mColor = color;
            return this;
        }

        /**
         * 设置绘制背景色
         *
         * @param color
         * @return
         */
        public MusicDrawableBuild setBackgroudColor(int color) {
            this.mConfig.mBackgroudColor = color;
            return this;
        }

        /**
         * 设置绘制线宽
         *
         * @param width
         * @return
         */
        public MusicDrawableBuild setStrokeWidth(int width) {
            this.mConfig.mWidth = width;
            return this;
        }

        public MusicDrawable build() {
            return new MusicDrawable(this);
        }

        public MusicDrawableConfig getConfig() {
            return mConfig;
        }

        /**
         * 创造默认build项
         *
         * @return
         */
        public static MusicDrawableBuild createDefault() {
            return new MusicDrawableBuild().setColor(Color.GRAY).setBackgroudColor(Color.DKGRAY).setSize(50);
        }
    }
}
