package com.weishang.repeater.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.listener.SimpleAnimatorListener;

/**
 * 自定义ViewGounp数据列表,为解决设置自定义addView removeView动画问题
 *
 * @author momo
 * @Date 2015/2/28
 */
public class TransitionLinearLayout extends LinearLayout {
    private static final int ANIMATOR_TIME = 300;

    public TransitionLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransitionLinearLayout(Context context) {
        super(context);
    }

    private boolean isRemoving;//正在删除中

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        addLastViewAnim(child);
    }

    /**
     * 添加根view的动画
     *
     * @param child
     */
    private void addLastViewAnim(final View child) {
        ViewHelper.setAlpha(child, 0f);
        ViewHelper.setTranslationX(child, getWidth());
        ViewPropertyAnimator.animate(child).alpha(1f).translationX(0).setDuration(ANIMATOR_TIME);
    }

    @Override
    public void removeViewAt(int index) {
        removeView(getChildAt(index));
    }

    @Override
    public void removeView(View view) {
        removeViewAnim(view);
    }

    /**
     * 移除view动画
     *
     * @param view
     */
    private void removeViewAnim(final View view) {
        if (isRemoving) return;
        ViewPropertyAnimator.animate(view).setDuration(ANIMATOR_TIME).alpha(0f).setListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isRemoving = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                View childView = null;
                int index = indexOfChild(view);
                final int childCount = getChildCount();
                int translationY = 0;
                for (int i = 0; i < childCount; i++) {
                    childView = getChildAt(i);
                    final int finalIndex = i;
                    if (i > index) {
                        translationY = -childView.getHeight();
                    }
                    ViewPropertyAnimator.animate(childView).translationY(translationY).setDuration(ANIMATOR_TIME).setListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            View childView = getChildAt(finalIndex);
                            if (null != childView) {
                                ViewHelper.setTranslationX(childView, 0);
                                ViewHelper.setTranslationY(childView, 0);
                                if (finalIndex == (childCount - 1)) {
                                    TransitionLinearLayout.super.removeView(view);
                                    isRemoving = false;
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
