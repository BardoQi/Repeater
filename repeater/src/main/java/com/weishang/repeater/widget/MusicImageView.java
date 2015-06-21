package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.utils.UnitUtils;

/**
 * 音乐播放标记
 *
 * @author momo
 * @Date 2015/3/3
 * <p/>
 * //记:在绘图内的单位,应尽量都以float来计,使用int,会出现偏差
 */
public class MusicImageView extends ImageViewFlat {
    private Paint mPaint;
    private Path mLeftPath;
    private Path mRightPath;
    private int mPadding;
    private float mCenterPadding;
    private float mCenterRadius;
    private int mColor;
    private float mDegrees;
    private float mCornerPadding;
    private boolean isPlaying;

    public MusicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftPath = new Path();
        mRightPath = new Path();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicImageView);
        setMusicPadding((int) a.getDimension(R.styleable.MusicImageView_mv_padding, UnitUtils.dip2px(context, 5)));
        setCenterPadding(a.getDimension(R.styleable.MusicImageView_mv_center_padding, UnitUtils.dip2px(context, 2)));
        setCenterRadius(a.getDimension(R.styleable.MusicImageView_mv_corner_radius, UnitUtils.dip2px(context, 2)));
        setCornerPadding(a.getDimension(R.styleable.MusicImageView_mv_corner_padding, UnitUtils.dip2px(context, 2)));
        setColor(a.getColor(R.styleable.MusicImageView_mv_color, Color.WHITE));
        a.recycle();
    }

    public MusicImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicImageView(Context context) {
        this(context, null, 0);
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    private void setCornerPadding(float padding) {
        this.mCornerPadding = padding;
        invalidate();
    }

    public void setCenterRadius(float radius) {
        this.mCenterRadius = radius;
        invalidate();
    }

    public void setCenterPadding(float padding) {
        this.mCenterPadding = padding;
        invalidate();
    }

    public void setDegress(int degress) {
        this.mDegrees = degress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;
        int size = (int) Math.sqrt(radius * radius + radius * radius) - mPadding * 2;// 圆内切正方形边长

        int left = (width - size) / 2;
        int top = (height - size) / 2;

        // 使用fraction对矩阵生效,(1f-fraction)对三角形生效
        float fraction = mDegrees * 1f / 90;
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        //边缘圆角
        mPaint.setPathEffect(new CornerPathEffect(mCenterRadius * fraction));

        mLeftPath.reset();
        mRightPath.reset();


        //内边距
        float padding = mCenterPadding * fraction;
        //上边距

        float cornerPadding = mCornerPadding * fraction;
        //右边止点
        float rightX = width - left - cornerPadding * 2;
        mLeftPath.moveTo(left, top + cornerPadding);
        mLeftPath.lineTo(rightX, top + (centerY - top) * (1f - fraction) + cornerPadding);
        mLeftPath.lineTo(rightX, centerY - padding);
        mLeftPath.lineTo(left, centerY - padding);
        //之所以会有这段,多余的绘制,是因为想让CornerPathEffect四边生效,只有lineTo绘过之后,才能圆角化
        mLeftPath.lineTo(left, top + cornerPadding);
        mLeftPath.lineTo(rightX, top + (centerY - top) * (1f - fraction) + cornerPadding);

        mRightPath.moveTo(left, top + size - cornerPadding);
        mRightPath.lineTo(rightX, centerY + size / 2 * fraction - cornerPadding);
        mRightPath.lineTo(rightX, centerY + padding);
        mRightPath.lineTo(left, centerY + padding);


        mRightPath.lineTo(left, top + size - cornerPadding);
        mRightPath.lineTo(rightX, centerY + size / 2 * fraction - cornerPadding);


        double sizeRadius = size / 2 * Math.sqrt(2);

        //获得左侧顶上315度时 x轴位置,与绘制x比较
        int x = (int) (radius + Math.cos((90f - 315f) / 360f * 2 * Math.PI) * radius);
        int dx = (int) (sizeRadius + Math.cos((90f - 315f) / 360f * 2 * Math.PI) * sizeRadius);
        canvas.save();
        canvas.translate(Math.abs((width / 2 - (float) sizeRadius + dx) - x - (width - rightX)) / 2 * (1f - fraction), cornerPadding);
        canvas.rotate(mDegrees, centerX, centerY);
        canvas.drawPath(mLeftPath, mPaint);
        canvas.drawPath(mRightPath, mPaint);
        canvas.restore();
    }

    private void startAnim(final boolean isPlay) {
        if (isPlay == isPlaying) return;
        this.isPlaying = isPlay;
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(90);
        valueAnimator.setDuration(400);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer degrees = Integer.valueOf(valueAnimator.getAnimatedValue().toString());
                setDegress(isPlay ? degrees : (90 - degrees));
            }
        });
        valueAnimator.start();
    }

    public void startPlay() {
        startAnim(true);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopPlay() {
        startAnim(false);
    }

    public void setMusicPadding(int musicPadding) {
        this.mPadding = musicPadding;
        invalidate();
    }

}
