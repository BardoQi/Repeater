package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.listener.SimpleAnimatorListener;
import com.weishang.repeater.utils.UnitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * gridLayout,并附带选择状态
 *
 * @author momo
 * @version 5:实现当布局不满一行的时候,自定义条目排版方向
 * @Date 2015/2/11
 */
public class FixedGridLayout extends ViewGroup {
    //排版方向
    private static final int CENTER = 0;//之所以默认居中,是当控件在编排不满1行时,控件自动居中效果应用较多.非常实用.而无须使用LinearLayout setGrayvity(center)编排;
    private static final int LEFT = 1;

    private static final int ANIMATOR_TIME = 300;
    private static final int DEFAULT_PADDING = 2;
    private static final int DEFAULT_CELL_WIDTH = 60;
    private static final int DEFAULT_CELL_HEIGHT = 40;
    private ArrayList<Rect> mRects;// 所有控件布局矩阵集
    private boolean mShowAddAnim;// 显示控件添加自身动画
    private boolean mShowRemoveAnim;// 显示控件删除自身动画
    private int mCellWidth;
    private int mCellHeight;
    private int mGravity;//自定义排版方向
    private int mPadding;// 条目上下间隔
    private int mViewPadding;// 条目左右边距
    private int mColumns;// 行数
    private int mRaw;//列数
    private boolean isRemoving;//正在删除中

    public FixedGridLayout(Context context) {
        this(context, null, 0);
    }

