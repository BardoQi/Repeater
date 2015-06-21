package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.UnitUtils;

/**
 * Created by momo on 2015/3/25.
 */
public class MusicProgressBar extends View {
    //拖动模式
    private static final int SEEK_MODE = 0x00;
    private static final int REPEAT_MODE = 0x01;

    //速率角标
    private static final int _PRESS = 0;
    private static final int _SWAP = 1;//切换模式
    private Paint mPaint;
    private float max;
    private float mProgress;
    private float mStart;
    private float mEnd;
    private int mColor;
    private int mRateColor;
    private int mChooseColor;
    private float mRateTextSize;//进度文字大小
    private float mPadding;//内圆边距
    private float mWidth; //线宽
    private float mSelectRadius;//选择宽
    private float[] mFractions;//执行动作

    private boolean isLeftPress;
    private boolean isRightPress;

    private int mode;//拖动模式
    private OnProgressChangeListener mListener;
    private OnSelectListener mSelectListener;

    public MusicProgressBar(Context context) {
        this(context, null, 0);
    }

    public MusicProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFractions = new float[10];
        mEnd = 10;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicProgressBar);
        setColor(a.getColor(R.styleable.MusicProgressBar_mb_color, Color.WHITE));
        setMax(a.getFloat(R.styleable.MusicProgressBar_mb_max, 100));
        setProgress(a.getFloat(R.styleable.MusicProgressBar_mb_progress, 0f));
        setRateColor(a.getColor(R.styleable.MusicProgressBar_mb_rate_color, Color.GREEN));
        setChooseColor(a.getColor(R.styleable.MusicProgressBar_mb_choose_color, Color.BLUE));
        setCirclePadding(a.getDimension(R.styleable.MusicProgressBar_mb_circle_padding, UnitUtils.dip2px(context, 8)));
        setWidth(a.getDimension(R.styleable.MusicProgressBar_mb_width, UnitUtils.dip2px(context, 2)));
        setSelectRadius(a.getDimension(R.styleable.MusicProgressBar_mb_select_radius, UnitUtils.dip2px(context, 5)));
        setRateTextSize(a.getDimension(R.styleable.MusicProgressBar_mb_rate_textsize,UnitUtils.dip2px(context,10)));
        setMode(a.getInt(R.styleable.MusicProgressBar_mb_mode, SEEK_MODE), 0);
        a.recycle();
    }

    private void setRateTextSize(float textSize) {
        this.mRateTextSize=textSize;
        invalidate();
    }

    /**
     * 设置进度颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 设置当前进度大小
     *
     * @param max
     */
    public void setMax(float max) {
        this.mProgress = 0;
        this.max = max;
        invalidate();
    }

    /**
     * 设置绘制进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        if (Math.abs(progress) > max) return;
        this.mProgress = progress;
        if (SEEK_MODE == mode) {
            mStart = mProgress;
            mEnd = (mProgress + 5 < max) ? mProgress + 5 : max;
        }
        invalidate();
    }

    /**
     * 设置进度颜色
     *
     * @param color
     */
    public void setRateColor(int color) {
        this.mRateColor = color;
        invalidate();
    }

    /**
     * 设置选中颜色
     *
     * @param color
     */
    public void setChooseColor(int color) {
        this.mChooseColor = color;
        invalidate();
    }

    /**
     * 设置圆心内边距
     *
     * @param padding
     */
    public void setCirclePadding(float padding) {
        this.mPadding = padding;
        invalidate();
    }

    /**
     * 设置滑动线宽
     *
     * @param width
     */
    public void setWidth(float width) {
        this.mWidth = width;
        invalidate();
    }

    public void setSelectRadius(float radius) {
        this.mSelectRadius = radius;
        invalidate();
    }

    private void setMode(int mode, int duration) {
        startAnim(_SWAP, duration, SEEK_MODE == mode, false);
        this.mode = mode;
    }

    /**
     * 设置滑动模式
     *
     * @param mode 设置模式
     * @see {@link MusicProgressBar#SEEK_MODE 拖动进度模式 }{@link MusicProgressBar#REPEAT_MODE 选取进度模式 }
     */
    public void setMode(int mode) {
        setMode(mode, 200);
    }

    public void wrapMode() {
        setMode((SEEK_MODE == mode) ? REPEAT_MODE : SEEK_MODE, 200);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int halfHeight = height / 2;
        float halfPadding = mPadding / 2;
        //圆球半径
        float radius = (height - mPadding) / 2;
        float progressWidth = width - (radius + halfPadding) * 2;//线长
        float division = progressWidth / max;//每个进度大小
        float x = radius + halfPadding + (division * mProgress);
        float sx = radius + halfPadding + (division * mStart);
        float dx = radius + halfPadding + (division * mEnd);

        //绘中间线
        mPaint.reset();
        mPaint.setAntiAlias(true);

        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mWidth);
        canvas.drawLine(radius + halfPadding, halfHeight, width - radius - halfPadding, halfHeight, mPaint);

        mPaint.setColor(mRateColor);
        //绘制进度线
        canvas.drawLine(radius + halfPadding, halfHeight, x, halfHeight, mPaint);

        float pressHeight = halfHeight / 2 * mFractions[_PRESS];
        //起始位置
        canvas.drawCircle(sx, halfHeight / 2, mSelectRadius * (1f - mFractions[_SWAP]), mPaint);

        //结束位置
        canvas.drawCircle(dx + mSelectRadius, height / 4 * 3, mSelectRadius * (1f - mFractions[_SWAP]), mPaint);

        mPaint.setColor(1 == mFractions[_SWAP] ? Color.TRANSPARENT : mRateColor);
        mPaint.setStrokeWidth(mWidth * (1f - mFractions[_SWAP]));
        //起始竖线
        canvas.drawLine(sx, halfHeight / 2, sx, height / 4 * 3 + (isLeftPress ? pressHeight : 0), mPaint);
        //结束竖线
        canvas.drawLine(dx + mSelectRadius, height / 4 - (isRightPress ? pressHeight : 0), dx + mSelectRadius, height / 4 * 3, mPaint);

        //绘制选中线,设定颜色是因为setStrokeWidth=0时,依然会有一条线,所有在这里判断,如果=1,则设定透明色,不影响动画效果
        mPaint.setColor(1 == mFractions[_SWAP] ? Color.TRANSPARENT : mChooseColor);
        mPaint.setStrokeWidth(mWidth * (1f - mFractions[_SWAP]));
        canvas.drawLine(sx + mWidth / 2, halfHeight, dx + mSelectRadius - mWidth / 2, halfHeight, mPaint);

        //绘制拖动
        mPaint.setColor(mRateColor);
        canvas.drawCircle(x, halfHeight, radius / 2 + (radius / 4 + (mFractions[_PRESS] * halfPadding / 2)) * mFractions[_SWAP], mPaint);

        //绘进度文字
        if(isLeftPress||isRightPress&&(mode==REPEAT_MODE)){
            String text=null;
            Rect bounds=new Rect();
            mPaint.setTextSize(mRateTextSize);
            if(isLeftPress){
                text = DateUtils.getProgressTime((long) (mStart * 1000));
                mPaint.getTextBounds(text,0,text.length(),bounds);
//                mPaint.setAlpha((int) (0xFF*(1f-mFractions[_PRESS])));
                canvas.drawText(text, sx-bounds.width()/2, height/4*3+bounds.height()/2, mPaint);
            } else if(isRightPress){
                text = DateUtils.getProgressTime((long) (mEnd * 1000));
                mPaint.getTextBounds(text,0,text.length(),bounds);
//                mPaint.setAlpha((int) (0xFF*(1f-mFractions[_PRESS])));
                canvas.drawText(text, dx + mSelectRadius-bounds.width()/2,halfHeight/2+bounds.height()/2 , mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        int width = getWidth();
        int height = getHeight();

        float halfPadding = mPadding / 2;
        //圆球半径
        float radius = (height - mPadding) / 2;
        float progressWidth = width - (radius + halfPadding) * 2;//线长
        float division = progressWidth / max;//每个进度大小

        float left = radius + halfPadding;
        float right = width - radius - halfPadding;


        //越界处理
        if (x < left) {
            x = radius + halfPadding;
        } else if (x > right) {
            x = right;
        }

        float pressProgress = (x - left) / division;//按下进度值
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (SEEK_MODE == mode) {
                    //根据按下去点计算当前进度
                    mProgress = pressProgress;
                } else {
                    //按下去,在左边,还是右边
                    if (mStart >= pressProgress) {
                        mStart = pressProgress;
                        isLeftPress = true;
                    } else if (mEnd <= pressProgress) {
                        mEnd = pressProgress;
                        isRightPress = true;
                    } else {
                        //中间,取靠近
                        if (Math.abs(mStart - pressProgress) < Math.abs(mEnd - pressProgress)) {
                            mStart = pressProgress;
                            isLeftPress = true;
                        } else {
                            mEnd = pressProgress;
                            isRightPress = true;
                        }
                    }
                }
                startAnim(_PRESS, 200, true, false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (SEEK_MODE == mode) {
                    mProgress = (x - left) / division;
                } else {
                    if (isLeftPress) {
                        //处理越界
                        mStart = pressProgress >= mEnd ? mEnd : pressProgress;
                    } else if (isRightPress) {
                        mEnd = pressProgress <= mStart ? mStart : pressProgress;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startAnim(_PRESS, 200, false, true);
                break;
        }
        if (mode == SEEK_MODE && null != mListener) {
            mListener.onSeekChange(this, (int) mProgress);
        }
        if (mode == REPEAT_MODE && null != mSelectListener) {
            mSelectListener.onSelect(this, (int) mStart, (int) mEnd, (int) (mEnd - mStart));
        }
        return true;
    }

    /**
     * 设置切换状态进度值
     *
     * @param fraction
     */
    public void setSwapFraction(float fraction) {
        mFractions[_SWAP] = fraction;
        this.mode = 1f != fraction ? REPEAT_MODE : SEEK_MODE;
        invalidate();
    }

    public int getStart() {
        return (int) mStart;
    }

    public int getEnd() {
        return (int) mEnd;
    }

    public void setStart(int start) {
        this.mStart = start;
        invalidate();
    }

    public void setEnd(int end) {
        this.mEnd = end;
        invalidate();
    }

    /**
     * 开启按下动画
     *
     * @param isReverse
     */
    private void startAnim(final int index, final int duration, final boolean isReverse, final boolean isCancel) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                mFractions[index] = isReverse ? fraction : (1f - fraction);
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isCancel) {
                    isLeftPress = isRightPress = false;
                }
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.mListener = listener;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.mSelectListener = listener;
    }

    /**
     * 进度拖动监听
     */
    public interface OnProgressChangeListener {
        void onSeekChange(MusicProgressBar progressBar, int progress);
    }

    public interface OnSelectListener {
        void onSelect(MusicProgressBar progressBar, int start, int end, int value);
    }
}
