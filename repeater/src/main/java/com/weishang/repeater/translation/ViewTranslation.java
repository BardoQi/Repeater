package com.weishang.repeater.translation;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/13.
 * 动画属性记录及执行器
 */
public class ViewTranslation {
    private static final int ANIM_DURATION = 300;
    //动画方向
    public static final int LEFT = 0x00;
    public static final int TOP = 0x01;
    public static final int RIGHT = 0x02;
    public static final int BOTTOM = 0x03;
    public static final int NEAR = 0x04;

    //动画模式
    public static final int ALL = 0x00;
    public static final int OWN = 0x01;
    public static final int INCREASE = 0x02;
    public static final int ALL_RVS = 0x03;//整体相反


    //动画属性
    public static final String ALPHA = "alpha";
    public static final String TRANSLATION_X = "translationX";
    public static final String TRANSLATION_Y = "translationY";
    public static final String SCALE_X = "scaleX";
    public static final String SCALE_Y = "scaleY";
    public static final String ROTATION = "rotation";
    public static final String ROTATION_X = "rotationX";
    public static final String ROTATION_Y = "rotationY";

    //动画值与属性映射集
    private static final ArrayList<String> mTranslationProperties;

    static {
        mTranslationProperties = new ArrayList<String>();
        mTranslationProperties.add(ALPHA);
        mTranslationProperties.add(TRANSLATION_X);
        mTranslationProperties.add(TRANSLATION_Y);
        mTranslationProperties.add(SCALE_X);
        mTranslationProperties.add(SCALE_Y);
        mTranslationProperties.add(ROTATION);
        mTranslationProperties.add(ROTATION_X);
        mTranslationProperties.add(ROTATION_Y);
    }


    public static void startAnim(View view, int mode, int anim, int gravity) {
        if (null == view) return;
        TranslationValue translationValue = new TranslationValue(false, 300, gravity, mode, anim);
        AnimatorSet animatorSet = getAnimatorSet(view, translationValue, true);
        if (null != animatorSet) {
            animatorSet.start();
        }
    }

    /**
     * 获得起始属性动画
     *
     * @param view
     * @param translationValues
     * @return
     */
    private static AnimatorSet getStartAnimatorSet(View view, ArrayList<TranslationValue> translationValues, boolean isStart) {
        int length = translationValues.size();
        AnimatorSet lastAnimatorSet = null;
        AnimatorSet allAnimators = new AnimatorSet();
        for (int i = 0; i < length; i++) {
            //克隆一个对象体,用来作直接操作,并不改动之前对象的取值
            TranslationValue translationValue = translationValues.get(i).clone();
            translationValue.duration = 0;//动画时间为零,让其直接生效
            translationValue.mode = ALL;//整体
            View findView = view.findViewById(translationValue.id);
            if (null != findView) {
                AnimatorSet animatorSet = getAnimatorSet(findView, translationValue, true);
                if (null != lastAnimatorSet) {
                    allAnimators.play(lastAnimatorSet).with(animatorSet);
                }
                lastAnimatorSet = animatorSet;
            }
        }
        return allAnimators;
    }

    public static void preStartAnims(View view, ArrayList<TranslationValue> translationValues) {
        if (null == translationValues) return;
        AnimatorSet startAnimatorSet = getStartAnimatorSet(view, translationValues, true);
        startAnimatorSet.start();
    }

    public static void startAnims(final View view, ArrayList<TranslationValue> translationValues, boolean isStart) {
        if (null == translationValues) return;
        AnimatorSet startAnimatorSet = getStartAnimatorSet(view, translationValues, isStart);
        int length = translationValues.size();
        int lastWeight = -1;
        AnimatorSet lastAnimatorSet = null;
        final AnimatorSet allAnimators = new AnimatorSet();
        for (int i = 0; i < length; i++) {
            TranslationValue translationValue = translationValues.get(i);
            View findView = view.findViewById(translationValue.id);
            if (null != findView) {
                AnimatorSet animatorSet = getAnimatorSet(findView, translationValue, !isStart);
                if (null != lastAnimatorSet) {
                    if (lastWeight == translationValue.weight) {
                        allAnimators.play(lastAnimatorSet).with(animatorSet);
                    } else {
                        allAnimators.play(lastAnimatorSet).before(animatorSet);
                    }
                } else {
                    allAnimators.play(startAnimatorSet).before(animatorSet);
                }
                lastWeight = translationValue.weight;
                lastAnimatorSet = animatorSet;
            }
        }
        allAnimators.start();
    }