    public FixedGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
    }

    public FixedGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedGridLayout);
        setCellWidth((int) a.getDimension(R.styleable.FixedGridLayout_fg_cellwidth, 0));
        setCellHeight((int) a.getDimension(R.styleable.FixedGridLayout_fg_cellheight, UnitUtils.dip2px(context, DEFAULT_CELL_HEIGHT)));
        setShowAddAnim(a.getBoolean(R.styleable.FixedGridLayout_fg_add_anim, true));
        setShowRemoveAnim(a.getBoolean(R.styleable.FixedGridLayout_fg_remove_anim, true));
        setItemPadding((int) a.getDimension(R.styleable.FixedGridLayout_fg_padding, UnitUtils.dip2px(context, DEFAULT_PADDING)));
        setItemGravity(a.getInt(R.styleable.FixedGridLayout_fg_gravity, CENTER));
        setRaw(a.getInteger(R.styleable.FixedGridLayout_rl_raw, 0));
        a.recycle();
    }

    /**
     * 设置条目排版方向
     *
     * @param gravity 使用 {@link FixedGridLayout#CENTER/LEFT/CENTER }
     */
    public void setItemGravity(int gravity) {
        this.mGravity = gravity;
        requestLayout();
    }

    public void setShowAddAnim(boolean showAddAnim) {
        this.mShowAddAnim = showAddAnim;
    }

    public void setShowRemoveAnim(boolean showRemoveAnim) {
        this.mShowRemoveAnim = showRemoveAnim;
    }

    public void setCellWidth(int cellWidth) {
        mCellWidth = cellWidth;
        requestLayout();
    }

    public void setCellHeight(int cellHeight) {
        mCellHeight = cellHeight;
        requestLayout();
    }

    /**
     * 设置条目上下间距
     *
     * @param padding
     */
    public void setItemPadding(int padding) {
        this.mPadding = padding;
        requestLayout();
    }

    private void setRaw(int raw) {
        this.mRaw = raw;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (0 != mRaw) {
            int rawWidth = width / mRaw;
            //当rawWidth小于指定的width,时,则取cellWidth,因为存在指定值太大,而raw等比例分后,超出的现象
            if (rawWidth < mCellWidth) {
                mCellWidth = rawWidth;
            }
        }
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellWidth, MeasureSpec.EXACTLY);
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellHeight, MeasureSpec.EXACTLY);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            child.measure(cellWidthSpec, cellHeightSpec);
        }
        int comnlumCount = 0 != mRaw ? mRaw : widthSize / mCellWidth;
        int minCount = count % comnlumCount == 0 ? (count / comnlumCount) : count / comnlumCount + 1;
        setMeasuredDimension(resolveSize(mCellWidth * minCount, widthMeasureSpec), resolveSize(mCellHeight * minCount + mPadding * (minCount + 1), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mRects = getMeasureRect(getChildCount());
        if (!mRects.isEmpty()) {
            Rect rect = null;
            int size = mRects.size();
            for (int i = 0; i < size; i++) {
                rect = mRects.get(i);
                getChildAt(i).layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }

    }

    /**
     * 获得计算后的矩阵组
     *
     * @param childCount
     * @return
     */
    public ArrayList<Rect> getMeasureRect(int childCount) {
        int cellWidth = mCellWidth;
        int cellHeight = mCellHeight;
        int width = getWidth();
        mColumns = 0 != mRaw ? mRaw : width / mCellWidth;
        // 当单行个数大于总个数时,以总个数为主
        mColumns = (mColumns > childCount) ? childCount : mColumns;
        if (mColumns < 0) {
            mColumns = 1;
        }
        int x = 0, y = mPadding, i = 0;
        ArrayList<Rect> rects = new ArrayList<Rect>();
        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();
            switch (mGravity) {
                case CENTER:
                    // 单个条目居中
                    mViewPadding = ((width - (w * mColumns)) / (mColumns + 1));
                    break;
                case LEFT:
                    int columns = width / cellWidth;
                    mViewPadding = (width - cellWidth * columns) / (columns + 1);
                    break;
            }
            rects.add(new Rect(x + mViewPadding, y, x + w + mViewPadding, y + h));
            if (i >= (mColumns - 1)) {
                i = 0;
                x = 0;
                y += (cellHeight + mPadding);
            } else {
                i++;
                x += cellWidth + mViewPadding;
            }
        }
        return rects;
    }

    @Override
    public void addView(final View child, final int index) {
        addView(child, index, false);
    }

    /**
     * 添加view时,动态设定是否显示动画
     *
     * @param child
     * @param index
     * @param showAnim
     */
    public void addView(final View child, final int index, boolean showAnim) {
        FixedGridLayout.super.addView(child, index);
        if (mShowAddAnim && showAnim) {
            if (-1 == index) {
                // addView(view) anim动画
                addLastViewAnim(child);
            } else {
                addViewAnim(child, index);
            }
        }
    }

    /**
     * 添加控件动画
     *
     * @param child
     * @param index
     */
    private void addViewAnim(final View child, final int index) {
        // 先将view设置未添加view前位置,再复原.然后alpha出来添加view
        int translationX, translationY;
        int childCount = getChildCount();
        ArrayList<Rect> oldRects = getMeasureRect(childCount - 1);
        ArrayList<Rect> newRects = getMeasureRect(childCount);
        View childView = null;
        Rect oldRect, newRect;
        // 当只有一个时,不需要执行动画
        ViewHelper.setAlpha(child, 1 != childCount ? 0f : 1f);
        // 动画结果监听
        SimpleAnimatorListener listener = new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ViewPropertyAnimator.animate(child).setDuration(ANIMATOR_TIME).alpha(1f);
            }
        };
        for (int i = index + 1; i < childCount; i++) {
            childView = getChildAt(i);
            oldRect = oldRects.get(i - 1);
            newRect = newRects.get(i);
            translationX = oldRect.left - newRect.left;
            translationY = oldRect.top - newRect.top;
            ViewHelper.setTranslationX(childView, translationX);
            ViewHelper.setTranslationY(childView, translationY);
            ViewPropertyAnimator.animate(childView).translationX(0).translationY(0).setDuration(ANIMATOR_TIME).setListener(i == (childCount - 1) ? listener : null);
        }
    }

    /**
     * 添加根view的动画
     *
     * @param child
     */
    private void addLastViewAnim(final View child) {
        int width = getWidth();
        int childCount = getChildCount();
        ArrayList<Rect> oldRects = getMeasureRect(childCount - 1);
        ArrayList<Rect> rects = getMeasureRect(childCount);
        Rect rect = rects.get(childCount - 1);
        ViewHelper.setAlpha(child, 0f);
        ViewHelper.setTranslationX(child, width - rect.left);
        View childView = null;
        Rect oldRect, newRect;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            if (i != (childCount - 1)) {
                oldRect = oldRects.get(i);
                newRect = rects.get(i);
                ViewHelper.setTranslationX(childView, oldRect.left - newRect.left);
            }
            ViewPropertyAnimator.animate(childView).alpha(1f).translationX(0).setDuration(ANIMATOR_TIME);
        }
    }

    @Override
    public void removeView(final View view) {
        if (!mShowRemoveAnim) {
            FixedGridLayout.super.removeView(view);
        } else {
            //当用户点击删除速度特别快的时候,使用一个标记,判断正在删除.
            // TODO 使用阻塞队列删除
            if (!isRemoving) {
                removeViewAnim(view);
            }
        }
    }

    @Override
    public void removeViewAt(int index) {
        removeView(getChildAt(index));
    }

    /**
     * 移除view动画
     *
     * @param view
     */
    private void removeViewAnim(final View view) {
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
                Rect rect = null;
                Rect newRect = null;
                int translationX, translationY;
                // 当前的布局排版位置
                ArrayList<Rect> rects = getMeasureRect(childCount);
                // 预排版删除后的控件位置
                ArrayList<Rect> newRects = getMeasureRect(childCount - 1);
                //没有控件时,直接删除
                if (newRects.isEmpty()) {
                    FixedGridLayout.super.removeView(view);
                    return;
                }
                for (int i = 0; i < childCount; i++) {
                    childView = getChildAt(i);
                    final int finalIndex = i;
                    // 布局之前
                    rect = rects.get(i);
                    // 布局之前
                    if (i < index) {
                        newRect = newRects.get(i);
                    } else if (i > (index - 1)) {
                        newRect = newRects.get(i - 1 < 0 ? 0 : i - 1);
                    }
                    translationX = newRect.left - rect.left;
                    translationY = newRect.top - rect.top;
                    ViewPropertyAnimator.animate(childView).translationX(translationX).translationY(translationY).setDuration(ANIMATOR_TIME).setListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            View childView = getChildAt(finalIndex);
                            if (null != childView) {
                                ViewHelper.setTranslationX(childView, 0);
                                ViewHelper.setTranslationY(childView, 0);
                            }
                            if (finalIndex == (childCount - 1)) {
                                FixedGridLayout.super.removeView(view);
                                isRemoving = false;
                            }
                        }
                    });
                }
            }
        });
    }

    public int getItemWidth() {
        return mCellWidth + mViewPadding;
    }

    public int getItemHeight() {
        return mCellHeight + mPadding;
    }

    /**
     * 获得行个数
     *
     * @return
     */
    public int getColumns() {
        return mColumns;
    }

    public List<Rect> getLayoutRects() {
        return mRects;
    }
}
