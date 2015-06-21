package com.weishang.repeater.translation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by momo on 2015/3/24.
 * 组群动画执行对象
 */
public class TranslationHelper {
    private static final SparseArray<ArrayList<TranslationValue>> mTranslationValues;

    static {
        mTranslationValues = new SparseArray<ArrayList<TranslationValue>>();
    }

    public static void preTranslation(Object object, int layoutId) {
        if (null == object) return;
        View contentView = getContentView(object);
        ArrayList<TranslationValue> animValues = getAnimValues(layoutId);
        if (null != animValues && !animValues.isEmpty()) {
            ViewTranslation.preStartAnims(contentView, animValues);
        }
    }

    /**
     * 开始群动画
     *
     * @param object 执行动画fragment,或view体
     */
    public static void startTranslation(Object object, int layoutId) {
        if (null == object) return;
        final View contentView = getContentView(object);
        final ArrayList<TranslationValue> animValues = getAnimValues(layoutId);
        if (null != animValues && !animValues.isEmpty()) {
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewTranslation.startAnims(contentView, animValues, true);
                }
            });
        }
    }

    /**
     * 开始结束群动画
     *
     * @param object 执行动画fragment,或view体
     */
    public static void endTranslation(Object object, int layoutId) {
        if (null == object) return;
        View contentView = getContentView(object);
        ArrayList<TranslationValue> animValues = getAnimValues(layoutId);
        if (null != animValues && !animValues.isEmpty()) {
            ViewTranslation.startEndAnims(contentView, animValues);
        }
    }

    private static View getContentView(Object object) {
        View contentView = null;
        if (object instanceof Fragment) {
            contentView = ((Fragment) object).getView();
        } else if (object instanceof Activity) {
            contentView = getContentView(((Activity) object));
        } else if (object instanceof View) {
            //dialog 或者自定义view可以直接使用
            contentView = (View) object;
        }
        return contentView;
    }


    /**
     * 获得activity contentView;
     *
     * @param activity
     * @return
     */
    private static View getContentView(Activity activity) {
        View contentView = null;
        View decorView = activity.getWindow().getDecorView();
        if (decorView instanceof ViewGroup) {
            ViewGroup decorViewGroup = (ViewGroup) decorView;
            int childCount = decorViewGroup.getChildCount();
            if (0 < childCount) {
                View childView = decorViewGroup.getChildAt(0);
                if (childView instanceof ViewGroup) {
                    ViewGroup childViewGroup = ((ViewGroup) childView);
                    childCount = childViewGroup.getChildCount();
                    contentView = childViewGroup.getChildAt(childCount - 1);
                }
            }
        }
        return contentView;
    }

    /**
     * 获得当前布局体的动画体集合
     * \
     *
     * @param layout
     * @return
     */
    public static ArrayList<TranslationValue> getAnimValues(int layout) {
        ArrayList<TranslationValue> animValues = new ArrayList<TranslationValue>();
        ArrayList<TranslationValue> values = mTranslationValues.get(layout);
        if (null == values) {
            values = new LayoutReader(layout).read();
        }
        if (null != values) {
            animValues.addAll(values);
        }
        //以权重排序 从大到小
        Collections.sort(animValues, new Comparator<TranslationValue>() {
            @Override
            public int compare(TranslationValue translationValue, TranslationValue translationValue2) {
                return translationValue2.weight - translationValue.weight;
            }
        });
        return animValues;
    }
}