    public static void startEndAnims(View view, ArrayList<TranslationValue> translationValues) {
        if (null == translationValues) return;
        int length = translationValues.size();
        int lastWeight = -1;
        AnimatorSet lastAnimatorSet = null;
        AnimatorSet allAnimators = new AnimatorSet();
        for (int i = 0; i < length; i++) {
            TranslationValue translationValue = translationValues.get(i);
            View findView = view.findViewById(translationValue.id);
            if (null != findView) {
                AnimatorSet animatorSet = getAnimatorSet(findView, translationValue, true);
                if (null != lastAnimatorSet) {
                    if (lastWeight == translationValue.weight) {
                        allAnimators.play(lastAnimatorSet).with(animatorSet);
                    } else {
                        allAnimators.play(lastAnimatorSet).before(animatorSet);
                    }
                }
                lastWeight = translationValue.weight;
                lastAnimatorSet = animatorSet;
            }
        }
        allAnimators.start();
    }

    public static void startAnim(View view, TranslationValue translationValue) {
        if (null == view || null == translationValue) return;
        AnimatorSet animatorSet = getAnimatorSet(view, translationValue, true);
        if (null != animatorSet) {
            animatorSet.start();
        }
    }

    private static AnimatorSet getAnimatorSet(View view, TranslationValue translationValue, boolean isStart) {
        AnimatorSet animatorSet = null;
        switch (translationValue.mode) {
            case ALL:
                animatorSet = getAnimatorSet(view, translationValue, isStart, false, false);
                break;
            case OWN:
                animatorSet = getAnimatorSet(view, translationValue, isStart, false, false);
                break;
            case INCREASE:
                animatorSet = getAnimatorSet(view, translationValue, isStart, false, true);
                break;
            case ALL_RVS:
                animatorSet = getAnimatorSet(view, translationValue, isStart, true, true);
                break;
        }
        return animatorSet;
    }


