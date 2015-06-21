package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.utils.LoopList;

import static com.weishang.repeater.utils.UnitUtils.dip2px;


/**
 * Created by momo on 2015/3/28.
 * 自定义表情切换控件
 * 1:画笔颜色,画笔宽
 * 2:眼睛的边框颜色 宽,眼睛内颜色
 */
public class FaceView extends View {
    private static final int HAPPY = 0;
    private static final int NORMAL = 1;
    private static final int NOT_HAPPY = 2;
    private LoopList<Integer> mLoopList;
    private int mColor;
    private float mWidth;

    private int mEarColor;
    private float mEarWidth;
    private int mInnerColor;

    private int mStatus;
    private Path mPath;
    private Paint mPaint;
    private PorterDuffXfermode mode;
    private float[] mFractions;

    public FaceView(Context context) {
        this(context, null, 0);
    }

    public FaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mFractions = new float[10];
        mLoopList = new LoopList<Integer>();
        mLoopList.offer(HAPPY);
        mLoopList.offer(NORMAL);
        mLoopList.offer(NOT_HAPPY);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FaceView);
        setInnerColor(a.getColor(R.styleable.FaceView_fv_color, Color.DKGRAY));
        setStatus(a.getInt(R.styleable.FaceView_fv_status, HAPPY), 0);
        setEarColor(a.getColor(R.styleable.FaceView_fv_ear_color, 0XFFEFEFEF));
        setEarWidth(a.getDimension(R.styleable.FaceView_fv_ear_width, dip2px(context, 2)));
        setColor(a.getColor(R.styleable.FaceView_fv_color, 0XFFEFEFEF));
        setWidth(a.getDimension(R.styleable.FaceView_fv_width, dip2px(context, 2)));
        a.recycle();
    }

    public void setWidth(float width) {
        this.mWidth = width;
        invalidate();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public void setEarWidth(float width) {
        this.mEarWidth = width;
        invalidate();
    }

    public void setEarColor(int color) {
        this.mEarColor = color;
        invalidate();
    }

    public void setInnerColor(int color) {
        this.mInnerColor = color;
        invalidate();
    }

    /**
     * 设置表情状态
     *
     * @param status
     */
    private void setStatus(int status, int duration) {
        this.mStatus = status;
        startAnim(this.mStatus, duration);
    }

    /**
     * 切换下一个状态
     */
    public void toggle() {
        setStatus(mLoopList.next(), 300);
    }

    private void startAnim(final int index, int duration) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFractions[index] = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerX = width >> 1;
        int centerY = height >> 1;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //画外圈圆
        canvas.drawOval(new RectF(mWidth, mWidth, width - mWidth, height - mWidth), mPaint);


        //测试代码
//        mPaint.setColor(Color.BLUE);
//        mPaint.setStrokeWidth(mWidth / 2);
//        canvas.drawRect(0, 0, width, height, mPaint);
//
//        canvas.drawLine(0, centerY, width, centerY, mPaint);
//        canvas.drawLine(centerX, 0, centerX, height, mPaint);
//
//        canvas.drawLine(0, centerY / 2, width, centerY / 2, mPaint);
//        canvas.drawLine(0, centerY + centerY / 2, width, centerY + centerY / 2, mPaint);
//
//        canvas.drawLine(centerX / 2, 0, centerX / 2, height, mPaint);
//        canvas.drawLine(centerX + centerX / 2, 0, centerX + centerX / 2, height, mPaint);


        //画两个眼睛
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mEarWidth);

        int ex = width / 8;
        int ey = height / 8;

        int sc = canvas.saveLayer(0, 0, width, height, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.white));
        canvas.drawOval(new RectF(ex, ey * 2, ex + ex * 2, centerY), mPaint);
        canvas.drawOval(new RectF(centerX + ex, ey * 2, centerX + ex + ex * 2, centerY), mPaint);

        mPaint.setXfermode(mode);
        canvas.save();
        canvas.translate(0, ey);
        mPaint.setColor(mEarColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(new RectF(ex, ey * 2, ex + ex * 2, centerY), mPaint);
        canvas.drawOval(new RectF(centerX + ex, ey * 2, centerX + ex + ex * 2, centerY), mPaint);
        canvas.restore();
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(new RectF(ex, ey * 2, ex + ex * 2, centerY), mPaint);
        canvas.drawOval(new RectF(centerX + ex, ey * 2, centerX + ex + ex * 2, centerY), mPaint);


        float x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0;
        switch (mStatus) {
            case HAPPY:
                x1 = ex * 2;
                y1 = centerY + ey;
                x2 = centerX;
                //NOT_HAPPY y2 = centerY - ey;
                y2 = centerY - ey + centerY * mFractions[HAPPY];
                x3 = centerX + ex * 2;
                y3 = centerY + ey;
                break;
            case NORMAL:
                x1 = ex * 2;
                y1 = centerY + ey;
                x2 = centerX;
//                y2 = centerY + ey;
                y2 = centerY + ey * 3 - ey * 2 * mFractions[NORMAL];
                x3 = centerX + ex * 2;
                y3 = centerY + ey;
                break;
            case NOT_HAPPY:
                x1 = ex * 2;
                y1 = centerY + ey;
                x2 = centerX;
                //normal  y2 = centerY + ey;
//                y2 = centerY - ey;
                y2 = (centerY + ey) - ey * 2 * mFractions[NOT_HAPPY];
                x3 = centerX + ex * 2;
                y3 = centerY + ey;
                break;
        }
        //嘴巴
        mPath.reset();
        mPath.moveTo(ex * 2, centerY + ey);
        mPath.cubicTo(x1, y1, x2, y2, x3, y3);

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.save();
        canvas.translate(0, ey);
        canvas.drawCircle(ex * 2, centerY + ey, 5, mPaint);
        canvas.drawCircle(centerX + ex * 2, centerY + ey, 5, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }
}
