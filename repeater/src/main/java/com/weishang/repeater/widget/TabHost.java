package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.utils.UnitUtils;


/**
 * Created by momo on 2015/3/8.
 */
public class TabHost extends FixedGridLayout implements ViewPager.OnPageChangeListener {
    private int mColor;//默认颜色
    private int mSelectColor;//选中颜色
    private int mPadding;//spec对象内边距
    private int mTextSize;//标签文字大小
    private int mPosition;//选中位置
    private int mItemSize;//条目大小
    private ViewPager mPager;
    private OnTabItemClickListener mListener;
    private ViewPager.OnPageChangeListener mPageChangeListener;

    public TabHost(Context context) {
        this(context, null);
    }

    public TabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        mItemSize = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
        setCellWidth(mItemSize);
        setCellHeight(mItemSize);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabHost);
        setColor(a.getColor(R.styleable.TabHost_th_color, Color.WHITE));
        setSelectColor(a.getColor(R.styleable.TabHost_th_select_color, Color.BLUE));
        setSpecPadding((int) a.getDimension(R.styleable.TabHost_th_padding, UnitUtils.dip2px(context, 8)));
        setTextSize((int) a.getDimension(R.styleable.TabHost_th_text_size, 13));
        a.recycle();
    }

    /**
     * 设置文字选中颜色
     *
     * @param color
     */
    private void setColor(int color) {
        this.mColor = color;
        notifyDataChange();
    }

    /**
     * 设置选中颜色
     *
     * @param selectColor
     */
    public void setSelectColor(int selectColor) {
        this.mSelectColor = selectColor;
        notifyDataChange();
    }

    /**
     * 设置spec条目内边距
     *
     * @param padding 内边距
     */
    public void setSpecPadding(int padding) {
        this.mPadding = padding;
        notifyDataChange();
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        notifyDataChange();
    }

    /**
     * 刷新spec数据样式
     */
    private void notifyDataChange() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof TextView) {
                TextView textSpec = ((TextView) childView);
                //重置text Spec的颜色取值
                textSpec.setTextColor(i == mPosition ? mSelectColor : mColor);
                textSpec.setTextSize(mTextSize);
            } else if (childView instanceof ImageView) {
                //重置image Spec的内边距值
                ((ImageView) childView).setPadding(mPadding, mPadding, mPadding, mPadding);
            }
        }
    }

    /**
     * 添加文字与图片spec
     *
     * @param text          提示文字
     * @param drawableResId 图片引用
     */
    public void addTextSpec(String text, int drawableResId) {
        Context context = getContext();
        TextView tabSpec = new TextView(context);
        tabSpec.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        tabSpec.setCompoundDrawablesWithIntrinsicBounds(0, drawableResId, 0, 0);
        tabSpec.setTextColor(mColor);
        tabSpec.setTextSize(mTextSize);
        tabSpec.setText(text);
        addView(tabSpec, new LayoutParams(mItemSize, mItemSize));
        setOnSpecClickListener(tabSpec);
    }

    /**
     * 添加图片spec
     *
     * @param drawableResId
     */
    public void addImageSpec(int drawableResId) {
        Resources resources = getResources();
        addImageSpec(resources.getDrawable(drawableResId));
    }

    /**
     * 添加图片spec
     *
     * @param drawable
     */
    public void addImageSpec(Drawable drawable) {
        Context context = getContext();
        ImageView tabSpec = new ImageView(context);
        tabSpec.setPadding(mPadding, mPadding, mPadding, mPadding);
        tabSpec.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tabSpec.setImageDrawable(drawable);
        addView(tabSpec, new LayoutParams(mItemSize, mItemSize));
        setOnSpecClickListener(tabSpec);
    }

    /**
     * 获得spec总数
     *
     * @return 总数
     */
    public int getCount() {
        return getChildCount();
    }

    /**
     * 设置spec点击事件
     *
     * @param tabSpec
     */
    private void setOnSpecClickListener(View tabSpec) {
        if (null != tabSpec) {
            tabSpec.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = indexOfChild(view);
                    if (position != mPosition) {
                        if (null != mListener) {
                            setSelectPosition(position, true);
                            setSelectPosition(mPosition, false);
                            mListener.onTabItemClick(view, position, mPosition);
                            mPosition = position;
                        }
                        if (null != mPager) {
                            mPager.setCurrentItem(position, false);
                        }
                    }
                }
            });
        }
    }


    /**
     * 设置指定tab选中tab
     *
     * @param position 指定tab
     * @param isSelect 当前view是否选中
     */
    public void setSelectPosition(int position, boolean isSelect) {
        View selectView = getChildAt(position);
        if (selectView instanceof TextView) {
            //之所以不用colorStateDrawable,是因为用颜色偏移,可以实现颜色渐变,
            ((TextView) selectView).setTextColor(isSelect ? mSelectColor : mColor);
        } else if (selectView instanceof ImageView) {
            ((ImageView) selectView).setSelected(isSelect);
        }
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;
        this.mPager.setOnPageChangeListener(this);
    }

    /**
     * 设置页滑动监听
     *
     * @param listener 滑动监听对象
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mPageChangeListener = listener;
    }

    /**
     * 设置tab条目点击事件
     *
     * @param listener 点击回调对象
     */
    public void setOnTabItemClickListener(OnTabItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mPosition = position;
        setSpecScroll(position, positionOffset);
        if (null != mPageChangeListener) {
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    /**
     * 设置spec滑动
     *
     * @param position       滑动位置
     * @param positionOffset 滑动偏移量
     */
    private void setSpecScroll(int position, float positionOffset) {
        int childCount = getChildCount();
        if (position < childCount) {
            View lastView = null;
            View selectView = null;
            if (position + 1 < childCount) {
                lastView = getChildAt(position + 1);
            }
            selectView = getChildAt(position);
            if (null != lastView) {
                // 颜色回退
                setViewColor(lastView, AnimationUtils.evaluate(1f - positionOffset, mSelectColor, mColor));
            }
            // 颜色选中
            setViewColor(selectView, AnimationUtils.evaluate(1f - positionOffset, mColor, mSelectColor));
        }
        // 当位置完成移动完成之后,重置各个tab颜色,否则颜色会显示不正常
        if (mPosition == position && 0 == positionOffset) {
            for (int i = 0; i < childCount; i++) {
                setSelectPosition(i, i == position);
            }
        }
    }

    private void setViewColor(View view, int color) {
        if (view instanceof TextView) {
            TextView textView = ((TextView) view);
            textView.setTextColor(color);
            Drawable[] compoundDrawables = textView.getCompoundDrawables();
            if (null != compoundDrawables && null != compoundDrawables[1]) {
                compoundDrawables[1].setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        } else if (view instanceof ImageView) {
            ((ImageView) view).getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (null != mPageChangeListener) {
            mPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (null != mPageChangeListener) {
            mPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * tab条目点击监听回调接口
     */
    public interface OnTabItemClickListener {
        /**
         * tab条目点击事件
         *
         * @param v        点击view对象
         * @param position 点击位置
         */
        void onTabItemClick(View v, int position, int lastPosition);
    }
}
