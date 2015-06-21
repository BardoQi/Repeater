package com.weishang.repeater.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.R;

import static com.weishang.repeater.utils.UnitUtils.dip2px;

/**
 * Created by momo on 2015/3/29.
 * 时钟进度控件
 */
public class ClockWheelView extends View {
    private Paint mPaint;
    private int mColor;
    private int mWidth;
    private int mPadding;
    private int mBackgroudColor;
    private int mProgress;//当前进度
    private int max;//最大指数
    private int mCount;//旋转周数
    private int maxCount;//最大旋转周数
    private float mScaleFraction;
    private int mTextSize;//设置文字大小
    private int mDuration;//动作时间
    private ValueAnimator mValueAnimator;

    public ClockWheelView(Context context) {
        this(context, null, 0);
    }

    public ClockWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setColor(Color.WHITE);
        setStrokeWidth(dip2px(context, 2));
        setCircleBackgroudColor(Color.DKGRAY);
        setClickPadding(dip2px(context, 2));
        setTextSize(getResources().getDimensionPixelSize(R.dimen.normal_text));
        setMax(100);
        setDuration(2 * 1000);
    }

    public void setClickPadding(int padding) {
        this.mPadding = padding;
        invalidate();
    }

    private void setTextSize(int textSize) {
        this.mTextSize = textSize;
        invalidate();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public void setStrokeWidth(int width) {
        this.mWidth = width;
        invalidate();
    }

    private void setCircleBackgroudColor(int color) {
        this.mBackgroudColor = color;
        invalidate();
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setCount(int count) {
        this.mCount = count;
        invalidate();
    }

    public void startAnim() {
        mValueAnimator = ObjectAnimator.ofInt(max);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgress = Integer.valueOf(valueAnimator.getAnimatedValue().toString());
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    public void stopAnim() {
        if (null != mValueAnimator) {
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    public boolean isRunning() {
        return null != mValueAnimator && mValueAnimator.isRunning();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        int radius = Math.min(width, height) / 2;

        canvas.save();
        canvas.scale(0.5f + mScaleFraction * 0.5f, 0.5f + mScaleFraction * 0.5f, centerX, centerY);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroudColor);
        //画背景
        canvas.drawCircle(centerX, centerY, radius, mPaint);


        //画中间圆心
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        mPaint.setColor(mColor);
        canvas.drawCircle(centerX, centerY, radius - mPadding, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, 5, mPaint);

        //画旋转圈数
        mPaint.setTextSize(mTextSize);
        Rect rect = new Rect();
        String text = String.valueOf(mCount);
        mPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, centerX - rect.width() / 2, centerY + rect.height() / 2, mPaint);

        //画固定指针
        canvas.drawLine(centerX, centerY, centerX, centerY / 2, mPaint);
        float angle = 90 - 360 * mProgress / max;
        int dx = (int) (radius / 2 + Math.cos(angle / 360f * 2 * Math.PI) * radius / 2);
        int dy = (int) (radius / 2 - Math.sin(angle / 360f * 2 * Math.PI) * radius / 2);
        //画指定指针
        canvas.translate(centerX / 2, centerX / 2);
        canvas.drawLine(centerX / 2, centerY / 2, dx, dy, mPaint);
        canvas.restore();
    }

}
