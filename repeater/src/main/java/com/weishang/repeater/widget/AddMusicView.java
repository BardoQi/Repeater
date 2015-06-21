package com.weishang.repeater.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.utils.Loger;
import com.weishang.repeater.utils.UnitUtils;

import java.util.Random;

/**
 * Created by momo on 2015/4/7.
 * 1:从小放到大.objectanimator
 * 2:颜色渐变,0---100% green--->red
 * 3:中间圆心,会随着进度增加,变大.
 * 4:随机位置出现的
 * 5:添加完成.中间圆放大到整体,缩小不见.
 */
public class AddMusicView extends View {
    private Paint mPaint;
    private Random mRandom;
    private Bitmap mBitmap;
    private SparseArray<Float> mDegrees;
    private int mCount;
    private int max;
    private int mSize;
    private float mScaleFraction;//放大圆心

    public AddMusicView(Context context) {
        this(context, null, 0);
    }

    public AddMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRandom = new Random();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSize = UnitUtils.dip2px(context, 20);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_press);
        mDegrees = new SparseArray<Float>();
        setMax(10);
    }

    public void add() {
        if (++mCount >= max) return;
        final int degrees = mRandom.nextInt(360);
        mDegrees.append(degrees, 0f);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDegrees.append(degrees, valueAnimator.getAnimatedFraction());
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDegrees.remove(degrees);
                Loger.i(this, "count:" + mCount + " max:" + max);
                if (mCount >= max && 0 == mDegrees.size()) {
                    startComplateAnim();
                }
            }
        });
        valueAnimator.start();
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerX = width >> 1;
        int centerY = height >> 1;

        int radius = Math.min(width, height) >> 1;

        mPaint.setColor(App.getResourcesColor(R.color.yellow));
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        //小圆取4/1-->4/3
        mPaint.setColor(AnimationUtils.evaluate((mCount * 1f / max), App.getResourcesColor(R.color.white), App.getResourcesColor(R.color.dark_yellow)));
        canvas.drawCircle(centerX, centerY, radius / 4 + radius / 4 * mScaleFraction + (int) (radius / 4 * (mCount * 1f / max)), mPaint);

        int size = mDegrees.size();
        for (int i = 0; i < size; i++) {
            int degrees = mDegrees.keyAt(i);
            Float fraction = mDegrees.valueAt(i);

            //计算从dx-centerX  dy-centerY的渐进
            int dx = (int) ((radius - mSize) + Math.cos((float) (90 - degrees) / 360f * 2 * Math.PI) * (radius - mSize));
            int dy = (int) ((radius - mSize) - Math.sin((float) (90 - degrees) / 360f * 2 * Math.PI) * (radius - mSize));

            canvas.save();
            canvas.translate(dx - mSize / 2 + (centerX - dx) * fraction, dy - mSize / 2 + (centerY - dy) * fraction);
            canvas.drawBitmap(mBitmap, null, new RectF(0, 0, mSize, mSize), null);
            canvas.restore();
        }
    }

    public void startComplateAnim() {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScaleFraction = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCount = 0;
                mScaleFraction = 0f;
                mDegrees.clear();
                ViewPropertyAnimator.animate(AddMusicView.this).scaleX(0f).scaleY(0f).setDuration(600);
            }
        });
        valueAnimator.start();
    }
}
