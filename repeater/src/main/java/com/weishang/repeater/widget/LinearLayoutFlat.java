package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.weishang.repeater.R;

/**
 * 带点击波纹扩散效果的imageView;
 *
 * @author momo
 * @Date 2015/3/2 想要的效果: 1:点击下去.按住情况下,会有持续的扩散波纹 2:点击弹开,大范围的扩散,之后触发点击事件
 */
public class LinearLayoutFlat extends LinearLayout {
    // 散开波纹类型
    private static final int NONE = 0;
    private static final int CIRCLE = 1;
    private static final int RECTANGLE = 2;
    //点击启动模式
    private static final int CLICK = 0;
    private static final int DELAY_CLICK = 1;

    private OnClickListener mListener;
    private int mPressCircleWidth;
    private int mFlatPadding;
    private int mFlatColor;
    private int mFlatBackgroud;
    private long mFlatDuration;
    private int mFlatType;
    private int mClickMode;//点击模式,正常点击,延持等动画完毕后启动
    private int x, y;
    private Paint mPaint, mCirclePaint;
    private boolean isComplate;// 动画执行完毕
    private boolean isDismiss;// 消失状态
    private PorterDuffXfermode mXfermode;


    public LinearLayoutFlat(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mCirclePaint = new Paint();
        mPaint.setAntiAlias(true);
        mCirclePaint.setAntiAlias(true);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        // 空实现长按事件,如果用户自定义了长按事件,会直接替换
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageViewFlat);
        setFlatColor(a.getColor(R.styleable.ImageViewFlat_flat_color, Color.WHITE));
        setFlatBackgroud(a.getColor(R.styleable.ImageViewFlat_flat_backgroud, Color.WHITE));
        setFlatPadding((int) a.getDimension(R.styleable.ImageViewFlat_flat_padding, 0));
        setFlagDuration(a.getInteger(R.styleable.ImageViewFlat_flat_duration, 350));
        setFlatType(a.getInt(R.styleable.ImageViewFlat_flat_type, NONE));
        setClickMode(a.getInt(R.styleable.ImageViewFlat_flat_click, CLICK));
        a.recycle();
    }

    public LinearLayoutFlat(Context context) {
        this(context, null);
    }

    public void setFlatColor(int color) {
        this.mFlatColor = getAlphaColor(0x88, color);
        invalidate();
    }

    private int getAlphaColor(int alpha, int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void setFlatPadding(int padding) {
        this.mFlatPadding = padding;
        invalidate();
    }

    public void setFlatBackgroud(int color) {
        this.mFlatBackgroud = color;
        int backgroudRes = getBackgroudResByType();
        if (-1 != backgroudRes) {
            StateListDrawable background = new StateListDrawable();
            background.addState(EMPTY_STATE_SET, getStateDrawable(0xFF, backgroudRes));
            //无效?!
            // background.addState(PRESSED_STATE_SET, getStateDrawable(0xFF,resid));
            setBackgroundDrawable(background);
        }
    }

    public void setFlagDuration(int duration) {
        this.mFlatDuration = duration;
    }

    @SuppressWarnings("deprecation")
    public void setFlatType(int type) {
        this.mFlatType = type;
        // 重新设置背景状态
        setFlatBackgroud(mFlatBackgroud);
    }

    private int getBackgroudResByType() {
        int resid = -1;
        switch (mFlatType) {
            case CIRCLE:
                resid = R.drawable.background_button_float;
                break;
            case RECTANGLE:
                resid = R.drawable.background_button_rectangle;
            case NONE:
            default:
                break;
        }
        return resid;
    }

    private Drawable getStateDrawable(int alpha, int resid) {
        Drawable drawable = getResources().getDrawable(resid);
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layer = (LayerDrawable) drawable;
            GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);
            shape.setColor(getAlphaColor(alpha, mFlatBackgroud));
        }
        return drawable;
    }

    /**
     * 设置点击模式
     *
     * @param mode
     */
    private void setClickMode(int mode) {
        this.mClickMode = mode;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mListener = l;
    }

    @Override
    public void setOnLongClickListener(final OnLongClickListener l) {
        super.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null != l) {
                    l.onLongClick(v);
                }
                startFlatAnim(false);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                // 执行扩散动画,并执行点击事件
                Rect outRect = new Rect();
                getDrawingRect(outRect);
                if (outRect.contains(x, y)) {
                    startFlatAnim(true);
                }
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();
                break;
            default:
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 执行点击事件
     *
     * @param animStart 动画开始
     */
    private void setClick(boolean animStart) {
        if (null == mListener) return;
        switch (mClickMode) {
            case CLICK:
                if (animStart) {
                    mListener.onClick(this);
                }
                break;
            case DELAY_CLICK:
                if (!animStart) {
                    mListener.onClick(this);
                }
                break;
        }
    }

    /**
     * 执行扩散动画
     *
     * @param isClick
     */
    public void startFlatAnim(final boolean isClick) {
        isComplate = false;
        // 执行动画距离,应该是长与宽的交叉线
        int size = (int) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(size);
        // 如果是长按,时间速度加快
        valueAnimator.setDuration(mFlatDuration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mFlatColor = getAlphaColor(isClick ? 0xBB : 0xAA, mFlatColor);
                mPressCircleWidth = Integer.valueOf(animator.getAnimatedValue().toString());
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isClick) {
                    setClick(true);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isComplate = true;
                isDismiss = isClick;
                if (isClick) {
                    setClick(false);
                }
            }
        });
        // 消失动画
        final ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(1f);
        alphaAnimator.setDuration(300);
        alphaAnimator.setInterpolator(new AccelerateInterpolator());
        alphaAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mPaint.setColor(getAlphaColor((int) (0x66 * (1f - animator.getAnimatedFraction())), mFlatColor));
                invalidate();
            }
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isDismiss = false;
            }
        });
        // 点击则执行动画组动画,否则执行扩散动画
        if (isClick) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator).before(alphaAnimator);
            animatorSet.start();
        } else {
            valueAnimator.start();
        }
    }

    /**
     * 创建限制绘制区域位图
     */
    public Bitmap createCropCircle() {
        int width = getWidth() - mFlatPadding * 2;
        int height = getHeight() - mFlatPadding * 2;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        final Rect rect = new Rect(0, 0, width, height);
        mPaint.reset();
        mPaint.setColor(mFlatColor);
        drawFlat(canvas);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(makeCircle(), rect, rect, mPaint);
        mPaint.setXfermode(null);
        return bitmap;
    }

    public Bitmap makeCircle() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        mCirclePaint.setColor(mFlatColor);
        canvas.drawCircle(x, y, mPressCircleWidth, mCirclePaint);
        return bitmap;
    }

    /**
     * 波纹展开后消失动画
     *
     * @param canvas
     */
    private void drawFlat(Canvas canvas) {
        int width = getWidth() - mFlatPadding * 2;
        int height = getHeight() - mFlatPadding * 2;
        switch (this.mFlatType) {
            case NONE:
            case RECTANGLE:
                canvas.drawRect(mFlatPadding, mFlatPadding, width, height, mPaint);
                break;
            case CIRCLE:
                int radius = Math.min(width, height);
                canvas.drawCircle(width / 2, height / 2, radius / 2, mPaint);
            default:
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isComplate) {
            canvas.drawBitmap(createCropCircle(), mFlatPadding, mFlatPadding, null);
        } else if (isDismiss) {
            drawFlat(canvas);
        }
    }

}
