package com.weishang.repeater.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.listener.ViewImageClickListener;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.weishang.repeater.utils.UnitUtils.dip2px;


/**
 * Created by momo on 2015/3/26.
 * 微看点订阅展示功能
 */
public class SubscribeView extends RelativeLayout {
    private static final int ANIM_DEGREES = 40;
    @ID(id = R.id.iv_user_cover)
    private ImageView mUserCover;
    private int mCount;//绘制线数据
    private int mColor;//绘制线颜色
    private float mWidth;//线宽
    private float mInterval;//线与线递增间隔
    private float minWidth;//最小圆心宽
    private Paint mPaint;
    private final ArrayList<Ball> mBalls;
    private final HashMap<Long, Float> mWaveFractions;
    private OnSubscribeClickListener mlistener;

    public SubscribeView(Context context) {
        this(context, null, 0);
    }

    public SubscribeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubscribeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWaveFractions = new HashMap<Long, Float>();
        mBalls = new ArrayList<Ball>();
        LayoutInflater.from(context).inflate(R.layout.subscribe_layout, this);
        ViewInject.init(this);
        //设置默认取值
        setDefault(context);

        mUserCover.setOnClickListener(new ViewImageClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) return;
                final long timeMillis = System.currentTimeMillis();
                //不断重绘
                ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
                valueAnimator.setDuration(1 * 1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mWaveFractions.put(timeMillis, valueAnimator.getAnimatedFraction());
                        invalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mWaveFractions.remove(timeMillis);
                    }
                });
                valueAnimator.start();

                PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 0.9f);
                PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 0.9f);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXHolder, scaleYHolder);
                animator.setDuration(500);
                animator.setRepeatCount(1);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.start();

            }
        }));
    }

    private void setDefault(Context context) {
        setCount(4);
        setColor(0XEFEFEF);
        setInterval(dip2px(context, 10));
        setWidth(dip2px(context, 1));
        setMinWidth(dip2px(context, 50));
    }

    public void setCount(int count) {
        this.mCount = count;
        invalidate();
    }

    public void setWidth(float width) {
        this.mWidth = width;
        invalidate();
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
        invalidate();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public void setMinWidth(int width) {
        this.minWidth = width;
        invalidate();
    }

    /**
     * 添加一个订阅标签
     *
     * @param text     文本
     * @param size     当前标签大小
     * @param color    标签颜色背景
     * @param textSize 文字大小
     * @param line     依靠线
     * @param degrees  环绕线的角度
     * @param offset   距离线偏移量
     */
    public void addSubscribe(final String text, int size, int color, int textSize, final int line, final int degrees, final int offset) {
        Context context = getContext();
        final TextViewFlat textView = new TextViewFlat(context);
        final int viewSize = dip2px(context, size);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setGravity(Gravity.CENTER);
        final int index = getChildCount();
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mlistener) {
                    mlistener.onSubscribeClick(index - 1, text);
                }
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.subscribe_cover);
        if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            stateListDrawable.selectDrawable(0);
            Drawable current = stateListDrawable.getCurrent();
            if (current instanceof LayerDrawable) {
                LayerDrawable layer = (LayerDrawable) current;
                GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.ll_cover_press);
                shape.setColor(getDarkColor(color));
            }
            stateListDrawable.selectDrawable(1);
            current = stateListDrawable.getCurrent();
            if (current instanceof LayerDrawable) {
                LayerDrawable layer = (LayerDrawable) current;
                GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.ll_cover);
                shape.setColor(color);
            }
        }
        textView.setBackgroundDrawable(drawable);
        addView(textView, viewSize, viewSize);

        mBalls.add(new Ball(viewSize, line, degrees, offset));
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = getWidth();
                int height = getHeight();
                if (0 != width && 0 != height) {
                    ViewHelper.setAlpha(textView, 0f);
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    //根据依附line获得半径
                    int size = Math.min(width, height);
                    float interval = (size / 2 - minWidth) / (mCount + 1);
                    float radius = (minWidth + line * interval + line * mInterval + dip2px(getContext(), offset));
                    int dx = (int) (radius + Math.cos((float) (90 - degrees - 30) / 360f * 2 * Math.PI) * radius);
                    int dy = (int) (radius - Math.sin((float) (90 - degrees - 30) / 360f * 2 * Math.PI) * radius);

                    ViewHelper.setX(textView, width / 2 - radius - viewSize / 2 + dx);
                    ViewHelper.setY(textView, height / 2 - radius - viewSize / 2 + dy);
                }
            }
        });
    }

    private int getDarkColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.rgb(r + 30 > 0xFF ? r - 30 : r + 30, g + 30 > 0xFF ? g - 30 : g + 30, b + 30 > 0xFF ? b - 30 : b + 30);
    }

    /**
     * 清除所有标记
     */
    public void clearSubscribes() {
        if (null != mBalls) {
            mBalls.clear();
        }
        int childCount = getChildCount();
        for (int i = 2; i < childCount; i++) {
            removeView(getChildAt(i));
        }
    }


    public void startAnim() {
        int length = mBalls.size();
        for (int i = 0; i < length; i++) {
            //渐变动画
            ViewPropertyAnimator.animate(getChildAt(1 + i)).alpha(1f).setDuration(300).setStartDelay(i * 50);

            //移动动画
            ValueAnimator valueAnimator = ObjectAnimator.ofInt(ANIM_DEGREES);
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setStartDelay(i * 50);
            valueAnimator.start();
            final int finaIndex = i;
            final Ball ball = mBalls.get(i);
            final int startDegrees = ball.degrees - ANIM_DEGREES;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int degrees = startDegrees + (int) (valueAnimator.getAnimatedFraction() * ANIM_DEGREES);
                    ball.degrees = degrees;
                    setBallValue(finaIndex, ball);
                }
            });
        }
    }

    /**
     * 恢复动画
     */
    public void recovery() {
        int length = mBalls.size();
        for (int i = 0; i < length; i++) {
            ViewCompat.setAlpha(getChildAt(1 + i), 0f);
            Ball ball = mBalls.get(i).clone();
            ball.degrees -= ANIM_DEGREES;
            setBallValue(i, ball);
        }
    }

    /**
     * 测试方法
     *
     * @param degrees
     * @param line
     */
    public void setDegrees(int index, int viewSize, int degrees, int offset, int line) {
        Context context = getContext();
        setBallValue(index, new Ball(dip2px(context, viewSize), line, degrees, dip2px(context, offset)));
    }

    private void setBallValue(int index, Ball ball) {
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        float interval = (size / 2 - minWidth) / (mCount + 1);
        float radius = (minWidth + ball.line * interval + ball.line * mInterval + ball.offset);
        int dx = (int) (radius + Math.cos((float) (90 - ball.degrees) / 360f * 2 * Math.PI) * radius);
        int dy = (int) (radius - Math.sin((float) (90 - ball.degrees) / 360f * 2 * Math.PI) * radius);

        ViewHelper.setX(getChildAt(1 + index), width / 2 - radius - ball.size / 2 + dx);
        ViewHelper.setY(getChildAt(1 + index), height / 2 - radius - ball.size / 2 + dy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        float interval = (size / 2 - minWidth) / (mCount + 1);

        mPaint.setStrokeWidth(mWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.save();
        canvas.translate(width / 2, height / 2);
        //绘制线
        for (int i = 0; i < mCount; i++) {
            mPaint.setColor(getAlphaColor(mColor, i));
            canvas.drawCircle(0, 0, (minWidth + i * interval + i * mInterval), mPaint);
        }
        canvas.restore();

        //绘波浪
        int coverWidth = mUserCover.getWidth();
        int coverHeight = mUserCover.getHeight();
        int coverRadius = Math.min(coverWidth, coverHeight) >> 1;
        float maxRadius = (minWidth + (mCount - 1) * interval + (mCount - 1) * mInterval);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xFFA3DDA2);

        for (Map.Entry<Long, Float> entry : mWaveFractions.entrySet()) {
            mPaint.setAlpha((int) (0xFF * (1f - entry.getValue())));
            canvas.drawCircle(width / 2, height / 2, coverRadius + ((maxRadius - coverRadius) * entry.getValue()), mPaint);
        }
    }

    /**
     * 绘制以0.05f透明度递减
     *
     * @param color
     * @param i
     * @return
     */
    private int getAlphaColor(int color, int i) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb((int) (0xFF * 0.5f - (i * 0xFF * 0.05f)), r, g, b);
    }

    public void setOnSubscribeClickListener(OnSubscribeClickListener listener) {
        this.mlistener = listener;
    }

    public interface OnSubscribeClickListener {
        void onSubscribeClick(int position, String word);
    }

    class Ball implements Cloneable {
        public int size;
        public int line;
        public int degrees;
        public int offset;

        Ball(int size, int line, int degrees, int offset) {
            this.size = size;
            this.line = line;
            this.degrees = degrees;
            this.offset = offset;
        }

        @Override
        public Ball clone() {
            return new Ball(this.size, this.line, this.degrees, this.offset);
        }
    }
}