    private static AnimatorSet getAnimatorSet(View view, TranslationValue translationValue, boolean isStart, boolean isRvs, boolean isDelayed) {
        //构成动画组
        AnimatorSet animatorSet = new AnimatorSet();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            ValueAnimator lastAnimator = null;
            int childCount = viewGroup.getChildCount();
            for (int i = isRvs ? childCount - 1 : 0; isRvs ? i >= 0 : i < childCount; i = isRvs ? i - 1 : i + 1) {
                View childView = viewGroup.getChildAt(i);
                ValueAnimator valueAnimator = getViewAnimator(childView, translationValue, isDelayed ? 100 : 0, isStart);
                if (null != lastAnimator) {
                    animatorSet.play(valueAnimator).with(lastAnimator);
                } else if(1==childCount){
                    //当只有一个时,直接执行
                    animatorSet.play(valueAnimator);
                }
                lastAnimator = valueAnimator;
            }
        } else {
            ValueAnimator valueAnimator = getViewAnimator(view, translationValue, 0, isStart);
            animatorSet.play(valueAnimator);
        }
        return animatorSet;
    }

    /**
     * 执行单个view动画
     *
     * @param view             执行动画控件
     * @param translationValue 执行动画属性集
     */
    private static ValueAnimator getViewAnimator(View view, TranslationValue translationValue, int startDelay, boolean isStart) {
        ArrayList<String> animPropertyNames = getAnimPropertyNames(translationValue.anim);
        int size = animPropertyNames.size();
        PropertyValuesHolder[] holders = new PropertyValuesHolder[size];
        for (int i = 0; i < size; i++) {
            String propertyName = animPropertyNames.get(i);
            float animatorValue = getAnimatorValue(view, propertyName, translationValue, isStart);
            holders[i] = PropertyValuesHolder.ofFloat(propertyName, animatorValue);
        }
        ValueAnimator valueAnimator = ObjectAnimator.ofPropertyValuesHolder(view, holders);
        valueAnimator.setDuration(translationValue.duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setStartDelay(startDelay);
        return valueAnimator;
    }

    /**
     * 获得动画取值
     *
     * @param propertyName 属性名称
     * @return 动画执行值
     */
    private static float getAnimatorValue(View view, String propertyName, TranslationValue translationValue, boolean isStart) {
        float value = 0f;
        Resources resources = view.getContext().getResources();
        float screenWidth = resources.getDisplayMetrics().widthPixels;
        float screenHeight = resources.getDisplayMetrics().heightPixels;
        if (TRANSLATION_X.equals(propertyName)) {
            //动态取值
            value = isStart ? getTranslationByGravity(view, screenWidth, screenHeight, translationValue.gravity)[0] : 0;
        } else if (TRANSLATION_Y.equals(propertyName)) {
            value = isStart ? getTranslationByGravity(view, screenWidth, screenHeight, translationValue.gravity)[1] : 0;
        } else {
            Pair<Float, Float> propertyValue = translationValue.getPropertyValue(propertyName);
            if (null != propertyValue) {
                value = isStart ? propertyValue.first : propertyValue.second;
            }
        }
        return value;
    }

    /**
     * 获得执行动画属性集
     *
     * @param anim 动画参数
     * @return 动属属性名称集
     */
    public static ArrayList<String> getAnimPropertyNames(int anim) {
        ArrayList<String> mPropertyNames = new ArrayList<String>();
        int size = mTranslationProperties.size();
        for (int i = 0, a = 1; i < size; i++, a *= 2) {
            if (anim == (a | anim)) {
                String propertyName = mTranslationProperties.get(i);
                if (!TextUtils.isEmpty(propertyName)) {
                    mPropertyNames.add(propertyName);
                }
            }
        }
        return mPropertyNames;
    }

    /**
     * 移动控件组动画
     *
     * @param viewGroup 控件组
     * @param mode      移动模式   @see {@link ViewTranslation#ALL,ViewTranslation#OWN,ViewTranslation#INCREASE}
     * @param gravity   移动方向 @see {@link ViewTranslation#LEFT,ViewTranslation#TOP,ViewTranslation#RIGHT,ViewTranslation#BOTTOM,ViewTranslation#NEAR}
     * @param duration  个体移动时间
     * @param isReverse 是否置反
     */
    public static void startTransilate(ViewGroup viewGroup, int mode, int gravity, long duration, boolean isReverse) {
        switch (mode) {
            case ALL:
                startAllTranslate(viewGroup, gravity, duration, isReverse, false);
                break;
            case OWN:
                startOwnTranslate(viewGroup, gravity, duration, isReverse);
                break;
            case INCREASE:
                startAllTranslate(viewGroup, gravity, duration, isReverse, true);
                break;
        }
    }

    /**
     * 移动自身
     *
     * @param viewGroup 控件组
     * @param gravity   移动方向
     * @param duration  移动时间
     * @param isReverse 是否复原
     */
    private static void startOwnTranslate(ViewGroup viewGroup, int gravity, long duration, boolean isReverse) {
        Resources resources = viewGroup.getContext().getResources();
        float screenWidth = resources.getDisplayMetrics().widthPixels;
        float screenHeight = resources.getDisplayMetrics().heightPixels;
        //移动自身动画
        float[] translations = getTranslationByGravity(viewGroup, screenWidth, screenHeight, gravity);
        ViewPropertyAnimator.animate(viewGroup).translationX(isReverse ? 0 : translations[0]).translationY(isReverse ? 0 : translations[1]).setDuration(duration);
    }

    /**
     * 移动整体
     *
     * @param viewGroup  控件组
     * @param gravity    移动方向
     * @param duration   移动时间
     * @param isReverse  是否复原
     * @param isIncrease 是否递增移动
     */
    private static void startAllTranslate(ViewGroup viewGroup, int gravity, long duration, boolean isReverse, boolean isIncrease) {
        int childCount = viewGroup.getChildCount();
        int itemDuration = (int) (duration / childCount);
        int width = viewGroup.getWidth();
        int height = viewGroup.getHeight();
        //控件动画状态
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            float[] translation = getTranslationByGravity(childView, width, height, gravity);
            ViewPropertyAnimator.animate(childView).translationX(isReverse ? 0 : translation[0]).translationY(isReverse ? 0 : translation[1]).setDuration(itemDuration).setStartDelay(isIncrease ? itemDuration / 2 * i : 0);
        }
    }


    /**
     * 根据上下左右获得移动位置
     *
     * @param view         移动view
     * @param parentWidth  父容器宽
     * @param parentHeight 父容器高
     * @param gravity      移动方向
     * @return translation 相对移动的x,y坐标
     */
    private static float[] getTranslationByGravity(View view, float parentWidth, float parentHeight, int gravity) {
        float[] translations = new float[2];
        int width = view.getWidth();
        int height = view.getHeight();
        int left = view.getLeft();
        int top = view.getTop();
        int right = view.getRight();
        int bottom = view.getBottom();
        switch (gravity) {
            case LEFT:
                translations[0] = -left - width;
                break;
            case RIGHT:
                translations[0] = parentWidth - left;
                break;
            case TOP:
                translations[1] = -top - height;
                break;
            case BOTTOM:
                translations[1] = parentHeight - top;
                break;
            case NEAR:
                //求出最小的一边
                float parentRight = parentWidth - right;
                float parentBottom = parentHeight - bottom;

                float horizontal = Math.min(left, parentRight);
                float vertical = Math.min(top, parentBottom);

                int horizontalDuration = (left < parentRight) ? LEFT : RIGHT;
                int verticalDuration = (top < parentBottom) ? TOP : BOTTOM;
                translations = getTranslationByGravity(view, parentWidth, parentHeight, horizontal <= vertical ? horizontalDuration : verticalDuration);
                break;
            default:
                break;
        }
        return translations;
    }
}
