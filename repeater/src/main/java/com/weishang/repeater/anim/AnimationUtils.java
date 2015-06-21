package com.weishang.repeater.anim;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.Keyframe;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.App;

/**
 * 管理动画显示
 *
 * @author momo
 * @Date 2014/6/5
 */
public class AnimationUtils {
    private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(android.R.color.transparent);

    /**
     * 获得渐变透明度动画效果
     */
    public static void startAlphaAnimation(View view, float startAlpha, float endAlpha) {
        startAlphaAnimation(view, startAlpha, endAlpha, 500);
    }

    public static void startAlphaAnimation(View view, float startAlpha, float endAlpha, long duration) {
        AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
        alpha.setDuration(duration);
        alpha.setFillAfter(true);
        view.startAnimation(alpha);
    }

    /**
     * 获得缩放扩大的动画
     */
    public static void startScaleAnimation(View view, float fromX, float toX, float fromY, float toY) {
        ScaleAnimation scale = new ScaleAnimation(fromX, toX, fromY, toY);
        scale.setDuration(3000);
        view.startAnimation(scale);
    }

    /**
     * 获得画面位置移动动画的效果
     */
    public static void startTranslateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        TranslateAnimation translate = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translate.setDuration(2000);
        view.startAnimation(translate);
    }

    public static void startTranslateAnimation(View view, float fromX, float toX, float fromY, float toY, long duration) {
        TranslateAnimation animation = new TranslateAnimation(fromX, toX, fromY, toY);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * 设置自身轨迹移动动画
     *
     * @param view
     * @param fromXType
     * @param fromXValue
     * @param toXType
     * @param toXValue
     * @param fromYType
     * @param fromYValue
     * @param toYType
     * @param toYValue
     */
    public void startTranslateAnimation(View view, int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue, long duration) {
        TranslateAnimation animation = new TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        view.startAnimation(animation);
    }

    /**
     * 获得画面旋转动画的效果
     */
    public static void startRotateAnimation(View view, float fromDegrees, float toDegrees, float pivotX, float pivotY) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotate);
    }

    /**
     * 获得指定控件位置面画旋转的动画
     */
    public static void startRotateAnimation(View view, float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue, long duration) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
        rotate.setDuration(duration);
        rotate.setInterpolator(new LinearInterpolator());
        // 设置动画循环播放
        rotate.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotate);
    }

    /**
     * 开始动画
     *
     * @param view        : 开始动画控件
     * @param resId       : 动画资源
     * @param r           :执行事件
     * @param delayMillis :
     */
    public static void startAnimation(Context context, View view, int resId, Runnable r, long delayMillis) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, resId);
        view.startAnimation(animation);
        // 执行其他事件
        if (null != r) {
            new Handler().postDelayed(r, delayMillis);
        }
    }

    /**
     * 开启位图动画
     */
    public static void setTransitionDrawable(ImageView view, int color, int duration) {
        final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{TRANSPARENT_DRAWABLE, new ColorDrawable(color)});
        view.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }

    public static void setTransitionDrawable(View view, int color, int duration) {
        final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{TRANSPARENT_DRAWABLE, new ColorDrawable(color)});
        view.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }

    /**
     * 开始背景动画
     */
    public static void setBackGroundAnim(View view, int duration, int... colors) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view, "backgroundColor", colors);
        colorAnim.setDuration(duration);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    /**
     * 开始view内image重绘动画
     */
    public static void startViewImageAnim(final View view) {
        int duration = 800;
        final Drawable drawable = getViewDrawable(view);
        if (null != drawable) {
            final AnimValue animValue = ViewAnimHolder.getAnimValue(view);
            final int bitmapWidth = drawable.getIntrinsicWidth();
            final int bitmapHeight = drawable.getIntrinsicHeight();
            animValue.setWidth(-1 == bitmapWidth ? view.getWidth() : bitmapWidth);
            animValue.setHeight(-1 == bitmapHeight ? view.getHeight() : bitmapHeight);
            ValueAnimator squashAnimWidth = ObjectAnimator.ofInt(animValue, "width", animValue.getWidth() - 5);
            squashAnimWidth.setDuration(duration / 4);
            squashAnimWidth.setRepeatCount(1);
            squashAnimWidth.setRepeatMode(ValueAnimator.REVERSE);
            squashAnimWidth.setInterpolator(new DecelerateInterpolator());

            ValueAnimator squashAnimHeight = ObjectAnimator.ofInt(animValue, "height", animValue.getHeight() - 5);
            squashAnimHeight.setDuration(duration / 4);
            squashAnimHeight.setRepeatCount(1);
            squashAnimHeight.setRepeatMode(ValueAnimator.REVERSE);
            squashAnimHeight.setInterpolator(new DecelerateInterpolator());
            final View drawView = getDrawView(view);
            squashAnimHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float width = animValue.getWidth();
                    float height = animValue.getHeight();
                    Rect rect = new Rect((int) (bitmapWidth - width), (int) (bitmapHeight - height), (int) (bitmapWidth - (bitmapWidth - width)), (int) (bitmapHeight - (bitmapHeight - height)));
                    drawable.setBounds(rect);
                    drawView.invalidate();
                }
            });
            AnimatorSet bouncer = new AnimatorSet();
            bouncer.play(squashAnimWidth).with(squashAnimHeight);
            bouncer.start();
        }
    }

    private static View getDrawView(View view) {
        Drawable drawable = null;
        if (view instanceof ViewGroup) {
            ViewGroup gounp = (ViewGroup) view;
            for (int i = 0; i < gounp.getChildCount(); i++) {
                drawable = getChildViewDrawable(gounp.getChildAt(i));
                if (null != drawable) {
                    view = gounp.getChildAt(i);
                    break;
                }
            }
        }
        return view;
    }

    private static Drawable getViewDrawable(View view) {
        Drawable drawable = null;
        if (view instanceof ViewGroup) {
            ViewGroup gounp = (ViewGroup) view;
            for (int i = 0; i < gounp.getChildCount(); ) {
                drawable = getChildViewDrawable(gounp.getChildAt(i++));
                if (null != drawable) {
                    break;
                }
            }
        } else {
            drawable = getChildViewDrawable(view);
        }
        return drawable;
    }

    private static Drawable getChildViewDrawable(final View view) {
        Drawable drawable = null;
        if (view instanceof TextView) {
            Drawable[] drawables = ((TextView) view).getCompoundDrawables();
            for (int i = 0; i < drawables.length; i++) {
                if (null != drawables[i]) {
                    drawable = drawables[i];
                    break;
                }
            }
            if (null == drawable) {
                drawable = view.getBackground();
            }
        } else if (view instanceof ImageView) {
            drawable = ((ImageView) view).getDrawable();
        } else {
            drawable = view.getBackground();
        }
        return drawable;
    }

    public static void startImageAnim(View view) {
        Keyframe kf0 = Keyframe.ofFloat(0.2f, 360);
        Keyframe kf1 = Keyframe.ofFloat(0.5f, 30);
        Keyframe kf2 = Keyframe.ofFloat(0.8f, 1080);
        Keyframe kf3 = Keyframe.ofFloat(1f, 0);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2, kf3);
        PropertyValuesHolder scale = PropertyValuesHolder.ofKeyframe("scale", kf0, kf1, kf2, kf3);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(ObjectAnimator.ofPropertyValuesHolder(view, pvhRotation)).with(ObjectAnimator.ofPropertyValuesHolder(view, scale));
        animationSet.setDuration(2000);
        animationSet.start();
    }

    public static int evaluate(float fraction, int startValue, int endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24);
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24);
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) | (int) ((startR + (int) (fraction * (endR - startR))) << 16) | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }

    public static void startFractionAnim(ValueAnimator.AnimatorUpdateListener listener) {
        startFractionAnim(0, ValueAnimator.INFINITE, 800, listener);
    }

    public static void startFractionAnim(int count, int mode, int duration, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setRepeatCount(count);
        valueAnimator.setRepeatMode(mode);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.start();
    }

    public static void startIntAnimator(final TextView textView, final int resId, int startValue) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(startValue);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (-1 != resId) {
                    textView.setText(App.getResString(resId, valueAnimator.getAnimatedValue()));
                } else {
                    textView.setText(valueAnimator.getAnimatedValue().toString());
                }
            }
        });
        valueAnimator.start();
    }

    public static void startIntAnimator(TextView textView, int startValue) {
        startIntAnimator(textView, -1, startValue);
    }

}
